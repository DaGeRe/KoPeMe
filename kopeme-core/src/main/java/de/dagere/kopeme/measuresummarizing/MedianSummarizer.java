package de.dagere.kopeme.measuresummarizing;

import java.util.Arrays;
import java.util.List;

/**
 * A summarizer returning the medium of a measure.
 * 
 * @author reichelt
 *
 */
public final class MedianSummarizer implements MeasureSummarizer {

	@Override
	public long getValue(final List<Long> values) {
		Long[] longarray = values.toArray(new Long[0]);
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
