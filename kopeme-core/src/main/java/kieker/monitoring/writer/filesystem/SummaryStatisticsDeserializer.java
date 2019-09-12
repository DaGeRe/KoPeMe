package kieker.monitoring.writer.filesystem;

import java.io.IOException;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.stat.descriptive.StatisticalSummaryValues;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class SummaryStatisticsDeserializer extends StdDeserializer<StatisticalSummary> {

   public SummaryStatisticsDeserializer() {
      super(SummaryStatisticsDeserializer.class);
   }

   @Override
   public StatisticalSummary deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
      final JsonNode node = p.getCodec().readTree(p);
      if (node.isTextual()) {
         return new SummaryStatistics();
      } else {
         final double mean = node.get("mean").numberValue().doubleValue();
         final double deviation = node.get("standardDeviation").numberValue().doubleValue();
         final int n = node.get("N").numberValue().intValue();
         return new StatisticalSummaryValues(mean, deviation * deviation, n, Double.MAX_VALUE, 0, mean * n);
      }
   }
}