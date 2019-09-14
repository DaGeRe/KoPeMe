package kieker.monitoring.writer.filesystem.aggregateddata;

import com.fasterxml.jackson.annotation.JsonCreator;

public class CallTreeNode {

   private final int eoi, ess;
   private final String call;

   @JsonCreator
   public CallTreeNode(final int eoi, final int ess, final String call) {
      super();
      this.eoi = eoi;
      this.ess = ess;
      this.call = call;
   }

   @Override
   public int hashCode() {
      final int hashCode = eoi + ess + call.hashCode();
      if (call.equals("public void NonExistant.method1()")) {
         System.out.println(hashCode);
      }
      return hashCode;
   }

   @Override
   public boolean equals(final Object other) {
      if (other instanceof CallTreeNode) {
         final CallTreeNode node = (CallTreeNode) other;
         if (node.getCall().equals("public void NonExistant.method1()") || call.equals("public void NonExistant.method1()")) {
            System.out.println("test");
         }
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