package de.dagere.kopeme.visualizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
import org.jfree.chart.ChartUtilities;

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

		visualizeFile(filename, width, height);
	}

	public static void visualizeFile(String filename, int width, int height) throws JAXBException {
		File inputFile = new File(filename);

		XMLDataLoader xdl = new XMLDataLoader(inputFile);
		Map<Long, Integer> sizes = getSizes(xdl);
		List<ChartObject> charts;
		if (sizes.size() != 0) {
			charts = ThroughputVisualizer.createSizeGraphs(xdl, width, height, sizes);
		} else {
			charts = TrendVisualizer.createNormalGraphs(xdl, width, height);
		}

		try {
			for (ChartObject chart : charts) {
				ChartUtilities
						.writeBufferedImageAsPNG(new FileOutputStream(chart.getOutputFilename()), chart.getChart().createBufferedImage(width, height));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Map<Long, Integer> getSizes(XMLDataLoader xdl) {
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
		return sizes;
	}
}
