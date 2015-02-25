package de.dagere.kopeme.visualizer;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;

public class TrendVisualizer {
	public static List<ChartObject> createNormalGraphs(XMLDataLoader xdl, int width, int height) throws JAXBException {
		List<ChartObject> charts = new LinkedList<>();
		for (TestcaseType tc : xdl.getFullData().getTestcases().getTestcase()) {
			for (Datacollector dc : tc.getDatacollector()) {
				if (!dc.getName().equals("size")) {
					final XYSeries ts = new XYSeries(dc.getName());
					for (Result r : dc.getResult()) {

						final int value = Integer.parseInt(r.getValue());
						ts.add((Number) r.getDate(), (Number) value);
					}
					final XYSeriesCollection data = new XYSeriesCollection(ts);
					final JFreeChart chart = ChartFactory.createXYLineChart(
							"Performanzverlauf",
							"Zeitstempel",
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
