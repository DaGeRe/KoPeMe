package de.kopeme.datacollection;

import java.util.Date;
import java.util.Map;

import org.hyperic.sigar.Sigar;

public abstract class DataCollector {
//	protected Map<Date, Long> historicalData;

	protected Sigar sigar;

	public String getName() {
		return this.getClass().getName();
	}

	public abstract void startCollection();

	public abstract void stopCollection();

	public abstract long getValue();

//	public void setHistoricalData(Map<Date, Long> historicalData) {
//		this.historicalData = historicalData;
//	}
//
//	public Map<Date, Long> getHistoricalData() {
//		return historicalData;
//	}
}
