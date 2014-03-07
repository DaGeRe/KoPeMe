package de.dagere.kopeme.measuresummarizing;

import java.util.List;

/**
 * Calculates from some measures the one measure,
 * which should be saved
 * @author dagere
 *
 */
public interface MeasureSummarizer {
	
	/**
	 * Returns the value of the list of measures, that
	 * should be saved
	 * @param values
	 * @return
	 */
	public long getValue(List<Long> values);
}
