package de.dagere.kopeme.measuresummarizing;

import java.util.Arrays;
import java.util.List;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

/**
 * A MeasureSummarizer for the average.
 * 
 * @author dagere
 *
 */
public final class AverageSummerizer implements MeasureSummarizer {

   public static void main(String[] args) {
      System.out.println(new AverageSummerizer().getValue(Arrays.asList(5L, 6L)));
   }
   
	@Override
	public Number getValue(final List<Long> values) {
		if (values != null && values.size() != 0) {
			double sum = 0;
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
