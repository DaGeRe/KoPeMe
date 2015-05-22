package de.dagere.kopeme.measuresummarizing;

import java.util.List;

/**
 * Returns the maximum of the List of measures.
 * 
 * @author dagere
 */
public final class MaximumSummarizer implements MeasureSummarizer {

	@Override
	public long getValue(final List<Long> values) {
		long max = 0;
		for (Long l : values) {
			if (l > max) max = l;
		}
		return max;
	}
}
