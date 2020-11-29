package kieker.monitoring.writer.filesystem.aggregateddata;

import com.fasterxml.jackson.annotation.JsonCreator;

public class AggregatedDataNode {

   private final int eoi, ess;
   private final String call;

   @JsonCreator
   public AggregatedDataNode(final int eoi, final int ess, final String call) {
      this.eoi = eoi;
      this.ess = ess;
      this.call = call;
   }

   @Override
   public int hashCode() {
      final int hashCode = eoi + ess + call.hashCode();
      return hashCode;
   }

   @Override
   public boolean equals(final Object other) {
      if (other instanceof AggregatedDataNode) {
         final AggregatedDataNode node = (AggregatedDataNode) other;
         return eoi == node.eoi && ess == node.ess && call.equals(node.call);
      }
      return false;
   }

   @Override
   public String toString() {
      return eoi + "_" + ess + "_" + call;
   }

   public int getEoi() {
      return eoi;
   }

   public int getEss() {
      return ess;
   }

   public String getCall() {
      return call;
   }
}