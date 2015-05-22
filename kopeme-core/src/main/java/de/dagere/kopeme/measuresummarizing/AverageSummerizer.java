package de.dagere.kopeme.measuresummarizing;

import java.util.List;

/**
 * A MeasureSummarizer for the average.
 * 
 * @author dagere
 *
 */
public final class AverageSummerizer implements MeasureSummarizer {

	@Override
	public long getValue(final List<Long> values) {
		if (values != null && values.size() != 0) {
			long sum = 0;
			for (Long l : values) {
				sum += l;
			}
			return sum / values.size();
		}
		else {
			return 0;
		}
	}
}
