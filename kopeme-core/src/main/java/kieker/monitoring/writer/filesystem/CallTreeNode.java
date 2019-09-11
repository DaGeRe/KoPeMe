package kieker.monitoring.writer.filesystem;

class CallTreeNode {
   int eoi, ess;
   String call;

   public CallTreeNode(final int eoi, final int ess, final String call) {
      super();
      this.eoi = eoi;
      this.ess = ess;
      this.call = call;
   }

   @Override
   public boolean equals(final Object other) {
      if (other instanceof CallTreeNode) {
         final CallTreeNode node = (CallTreeNode) other;
         return eoi == node.eoi && ess == node.ess && call.equals(node.call);
      }
      return false;
   }
}