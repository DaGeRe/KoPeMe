package de.dagere.kopeme.junit.exampletests.runner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.junit3.KoPeMeTestcase;

/**
 * Tests whether KoPeMe is finished after Timeout
 * Attention: KoPeMe finishes the test after a timeout by System.exit, if kieker is running, since a the Kieker-Monitoring thread
 * could be hurt by Thread.stop. Therefore, unit testing a situation with timeout is currently not possible.
 * @author reichelt
 *
 */
public class TimeoutTestJUnit3 extends KoPeMeTestcase {
   private final static Logger LOG = LogManager.getLogger(TimeoutTestJUnit3.class);

   public void testFirst() {
      LOG.info("First");
      forceWaiting(20000);
      LOG.info("First End");
   }

   private void forceWaiting(int duration) {
      long start = System.currentTimeMillis();
      while (System.currentTimeMillis() < start + duration) {
         try {
            Thread.sleep(100);
         } catch (InterruptedException e) {

         }
      }
   }

   public void testSecond() {
      LOG.info("Second");
      forceWaiting(10000);
      LOG.info("Second End");
   }

   public void testThird() {
      LOG.info("Third");
      forceWaiting(10000);
      LOG.info("Third End");
   }

   @Override
   protected int getWarmupExecutions() {
      return 0;
   }

   @Override
   protected int getExecutionTimes() {
      return 1;
   }

   @Override
   protected boolean logFullData() {
      return false;
   }

   @Override
   protected boolean useKieker() {
      return false;
   }

   @Override
   protected long getMaximalTime() {
      return 900;
   }

}
