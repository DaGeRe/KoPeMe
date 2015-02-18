package de.dagere.kopeme.visualizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;

public class VisualizationGenerator {

	private static final String PERFORMANCEFILE = "performancefile";
	private static final Logger log = LogManager.getLogger(VisualizationGenerator.class);

	public static void main(String args[]) throws JAXBException, ParseException {
		Options options = new Options();
		options.addOption(OptionBuilder.isRequired(true).hasArg().create(PERFORMANCEFILE));
		options.addOption(OptionBuilder.isRequired(false).hasArg().create("width"));
		options.addOption(OptionBuilder.isRequired(false).hasArg().create("height"));

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = parser.parse(options, args);

		String filename = cmd.getOptionValue(PERFORMANCEFILE);
		int width = Integer.parseInt(cmd.getOptionValue("width", "600"));
		int height = Integer.parseInt(cmd.getOptionValue("width", "600"));

		log.info("Loading file: " + filename);

		File inputFile = new File(filename);

		creatGraphs(inputFile, width, height);
	}

	public static void creatGraphs(File file, int width, int height) throws JAXBException {
		XMLDataLoader xdl = new XMLDataLoader(file);

		Map<Long, Integer> sizes = new HashMap<Long, Integer>();

		for (TestcaseType tc : xdl.getFullData().getTestcases().getTestcase()) {
			for (Datacollector dc : tc.getDatacollector()) {
				if (dc.getName().equals("size")) {
					for (Result r : dc.getResult()) {
						final int value = Integer.parseInt(r.getValue());
						sizes.put(r.getDate(), value);
						log.trace("Size: {} Date: {}", value, r.getDate());
					}
				}
			}
		}

		for (TestcaseType tc : xdl.getFullData().getTestcases().getTestcase()) {
			for (Datacollector dc : tc.getDatacollector()) {
				if (!dc.getName().equals("size")) {
					final XYSeries ts = new XYSeries(dc.getName());
					for (Result r : dc.getResult()) {

						final int value = Integer.parseInt(r.getValue());
						log.trace("Date: {}", r.getDate());
						final Integer count = sizes.get(r.getDate());
						if (count != null) {
							log.trace("Count: {} Measured Value: {}", count, value);
							ts.add(count, (Number) value);
						}
					}
					final XYSeriesCollection data = new XYSeriesCollection(ts);
					final JFreeChart chart = ChartFactory.createXYLineChart(
							"Performanzverlauf",
							"Einheiten",
							dc.getName(),
							data,
							PlotOrientation.VERTICAL,
							true,
							true,
							false);
					try {
						ChartUtilities
								.writeBufferedImageAsPNG(new FileOutputStream(new File(tc.getName() + "_" + dc.getName() + ".png")), chart.createBufferedImage(width, height));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}
}
