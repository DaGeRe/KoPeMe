package de.kopeme.measuresummarizing;

import java.util.List;

/**
 * A MeasureSummarizer for the average
 * @author dagere
 *
 */
public class AverageSummerizer implements MeasureSummarizer{

	/**
	 * Returns the average of the List of measures
	 * @return average of the liste of measures
	 */
	public long getValue(List<Long> values) {
		if ( values!= null && values.size() != 0)
		{
			long sum = 0;
			for ( Long l : values ) sum+=l;
			return sum / values.size();
		}
		else
		{
			return 0;
		}
		
	}

}
