package de.dagere.kopeme.visualizer;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.AbstractProject;
import hudson.util.Graph;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import de.dagere.kopeme.PomProjectNameReader;
import de.dagere.kopeme.datastorage.FolderProvider;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.visualizer.data.GraphVisualizer;

//import hudson.util.

/**
 * This action visualized measured data from KoPeMe.
 * 
 * @author dagere
 * 
 */
public class VisualizeAction implements Action, Serializable {

	private static transient Logger log = Logger
			.getLogger(VisualizeAction.class.getName());
	private transient final AbstractProject project;
	private final List<String> collectorNames = new ArrayList<String>();
	private final List<String> testNames = new ArrayList<String>();

	transient KoPeMePublisher publisher;
	Map<String, GraphVisualizer> graphMap;
	int width = 800, height = 500;

	public VisualizeAction(final AbstractProject project, final KoPeMePublisher publisher) {
		// logger.log(Level.INFO, "Konstruktor Visualizeaction");
		this.project = project;
		this.publisher = publisher;
		loadData();
	}

	public AbstractProject getProject() {
		return project;
	}

	@Override
	public String getDisplayName() {
		return "Performanzmaß-Visualisierung";
	}

	@Override
	public String getIconFileName() {
		return "graph.gif";
	}

	@Override
	public String getUrlName() {
		return "Visualisierung_URL";
	}

	public List<String> getCollectorNames() {
		return collectorNames;
	}

	public List<String> getTestNames() {
		return testNames;
	}

	private void loadData() {
		log.info("VisualizeAction.loadData - Lade Daten");
		try {
			project.getLastBuild();

			if (graphMap == null)
				graphMap = new HashMap<String, GraphVisualizer>();
			final FilePath workspace = project.getSomeWorkspace();
			if (workspace != null) // prevent error, when workspace for project isn't initialized
			{
				for (FilePath testcaseName : workspace.list("**/*.yaml", "", false)) {
					log.log(Level.FINE, "Lade: " + testcaseName);
					File file = new File(testcaseName.toString());
					log.log(Level.FINE, "File: " + file + " " + file.exists());
					if (file.exists()) {
						loadFileData(testcaseName.toString(), file);
					}
				}
			} else {
				log.info("Error: Workspace == null");
			}

			String foldername = FolderProvider.getInstance().getKopemeDefaultFolder();

			log.info("Projekt: " + project.getName() + " " + project.getDisplayName());
			log.info(project.getSomeWorkspace() + File.separator + "pom.xml");
			File pomFile = new File(project.getSomeWorkspace() + File.separator + "pom.xml");
			String name = new PomProjectNameReader().getProjectName(pomFile);
			log.info("Name: " + name);

			File folder = new File(foldername + File.separator + name);
			if (folder.exists()) {
				log.info("Suche in: " + folder);
				for (Object fileObject : FileUtils.listFiles(folder, new WildcardFileFilter("*.xml"), TrueFileFilter.INSTANCE)) {
					log.log(Level.FINE, "Gefunden: " + fileObject);
					File file = (File) fileObject;
					log.log(Level.FINE, "File: " + file + " " + file.getAbsolutePath() + " " + file.exists());
					if (file.exists()) {
						loadFileData(file.getAbsolutePath(), file);
					}
				}
			} else {
				log.info("Achtung: Ordner " + folder.getAbsolutePath() + " existiert nicht.");
			}

		} catch (IOException e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (InterruptedException e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}

		log.info("VisualizeAction.loadData - Daten geladen");
	}

	private void loadFileData(final String testcaseName, final File file) {
		log.log(Level.FINE, "Lade Daten von: " + file.exists() + " " + file.getAbsolutePath());

		// lies testcases und schreibe name + visible in visibilityMap
		// ermoeglich schnelles nachschlagen
		List<GraphVisualizer> testcases = publisher.getTestcases();
		Map<String, Boolean> visibilityMap = new HashMap<String, Boolean>();
		for (GraphVisualizer gv : testcases)
			visibilityMap.put(gv.getName(), gv.isVisible());

		try {
			XMLDataLoader xdl = new XMLDataLoader(file);

			final String testName = testcaseName.substring(testcaseName.lastIndexOf(File.separator) + 1, testcaseName.lastIndexOf("."));
			if (!testNames.contains(testName))
				testNames.add(testName);

			for (String collector : xdl.getCollectors()) {
				Map<String, Map<Date, Long>> dataTemp = xdl.getData(collector);
				log.log(Level.FINE, "Daten für " + file.getAbsolutePath() + "(" + collector + ") geladen");

				final String collectorName = collector.substring(collector.lastIndexOf(".") + 1);
				final String prettyName = testName + " (" + collectorName + ")";

				if (!collectorNames.contains(collectorName)) {
					collectorNames.add(collectorName);
				}

				// nachschlagen von visible fuer aktuellen collector
				Boolean visible = visibilityMap.get(prettyName);
				// default setzen fuer den fall dass der collector noch nicht bekannt war
				if (visible == null) {
					visible = true;
				}

				// hinzufuegen der aktuellen (neu aus yaml eingelesenen) daten
				// mit korrektem (aus config gelesen oder default) visible
				log.log(Level.INFO, "Finaler Name: " + prettyName);
				graphMap.put(prettyName, new GraphVisualizer(prettyName, dataTemp, visible));
			}
		} catch (JAXBException e) {
			log.info(e.getLocalizedMessage());
			e.printStackTrace();
		}
		log.log(Level.FINE, "Laden beendet");
	}

	/**
	 * Returns an array of all Measurement-Names. This is called in config.jelly.
	 * 
	 * @return
	 */
	public String[] getMeasurements(final String file) {
		return graphMap.get(file).getMeasurements();
	}

	public boolean isVisible(final String fileName) {
		return graphMap.get(fileName).isVisible();
	}

	public String[] getAllFiles() {
		log.log(Level.FINE, "GraphMap: " + graphMap);
		String[] ret = graphMap.keySet().toArray(new String[0]);
		log.log(Level.FINE, "Loading all Files, Size: " + ret.length);
		return ret;
	}

	public void setVisible(final String name, final boolean visible) {
		log.log(Level.FINE, "Set visible: " + name + " " + visible);
	}

	public String[] getFiles() {
		List<String> files = new LinkedList<String>();
		for (Map.Entry<String, GraphVisualizer> entry : graphMap.entrySet()) {
			if (entry.getValue().isVisible()) {
				files.add(entry.getKey());
			}
		}
		String[] ret = files.toArray(new String[0]);
		return ret;
	}

	/**
	 * Creates the Graph with the measurment-data for displaying in the action.
	 * 
	 * @return
	 */
	public Graph getSummaryGraph(final String file) {
		loadData();

		log.info("VisualizeAction:getSummaryGraph - Lade Daten für: " + file);
		GraphVisualizer graphVisualizer = graphMap.get(file);
		if (graphVisualizer == null) {
			String fileTemp = "performanceresults" + File.separator + file;
			log.log(Level.FINE, "VisualizeAction:getSummaryGraph - Lade Daten für: " + fileTemp);
			graphVisualizer = graphMap.get(fileTemp);
		}
		if (graphVisualizer == null) {
			final String fileTemp = file.replace("^", File.separator);
			log.log(Level.FINE, "VisualizeAction:getSummaryGraph - Lade Daten für: " + fileTemp);
			graphVisualizer = graphMap.get(fileTemp);
		}
		if (graphVisualizer == null) {
			for (Map.Entry<String, GraphVisualizer> entry : graphMap.entrySet()) {
				log.log(Level.FINE, "Name: " + entry.getKey() + " " + entry.getKey().endsWith(file));
				if (entry.getKey().endsWith(file)) {
					graphVisualizer = entry.getValue();
					break;
				}
			}
		}
		if (graphVisualizer == null) {
			log.info("Fehler: " + file + " nicht vorhanden");
			for (String key : graphMap.keySet()) {
				log.info("Key vorhanden: " + key);
			}
			return null;
		}
		Map<String, Map<Date, Long>> subMap = graphVisualizer.getDatamap();

		JFreeChart chart = null;

		log.info("In Graphmap ist: " + graphMap.size());

		int i = 0;
		for (String viewable : graphVisualizer.getDatamap().keySet()) {
			log.info("Erstelle Graph für " + viewable);

			TimeSeriesCollection collection = new TimeSeriesCollection();
			TimeSeries serie = new TimeSeries(viewable, Minute.class);
			log.log(Level.FINE, "Suche Eintrag für " + viewable);
			for (Map.Entry<Date, Long> entry : subMap.get(viewable).entrySet()) {
				serie.addOrUpdate(new Minute(entry.getKey()),
						entry.getValue());
			}
			collection.addSeries(serie);
			// collection.
			if (i == 0) {
				chart = ChartFactory.createTimeSeriesChart("Chart", "Zeit",
						"Wert", collection, true, true, false);
			} else {
				XYPlot plot = chart.getXYPlot();
				plot.setDataset(i, collection);
				plot.setRenderer(i, new StandardXYItemRenderer());
			}
			i++;

		}
		log.info("Graph geladen");

		chart.setBackgroundPaint(Color.white);

		final JFreeChart chart2 = chart;

		return new Graph(-1, width, height) {

			@Override
			protected JFreeChart createGraph() {
				return chart2;
			}
		};
	}

	public List<GraphVisualizer> getVisualizer() {
		List<GraphVisualizer> list = new LinkedList<GraphVisualizer>();
		list.addAll(graphMap.values());
		return list;
	}
}
