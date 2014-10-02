package de.dagere.kopeme.datastorage;

import java.util.List;

public interface DataStorer {
	void storeValue(String name, long value);

	void storeData();

	void storeValue(PerformanceDataMeasure performanceDataMeasure, List<Long> values);
}
