package de.dagere.kopeme.measuresummarizing;

import java.util.Arrays;
import java.util.List;

public class MedianSummarizer implements MeasureSummarizer {

	/**
	 * Returns the median of the List of measures
	 * @return median of the liste of measures
	 */
	@Override
	public long getValue(List<Long> values) {
		Long longarray[] = values.toArray(new Long[0]);
		Arrays.sort(longarray);
		int middle = ((longarray.length) / 2);
		long median;
		if (longarray.length % 2 == 0) {
			long medianA = longarray[middle];
			long medianB = longarray[middle - 1];
			median = (medianA + medianB) / 2;
		} else {
			median = longarray[middle + 1];
		}
		return median;
	}

}
