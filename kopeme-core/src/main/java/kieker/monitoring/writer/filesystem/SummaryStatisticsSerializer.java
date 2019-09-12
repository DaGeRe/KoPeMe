package kieker.monitoring.writer.filesystem;

import java.io.IOException;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class SummaryStatisticsSerializer extends StdSerializer<SummaryStatistics> {

   public SummaryStatisticsSerializer() {
      super(SummaryStatistics.class);
   }

   private static final long serialVersionUID = 6773506005705287342L;

   @Override
   public void serialize(final SummaryStatistics value, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
      if (value.getMean() != Double.NaN) {
         gen.writeStartObject();
         gen.writeFieldName("mean");
         gen.writeNumber(value.getMean());
         gen.writeEndObject();
      } else {
         gen.writeString("NO_STATISTICS");
      }
   }

}