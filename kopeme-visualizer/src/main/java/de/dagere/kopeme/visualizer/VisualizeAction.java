package de.dagere.kopeme.visualizer;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.AbstractProject;
import hudson.util.Graph;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.visualizer.data.DateConverter;
import de.dagere.kopeme.visualizer.data.GraphVisualizer;
import de.dagere.kopeme.visualizer.data.NormalDateConverter;
import de.dagere.kopeme.visualizer.data.Testcase;

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

	// transient Map<String, Map<String, Map<Date, Long>>> dataMap;
	transient KoPeMePublisher publisher;
	Map<String, GraphVisualizer> graphMap;
	DateConverter dateconverter;
	// Map<String, Set<String>> viewable = null;
	int width = 800, height = 500;

	public VisualizeAction(AbstractProject project, KoPeMePublisher publisher) {
		// logger.log(Level.INFO, "Konstruktor Visualizeaction");
		this.project = project;
		this.publisher = publisher;
		dateconverter = new NormalDateConverter();
		loadData();
	}

	public AbstractProject getProject() {
		return project;
	}

	public String getDisplayName() {
		return "Performanzmaß-Visualisierung";
	}

	public String getIconFileName() {
		return "graph.gif";
	}

	public String getUrlName() {
		return "Visualisierung_URL";
	}

	public DateConverter getDateConverter() {
		return dateconverter;
	}

	/**
	 * Returns, weather the measurement with the given name is viewable; called from config.jelly.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isViewable(String file, String name) {
		if (graphMap.containsKey(file))
			return graphMap.get(file).isViewable(name);
		else
			return false;
	}

	public boolean isMultipleAxis(String graph) {
		return graphMap.get(graph).isUseMultipleAxis();
	}

	public void setMultipleAxis(String graph, boolean axis) {
		graphMap.get(graph).setUseMultipleAxis(axis);
	}

	private void loadData() {
		log.info("VisualizeAction.loadData - Lade Daten");
		try {
			project.getLastBuild();

			if (graphMap == null)
				graphMap = new HashMap<String, GraphVisualizer>();

			final List<Testcase> testcases = publisher.getTestcases();
			log.info("Testcases:" + testcases);
			for (Testcase testcase : testcases) {
				log.log(Level.FINE, "Testcase: " + testcase);
				String testcaseName = testcase.getName();
				log.info("Handling " + testcaseName);
				FilePath workspace = project.getSomeWorkspace();
				if (workspace != null) // prevent error, when workspace for
										// project isn't initialized
				{
					log.info("Suche nach: " + testcaseName + " " + workspace.list().size());

					FilePath[] list = workspace.list(testcaseName);
					log.log(Level.FINE, "Gefundene Daten: " + list + " " + list.length);
					if (list != null && list.length > 0
							&& testcaseName.length() != 0) {
						File file = new File(project.getSomeWorkspace() + File.separator
								+ testcaseName);
						log.info("Lade Daten von: " + testcaseName + " "
								+ file.exists() + " " + file.getAbsolutePath());
						try {
							XMLDataLoader xdl = new XMLDataLoader(file);
							for (String collector : xdl.getCollectors()) {
								Map<String, Map<Date, Long>> temp = xdl.getData(collector);
								log.info("Daten für " + file.getAbsolutePath()
										+ "(" + collector + ") geladen");
								graphMap.put(testcaseName.substring(testcaseName.lastIndexOf(File.separator) + 1) + " (" + collector.substring(collector.lastIndexOf(".") + 1)
										+ ")",
										new GraphVisualizer(
												temp, true));
							}

						} catch (JAXBException e) {
							log.info(e.getLocalizedMessage());
							e.printStackTrace();
						}
						log.log(Level.FINE, "Laden beendet");
					} else {
						log.info("Error: No data available!");
					}
				} else {
					log.info("Error: Workspace == null");
				}
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

	/**
	 * Returns an array of all Measurement-Names. This is called in config.jelly.
	 * 
	 * @return
	 */
	public String[] getMeasurements(String file) {
		return graphMap.get(file).getMeasurements();
	}

	/**
	 * Returns an array of the viewable Measurement-Names. This is called in config.jelly.
	 * 
	 * @return
	 */
	public String[] getViewable(String file) {
		String[] ret = graphMap.get(file).getViewable();
		return ret;
	}

	public String[] getFiles() {
		String[] ret = graphMap.keySet().toArray(new String[0]);
		return ret;
	}

	public int getValueCount(String file) {
		final GraphVisualizer graphVisualizer = graphMap.get(file);
		log.info("Visualizer: " + graphVisualizer);
		log.info("Count: " + graphVisualizer.getValueCount());
		return graphVisualizer.getValueCount();
	}

	public void setValueCount(String file, int value) {
		graphMap.get(file).setValueCount(value);
	}

	/**
	 * Creates the Graph with the measurment-data for displaying in the action.
	 * 
	 * @return
	 */
	public Graph getSummaryGraph(String file) {
		loadData();

		log.info("VisualizeAction:getSummaryGraph - Lade Daten für: " + file);

		GraphVisualizer graphVisualizer = graphMap.get(file);
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
		for (String viewable : graphVisualizer.getViewable()) {
			log.info("Erstelle Graph für " + viewable + " "
					+ (dateconverter instanceof NormalDateConverter));
			if (dateconverter instanceof NormalDateConverter) {
				TimeSeriesCollection collection = new TimeSeriesCollection();
				TimeSeries serie = new TimeSeries(viewable, Minute.class);
				log.info("Suche Eintrag für " + viewable);
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
					if (isMultipleAxis(file)) {
						XYPlot plot = chart.getXYPlot();
						NumberAxis axis2 = new NumberAxis(viewable);
						plot.setRangeAxis(i, axis2);
						plot.setDataset(i, collection);
						plot.setRenderer(i, new StandardXYItemRenderer());
						plot.mapDatasetToRangeAxis(i, i);
					} else {
						XYPlot plot = chart.getXYPlot();
						plot.setDataset(i, collection);
						plot.setRenderer(i, new StandardXYItemRenderer());
					}
				}
				i++;
			} else {
				// TODO: Visualisierung mit Revisionsnummern/Tags?
			}

		}
		log.info("Graph geladen");

		chart.setBackgroundPaint(Color.white);

		// Image image = chart.createBufferedImage(width, height);

		final JFreeChart chart2 = chart;

		return new Graph(-1, width, height) {

			@Override
			protected JFreeChart createGraph() {
				return chart2;
			}
		};
	}

	/**
	 * Called, when the form is saved and the graph should be refreshed
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doSave(final StaplerRequest request,
			final StaplerResponse response) throws IOException,
			ServletException {
		String file = request.getParameter("file");
		GraphVisualizer currentGraph = graphMap.get(file);
		currentGraph.setUseMultipleAxis("on".equals(request
				.getParameter("Verschiedene Skalierungen")));
		currentGraph.setValueCount(new Integer(request.getParameter("count")));
		for (String s : currentGraph.getMeasurements()) {
			currentGraph.setViewable(s, "on".equals(request.getParameter(s)));
		}
		response.sendRedirect("../" + getUrlName());
	}
}
