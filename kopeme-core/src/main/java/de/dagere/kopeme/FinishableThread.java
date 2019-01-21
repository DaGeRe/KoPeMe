package de.dagere.kopeme;

/**
 * A thread that could be told that it is finished. This state should stay the same, and should not be cleared like it is done with the interrupt state.
 * 
 * @author reichelt
 *
 */
public class FinishableThread extends Thread {

   private final Finishable finishable;

   public FinishableThread(final ThreadGroup experimentThreadGroup, final Finishable finishable, final String name) {
      super(experimentThreadGroup, finishable, name);
      this.finishable = finishable;
   }

   public boolean isFinished() {
      return finishable.isFinished();
   }

   public void setFinished(final boolean isFinished) {
      finishable.setFinished(isFinished);
   }

}
