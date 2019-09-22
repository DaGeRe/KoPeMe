package kieker.monitoring.writer.filesystem.aggregateddata;

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
      super(StatisticalSummary.class);
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
         final double min = node.get("min").isNull() ? 0 : node.get("min").doubleValue();
         final double max = node.get("max").isNull() ? 0 : node.get("max").doubleValue();
         return new StatisticalSummaryValues(mean, deviation * deviation, n, max, min, mean * n);
      }
   }
}