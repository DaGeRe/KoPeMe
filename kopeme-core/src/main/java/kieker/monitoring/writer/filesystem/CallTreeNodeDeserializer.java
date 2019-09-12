package kieker.monitoring.writer.filesystem;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class CallTreeNodeDeserializer extends KeyDeserializer {
   // @Override
   // public CallTreeNode deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
   // final String value = p.getText();
   // final int eoiSeparatorIndex = value.indexOf("_");
   // final int eoi = Integer.parseInt(value.substring(0, eoiSeparatorIndex));
   // final String withoutEoi = value.substring(eoiSeparatorIndex);
   // final int essSeparatorIndex = withoutEoi.indexOf("_");
   // final int ess = Integer.parseInt(withoutEoi.substring(0, essSeparatorIndex));
   // final String call = withoutEoi.substring(essSeparatorIndex);
   // return new CallTreeNode(eoi, ess, call);
   // }

   @Override
   public Object deserializeKey(final String value, final DeserializationContext ctxt) throws IOException {
      final int eoiSeparatorIndex = value.indexOf("_");
      final int eoi = Integer.parseInt(value.substring(0, eoiSeparatorIndex));
      final String withoutEoi = value.substring(eoiSeparatorIndex + 1);
      final int essSeparatorIndex = withoutEoi.indexOf("_");
      final int ess = Integer.parseInt(withoutEoi.substring(0, essSeparatorIndex));
      final String call = withoutEoi.substring(essSeparatorIndex);
      return new CallTreeNode(eoi, ess, call);
   }
}