package de.dagere.kopeme.visualizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
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

	public static void main(final String args[]) throws JAXBException, ParseException {
		final Options options = new Options();
		options.addOption(OptionBuilder.isRequired(false).hasArg().create(PERFORMANCEFILE));
		options.addOption(OptionBuilder.isRequired(false).hasArg().create("width"));
		options.addOption(OptionBuilder.isRequired(false).hasArg().create("height"));

		final CommandLineParser parser = new BasicParser();
		final CommandLine cmd = parser.parse(options, args);

		final int width = Integer.parseInt(cmd.getOptionValue("width", "600"));
		final int height = Integer.parseInt(cmd.getOptionValue("width", "600"));

		if (cmd.hasOption(PERFORMANCEFILE)) {
			final String filename = cmd.getOptionValue(PERFORMANCEFILE);
			log.info("Loading file: " + filename);

			visualizeFile(filename, width, height, "");
		}
		else {
			final Collection<File> fileList = FileUtils.listFiles(new File("performanceresults"), new WildcardFileFilter("*.yaml"), FalseFileFilter.FALSE);
			for (final File filename : fileList) {
				visualizeFile(filename.getName(), width, height, "performanceresults" + File.separatorChar);
			}
		}

	}

	public static void visualizeFile(final String filename, final int width, final int height, final String outputPrefix) throws JAXBException {
		final File inputFile = new File(filename);

		final XMLDataLoader xdl = new XMLDataLoader(inputFile);
		final Map<Long, Integer> sizes = getSizes(xdl);
		List<ChartObject> charts;
		if (sizes.size() != 0) {
			charts = ThroughputVisualizer.createSizeGraphs(xdl, width, height, sizes);
		} else {
			charts = TrendVisualizer.createNormalGraphs(xdl, width, height);
		}

		try {
			for (final ChartObject chart : charts) {
				ChartUtilities
						.writeBufferedImageAsPNG(new FileOutputStream(outputPrefix + chart.getOutputFilename()), chart.getChart().createBufferedImage(width, height));
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static Map<Long, Integer> getSizes(final XMLDataLoader xdl) {
		final Map<Long, Integer> sizes = new HashMap<Long, Integer>();

		for (final TestcaseType tc : xdl.getFullData().getTestcases().getTestcase()) {
			for (final Datacollector dc : tc.getDatacollector()) {
				if (dc.getName().equals("size")) {
					for (final Result r : dc.getResult()) {
						final int value = (int) r.getValue();
						sizes.put(r.getDate(), value);
						log.trace("Size: {} Date: {}", value, r.getDate());
					}
				}
			}
		}
		return sizes;
	}
}
