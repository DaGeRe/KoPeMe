package de.dagere.kopeme.visualizer;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;

public class ThroughputVisualizer {

	private static final Logger log = LogManager.getLogger(ThroughputVisualizer.class);

	public static List<ChartObject> createSizeGraphs(final XMLDataLoader xdl, final int width, final int height, final Map<Long, Integer> sizes) throws JAXBException {
		final List<ChartObject> charts = new LinkedList<>();
		for (final TestcaseType tc : xdl.getFullData().getTestcases().getTestcase()) {
			for (final Datacollector dc : tc.getDatacollector()) {
				if (!dc.getName().equals("size")) {
					final XYSeries ts = new XYSeries(dc.getName());
					for (final Result r : dc.getResult()) {

						final int value = (int) r.getValue();
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
					charts.add(new ChartObject(dc.getName(), "", tc.getName(), chart));
				}
			}
		}
		return charts;
	}
}
