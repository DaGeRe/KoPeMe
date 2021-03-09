package de.dagere.kopeme.datacollection.tempfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.generated.Result.Fulldata;
import de.dagere.kopeme.generated.Result.Fulldata.Value;

public class WrittenResultReader {

   private static final Logger LOG = LogManager.getLogger(WrittenResultReader.class);

   public static final String EXECUTIONSTART = "e:";
   public static final String COLLECTOR = "c:";
   public static final String FINAL_VALUE = "f:";
   public static final String COLLECTOR_INDEX = "i:";

   private File file;
   protected List<Map<String, Long>> realValues = null;
   protected List<Long> executionStartTimes = null;
   protected Map<String, Number> finalValues = null;
   protected Map<String, SummaryStatistics> collectorSummaries = null;
   private Map<Integer, String> collectorsIndexed;

   public WrittenResultReader(final File file) {
      this.file = file;
   }

   public void read(final Throwable exception, final Set<String> keys) {
      initSummaries(keys);
      readValues();
      checkValues(exception);
   }

   private void checkValues(final Throwable exception) {
      LOG.debug("Count of executions: {}  Values: {}", executionStartTimes.size(), realValues.size());
      if (executionStartTimes.size() != realValues.size()) {
         throw new RuntimeException("Count of executions is wrong, expected: " + executionStartTimes.size() + " but got " + realValues.size(), exception);
      }
   }

   public void readStreaming(final Throwable thrownException, final Set<String> keys) {
      finalValues = new HashMap<>();
      collectorsIndexed = new HashMap<>();
      initSummaries(keys);

      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
         String line;
         Map<String, Long> currentValues = new HashMap<>();
         while ((line = reader.readLine()) != null) {
            if (line.startsWith(COLLECTOR_INDEX)) {
               String collectorString = line.substring(COLLECTOR_INDEX.length());
               String[] values = collectorString.split("=");
               collectorsIndexed.put(Integer.parseInt(values[0]), values[1]);
            } else if (line.contains("=")) {
               String[] values = line.split("=");
               int collectorIndex = Integer.parseInt(values[0]);
               String collector = collectorsIndexed.get(collectorIndex);
               collectorSummaries.get(collector).addValue(Long.parseLong(values[1]));
            } else {
               // ignore executionstarts when streaming
            }
         }
         finishIteration(currentValues);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      for (String key : keys) {
         finalValues.put(key, collectorSummaries.get(key).getMean());
      }
   }

   private void initSummaries(final Set<String> keys) {
      collectorSummaries = new HashMap<>();
      for (String key : keys) {
         collectorSummaries.put(key, new SummaryStatistics());
      }
   }

   private void readValues() {
      realValues = new ArrayList<>();
      executionStartTimes = new ArrayList<>();
      finalValues = new HashMap<>();
      collectorsIndexed = new HashMap<>();

      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
         String line;
         Map<String, Long> currentValues = new HashMap<>();
         while ((line = reader.readLine()) != null) {
            if (line.startsWith(COLLECTOR_INDEX)) {
               String collectorString = line.substring(COLLECTOR_INDEX.length());
               String[] values = collectorString.split("=");
               collectorsIndexed.put(Integer.parseInt(values[0]), values[1]);
            } else if (line.contains("=")) {
               String[] values = line.split("=");
               int collectorIndex = Integer.parseInt(values[0]);
               String collector = collectorsIndexed.get(collectorIndex);
               currentValues.put(collector, Long.parseLong(values[1]));
               collectorSummaries.get(collector).addValue(Long.parseLong(values[1]));
            }  else {
               currentValues = finishIteration(currentValues);
               Long start = Long.parseLong(line);
               executionStartTimes.add(start);
            }
         }
         finishIteration(currentValues);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      if (realValues.size() > 0) {
         for (String key : realValues.get(0).keySet()) {
            finalValues.put(key, collectorSummaries.get(key).getMean());
         }
      }
   }

   public Fulldata createFulldata(final int warmup, final String currentDatacollector) {
      Fulldata result = new Fulldata();
      for (int i = warmup; i < realValues.size(); i++) {
         final Long executionStartTime = executionStartTimes.get(i);
         final Long value = realValues.get(i).get(currentDatacollector);
         final Value fulldataValue = new Value();
         fulldataValue.setStart(executionStartTime);
         fulldataValue.setValue(value);
         result.getValue().add(fulldataValue);
      }
      return result;
   }

   private Map<String, Long> finishIteration(Map<String, Long> currentValues) {
      if (!currentValues.isEmpty()) {
         realValues.add(currentValues);
         currentValues = new HashMap<>();
      }
      return currentValues;
   }

   public SummaryStatistics getCollectorSummary(final String collectorName) {
      return collectorSummaries.get(collectorName);
   }

   public List<Map<String, Long>> getRealValues() {
      return realValues;
   }

   public List<Long> getExecutionStartTimes() {
      return executionStartTimes;
   }

   public Map<String, Number> getFinalValues() {
      return finalValues;
   }

   public void clear(final String key) {
      if (realValues != null) {
         for (int i = 0; i < realValues.size(); i++) {
            realValues.get(i).remove(key);
         }
      }
   }

   public void deleteTempFile() {
      if (!file.delete()) {
         System.out.println("Warning: File " + file.getAbsolutePath() + " could not be deleted, existing: " + file.exists() + "!");
      }
   }
}
