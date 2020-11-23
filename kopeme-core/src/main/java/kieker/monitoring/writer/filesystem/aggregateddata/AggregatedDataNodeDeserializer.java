package kieker.monitoring.writer.filesystem.aggregateddata;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class AggregatedDataNodeDeserializer extends KeyDeserializer {

   @Override
   public AggregatedDataNode deserializeKey(final String value, final DeserializationContext ctxt) throws IOException {
      final int eoiSeparatorIndex = value.indexOf("_");
      final int eoi = Integer.parseInt(value.substring(0, eoiSeparatorIndex));
      final String withoutEoi = value.substring(eoiSeparatorIndex + 1);
      final int essSeparatorIndex = withoutEoi.indexOf("_");
      final int ess = Integer.parseInt(withoutEoi.substring(0, essSeparatorIndex));
      final String call = withoutEoi.substring(essSeparatorIndex + 1);
      return new AggregatedDataNode(eoi, ess, call);
   }
}