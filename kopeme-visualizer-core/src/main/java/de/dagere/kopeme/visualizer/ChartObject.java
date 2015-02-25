package de.dagere.kopeme.visualizer;

import org.jfree.chart.JFreeChart;

public class ChartObject {
	private final String datacollectorName;
	private final String testClassName;
	private final String testMethodName;
	private JFreeChart chart;

	public ChartObject(String datacollectorName, String testClassName, String testMethodName, JFreeChart chart) {
		super();
		this.datacollectorName = datacollectorName;
		this.testClassName = testClassName;
		this.testMethodName = testMethodName;
		this.chart = chart;
	}

	public JFreeChart getChart() {
		return chart;
	}

	public void setChart(JFreeChart chart) {
		this.chart = chart;
	}

	public String getDatacollectorName() {
		return datacollectorName;
	}

	public String getTestClassName() {
		return testClassName;
	}

	public String getTestMethodName() {
		return testMethodName;
	}

	public String getOutputFilename() {
		return testMethodName + "_" + datacollectorName + ".png";
	}

}
