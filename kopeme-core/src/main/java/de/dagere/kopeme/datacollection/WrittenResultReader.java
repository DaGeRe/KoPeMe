package de.dagere.kopeme.datacollection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WrittenResultReader {

   private static final Logger LOG = LogManager.getLogger(WrittenResultReader.class);

   public static final String EXECUTIONSTART = "e:";
   public static final String COLLECTOR = "c:";
   public static final String FINAL_VALUE = "f:";

   private File file;
   protected List<Map<String, Long>> realValues = null;
   protected List<Long> executionStartTimes = null;
   protected Map<String, Number> finalValues = null;

   public WrittenResultReader(File file) {
      this.file = file;
   }

   public void read(Throwable exception, Set<String> collectors) {
      readValues();
      checkValues(exception);
      calculateAverages(collectors);
   }

   private void checkValues(Throwable exception) {
      LOG.debug("Count of executions: {}  Values: {}", executionStartTimes.size(), realValues.size());
      if (executionStartTimes.size() != realValues.size()) {
         throw new RuntimeException("Count of executions is wrong, expected: " + executionStartTimes.size() + " but got " + realValues.size(), exception);
      }
   }

   private void readValues() {
      realValues = new ArrayList<>();
      executionStartTimes = new LinkedList<>();
      finalValues = new HashMap<String, Number>();

      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
         String line;
         Map<String, Long> currentValues = new HashMap<>();
         while ((line = reader.readLine()) != null) {
            if (line.startsWith(EXECUTIONSTART)) {
               currentValues = finishIteration(currentValues);
               String timeString = line.substring(EXECUTIONSTART.length());
               Long start = Long.parseLong(timeString);
               executionStartTimes.add(start);
            } else if (line.startsWith(COLLECTOR)) {
               String collectorString = line.substring(COLLECTOR.length());
               String[] values = collectorString.split("=");
               currentValues.put(values[0], Long.parseLong(values[1]));
            } else if (line.startsWith(FINAL_VALUE)) {
               
            } else {
               throw new RuntimeException("Unexpected line: " + line);
            }
         }
         finishIteration(currentValues);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      file.delete();
   }

   private Map<String, Long> finishIteration(Map<String, Long> currentValues) {
      if (!currentValues.isEmpty()) {
         realValues.add(currentValues);
         currentValues = new HashMap<>();
      }
      return currentValues;
   }

   private void calculateAverages(Set<String> collectors) {
      for (final String collectorName : collectors) {
         final Number result = getCollectorSummary(collectorName).getMean();
         finalValues.put(collectorName, result);
      }
   }
   
   SummaryStatistics getCollectorSummary(final String collectorName) {
      final SummaryStatistics stat = new SummaryStatistics();
      for (int i = 0; i < realValues.size() - 1; i++) {
         final Long collectorValue = realValues.get(i).get(collectorName);
         stat.addValue(collectorValue);
      }
      return stat;
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
}
