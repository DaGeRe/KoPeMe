package de.dagere.kopeme.kieker;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import kieker.monitoring.writer.filesystem.StatisticConfig;
import kieker.monitoring.writer.filesystem.aggregateddata.WritingData;

public class TestStatistic {

   WritingData data;

   @Before
   public void setUp() {
      data = new WritingData(null, new StatisticConfig(15, 5));
   }

   private void addMeasurements(final int count, final long value) {
      for (int i = 0; i < count; i++) {
         data.addValue(value);
      }
   }

   @Test
   public void testNormalStatistic() throws Exception {
      addMeasurements(30, 15);
      Assert.assertEquals(15, data.getOverallStatistics().getMean(), 0.01);
   }

   @Test
   public void testWarmup() throws Exception {
      addMeasurements(15, 15);
      addMeasurements(15, 30);
      Assert.assertEquals(30, data.getOverallStatistics().getMean(), 0.01);
   }

   @Test
   public void testOutlier() throws Exception {
      addMeasurements(15, 15);
      addMeasurements(50, 10);
      addMeasurements(10, 100);
      Assert.assertEquals(10, data.getOverallStatistics().getMean(), 0.01);
   }

   @Test
   public void testPersisting() throws Exception {
      addMeasurements(15, 15);
      addMeasurements(15, 20);
      data.persistStatistic();
      addMeasurements(15, 25);
      data.persistStatistic();
      addMeasurements(15, 30);
      Assert.assertEquals(25, data.getOverallStatistics().getMean(), 0.01);
      Assert.assertEquals(3, data.getStatistic().size());
      final Iterator<Long> iterator = data.getStatistic().keySet().iterator();
      Assert.assertEquals(20, data.getStatistic().get(iterator.next()).getMean(), 0.01);
      Assert.assertEquals(25, data.getStatistic().get(iterator.next()).getMean(), 0.01);
      Assert.assertEquals(30, data.getStatistic().get(iterator.next()).getMean(), 0.01);
   }

   @Test(expected = RuntimeException.class)
   public void testName() throws Exception {
      final StatisticConfig config = new StatisticConfig(10, 0.9);
   }
}
