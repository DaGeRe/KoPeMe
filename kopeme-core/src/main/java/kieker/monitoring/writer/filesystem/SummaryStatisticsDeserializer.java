package kieker.monitoring.writer.filesystem;

import java.io.IOException;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class SummaryStatisticsDeserializer extends StdDeserializer<SummaryStatistics> {

   public SummaryStatisticsDeserializer() {
      super(SummaryStatisticsDeserializer.class);
   }

   @Override
   public SummaryStatistics deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
      return null;
   }
}