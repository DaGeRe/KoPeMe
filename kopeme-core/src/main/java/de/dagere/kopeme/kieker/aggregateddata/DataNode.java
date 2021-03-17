package de.dagere.kopeme.kieker.aggregateddata;

public class DataNode {
   protected final String call;
   
   public DataNode(String call) {
      this.call = call;
   }

   @Override
   public int hashCode() {
      return call.hashCode();
   }

   @Override
   public boolean equals(final Object other) {
      if (other instanceof DataNode) {
         final DataNode node = (DataNode) other;
         return call.equals(node.call);
      }
      return false;
   }

   @Override
   public String toString() {
      return call;
   }

   public String getCall() {
      return call;
   }
}
