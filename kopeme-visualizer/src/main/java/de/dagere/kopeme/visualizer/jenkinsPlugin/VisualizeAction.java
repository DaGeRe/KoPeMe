package de.dagere.kopeme.visualizer.jenkinsPlugin;

import hudson.FilePath;
import hudson.model.Action;
import hudson.model.AbstractProject;
import hudson.model.Project;
import hudson.util.Graph;

import javax.xml.bind.JAXBException;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.ComparableObjectSeries;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.Log;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.yaml.snakeyaml.Yaml;
import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.ImageManager;
import org.apache.xmlgraphics.image.loader.impl.DefaultImageContext;
import org.apache.xmlgraphics.image.loader.impl.ImageRendered;
import org.apache.xmlgraphics.java2d.ps.EPSDocumentGraphics2D;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.DateConverter;
import de.dagere.kopeme.GraphVisualizer;
import de.dagere.kopeme.NormalDateConverter;
import de.dagere.kopeme.Testcase;

//import hudson.util.

/**
 * This action visualized measured data from KoPeMe.
 * 
 * @author dagere
 * 
 */
public class VisualizeAction implements Action, Serializable {

	private static transient Logger logger = Logger
			.getLogger(VisualizeAction.class.getName());
	private transient final AbstractProject project;

	// transient Map<String, Map<String, Map<Date, Long>>> dataMap;
	transient KoPeMePublisher publisher;
	Map<String, GraphVisualizer> graphMap;
	DateConverter dc; 
	// Map<String, Set<String>> viewable = null;
	int width = 800, height = 500;

	public VisualizeAction(AbstractProject project, KoPeMePublisher publisher) {
		// logger.log(Level.INFO, "Konstruktor Visualizeaction");
		this.project = project;
		this.publisher = publisher;
		dc = new NormalDateConverter();
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
	
	public DateConverter getDateConverter()
	{
		return dc;
	}

	/**
	 * Returns, weather the measurement with the given name is viewable; called
	 * from config.jelly.
	 * 
	 * @param name
	 * @return
	 */
	public boolean isViewable(String file, String name) {
		return graphMap.get(file).isViewable(name);
	}

	public boolean isMultipleAxis(String graph) {
		return graphMap.get(graph).isUseMultipleAxis();
	}

	public void setMultipleAxis(String graph, boolean axis) {
		graphMap.get(graph).setUseMultipleAxis(axis);
	}

	private FilePath getFilePath(String name) {
		FilePath workspace = project.getSomeWorkspace();
		if (workspace != null) // prevent error, when workspace for project
								// isn't initialized
		{
			FilePath[] list;
			try {
				list = workspace.list(name);
				if (list.length > 0)
					return list[0];
				else
					return null;
			} catch (IOException e) {
				// TODO Automatisch generierter Erfassungsblock
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Automatisch generierter Erfassungsblock
				e.printStackTrace();
			}
		} else {
			System.out.println("workspace == null");
		}
		return null;
	}

	private void loadData() {
		logger.info("Lade Daten");
		try {
			
			Yaml yaml = new Yaml();
			project.getLastBuild();

			// String file = "datei.yml";

			if (graphMap == null)
				graphMap = new HashMap<String, GraphVisualizer>();

			for (Testcase testcase : publisher.getTestcases()) {
				String testcaseName = testcase.getName();
				FilePath workspace = project.getSomeWorkspace();
				if (workspace != null) // prevent error, when workspace for
										// project isn't initialized
				{
					FilePath[] list = workspace.list(testcaseName);
					if (list != null && list.length > 0) {
						InputStream is = list[0].read();
						File file = new File(project.getSomeWorkspace() + "/" + testcaseName);
//						logger.info("Lade Daten von: " + testcaseName + " " + file.exists() + " " + file.getAbsolutePath());
						try{
							XMLDataLoader xdl = new XMLDataLoader(file);
							Map<String, Map<Date, Long>> temp = xdl.getData();
							Log.info("Daten für " + file.getAbsolutePath() + " geladen");
							graphMap.put(testcaseName, new GraphVisualizer(temp, true));
						}catch (JAXBException e){
							e.printStackTrace();
						}
						
						
//						Map<String, Map<Date, Long>> temp = (Map<String, Map<Date, Long>>) yaml
//								.load(is);
						
					}
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns an array of all Measurement-Names. This is called in
	 * config.jelly.
	 * 
	 * @return
	 */
	public String[] getMeasurements(String file) {
		return graphMap.get(file).getMeasurements();
	}

	/**
	 * Returns an array of the viewable Measurement-Names. This is called in
	 * config.jelly.
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
		return graphMap.get(file).getValueCount();
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
		
		logger.info("Lade Daten für: " + file);
		Map<String, Map<Date, Long>> subMap = graphMap.get(file).getDatamap();
		
		JFreeChart chart = null;
		
		int i = 0;
		for (String s : graphMap.get(file).getViewable()) {
			Log.info("Erstelle Graph für " + s + " " + (dc instanceof NormalDateConverter));
			if (dc instanceof NormalDateConverter)
			{
				TimeSeriesCollection collection = new TimeSeriesCollection();
				TimeSeries serie = new TimeSeries(s, Minute.class);
				logger.info("Suche Eintrag für " + s);
				for (Map.Entry<Date, Long> entry : subMap.get(s).entrySet()) {
					serie.addOrUpdate(new Minute(entry.getKey()), entry.getValue());
				}
				collection.addSeries(serie);
				if ( i == 0 )
				{
					chart = ChartFactory.createTimeSeriesChart("Chart",
							"Zeit", "Wert", collection, true, true, false);
				}
				else
				{
					if ( isMultipleAxis(file) )
					{
						XYPlot plot = chart.getXYPlot();
						NumberAxis axis2 = new NumberAxis(s);
						plot.setRangeAxis(i, axis2);
						plot.setDataset(i, collection);
						plot.setRenderer(i, new StandardXYItemRenderer());
						plot.mapDatasetToRangeAxis(i, i);
					}
					else
					{
						XYPlot plot = chart.getXYPlot();
						plot.setDataset(i, collection);
						plot.setRenderer(i, new StandardXYItemRenderer());
					}
				}
				i++;
			}
			else
			{
//				ComparableObjectSeriesCollection collection = new XYSeriesCollection();
//				ComparableObjectSeries serie = new ComparableObjectSeries(new String());
//				logger.info("Suche Eintrag für " + s);
//				for (Map.Entry<Date, Long> entry : subMap.get(s).entrySet()) {
//					serie.add("", 5);
//					serie.addOrUpdate(new Minute(entry.getKey()), entry.getValue());
//				}
//				collection.addSeries(serie);
//				if ( i == 0 )
//				{
//					chart = ChartFactory.createTimeSeriesChart("Chart",
//							"Zeit", "Wert", collection, true, true, false);
//				}
//				else
//				{
//					if ( isMultipleAxis(file) )
//					{
//						XYPlot plot = chart.getXYPlot();
//						NumberAxis axis2 = new NumberAxis(s);
//						plot.setRangeAxis(i, axis2);
//						plot.setDataset(i, collection);
//						plot.setRenderer(i, new StandardXYItemRenderer());
//						plot.mapDatasetToRangeAxis(i, i);
//					}
//					else
//					{
//						XYPlot plot = chart.getXYPlot();
//						plot.setDataset(i, collection);
//						plot.setRenderer(i, new StandardXYItemRenderer());
//					}
//				}
//				i++;
//			}
			}
			
		}
		logger.info("Graph geladen");
		
		chart.setBackgroundPaint(Color.white);
		
//		Image image = chart.createBufferedImage(width, height);
		
		final JFreeChart chart2 = chart;
//		
//		try {
//			String filename = project.getSomeWorkspace().getParent() + "/" + file + ".eps";
//			File outputFile = new File(filename);
//			FileOutputStream fos = new FileOutputStream(outputFile);
//			
//			ImageManager manager = new ImageManager(new DefaultImageContext());
//			
//			EPSDocumentGraphics2D g2d = new EPSDocumentGraphics2D(false);
//			g2d.setGraphicContext(new org.apache.xmlgraphics.java2d.GraphicContext());
//
//			//Set up the document size
//			g2d.setupDocument(fos, width, height); //400pt x 200pt
//			BufferedImage im = chart2.createBufferedImage(width, height);
//			g2d.drawRenderedImage(im, new AffineTransform());
//			
//			g2d.finish();
//			
//			
//			ImageIO.write(im, "eps", fos);
//		} catch (FileNotFoundException e) {
//			// TODO Automatisch generierter Erfassungsblock
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Automatisch generierter Erfassungsblock
//			e.printStackTrace();
//		}
		
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
