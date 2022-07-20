package de.dagere.kopeme.datacollection.tempfile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.kopemedata.Fulldata;
import de.dagere.kopeme.kopemedata.MeasuredValue;

public class WrittenResultReaderBin implements TempfileReader {

   private static final Logger LOG = LogManager.getLogger(WrittenResultReaderBin.class);

   private File file;
   protected List<Map<String, Long>> realValues = null;
   protected List<Long> iterationStartTimes = null;
   protected Map<String, Number> finalValues = null;
   protected Map<String, SummaryStatistics> collectorSummaries = null;
   private Map<Integer, String> collectorsIndexed;

   public WrittenResultReaderBin(final File file) {
      this.file = file;
   }

   @Override
   public void read(final Throwable exception, final Set<String> datacollectors) {
      initSummaries(datacollectors);
      readValues();
      checkValues(exception);
   }

   private void checkValues(final Throwable exception) {
      LOG.debug("Count of iterations: {}  Values: {}", iterationStartTimes.size(), realValues.size());
      if (iterationStartTimes.size() != realValues.size()) {
         throw new RuntimeException("Count of iterations is wrong, expected: " + iterationStartTimes.size() + " but got " + realValues.size(), exception);
      }
   }

   @Override
   public void readStreaming(final Set<String> keys) {
      finalValues = new HashMap<>();
      collectorsIndexed = new LinkedHashMap<>();
      initSummaries(keys);

      try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file))) {
         // String line;
         Map<String, Long> currentValues = new HashMap<>();

         readDataCollectors(reader);

         byte[] executionStartLine = new byte[Long.BYTES];
         
         while (reader.available() > 0) {
            // ignore executionstarts when streaming
            reader.read(executionStartLine);
            for (String collector : collectorsIndexed.values()) {
               long value = readLong(reader);
               collectorSummaries.get(collector).addValue(value);
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

   private void readDataCollectors(BufferedInputStream reader) throws IOException {
      int index = 0;
      char firstByte = (char) reader.read();

      while (firstByte != '\n') {
         if (firstByte != '=') {
            throw new RuntimeException("Broken format, expected = but was " + firstByte);
         }

         String dataCollectorName = readUntilSign(reader, '\n');
         collectorsIndexed.put(index++, dataCollectorName);
         
         firstByte = (char) reader.read();
      }
   }

   public static final String readUntilSign(BufferedInputStream reader, char separationSign) throws IOException {
      final List<Byte> bytes = new LinkedList<>();
      byte current;
      while ((current = (byte) reader.read()) != separationSign) {
         bytes.add(current);
      }
      final byte[] collectorNameBytes = ArrayUtils.toPrimitive(bytes.toArray(new Byte[0]));
      String dataCollectorName = new String(collectorNameBytes);
      return dataCollectorName;
   }

   private void initSummaries(final Set<String> datacollectors) {
      collectorSummaries = new HashMap<>();
      for (String datacollector : datacollectors) {
         collectorSummaries.put(datacollector, new SummaryStatistics());
      }
   }

   private void readValues() {
      realValues = new ArrayList<>();
      iterationStartTimes = new ArrayList<>();
      finalValues = new HashMap<>();
      collectorsIndexed = new HashMap<>();

      try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file))) {
         // String line;
         Map<String, Long> currentValues = new HashMap<>();

         readDataCollectors(reader);

         while (reader.available() > 0) {
            // ignore executionstarts when streaming
            long startTime = readLong(reader);
            currentValues = finishIteration(currentValues);
            iterationStartTimes.add(startTime);
            
            for (String collector : collectorsIndexed.values()) {
               long value = readLong(reader);
               currentValues.put(collector, value);
               collectorSummaries.get(collector).addValue(value);
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

   private final byte[] longBytes = new byte[Long.BYTES];
   private final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
   
   private long readLong(BufferedInputStream reader) throws IOException {
      buffer.clear();
      reader.read(longBytes);
      buffer.put(longBytes);
      buffer.flip();
      long value = buffer.getLong();
      return value;
   }

   @Override
   public Fulldata createFulldata(final int warmup, final String currentDatacollector) {
      Fulldata result = new Fulldata();
      for (int i = warmup; i < realValues.size(); i++) {
         final Long executionStartTime = iterationStartTimes.get(i);
         final Long value = realValues.get(i).get(currentDatacollector);
         final MeasuredValue fulldataValue = new MeasuredValue();
         fulldataValue.setStartTime(executionStartTime);
         fulldataValue.setValue(value);
         result.getValues().add(fulldataValue);
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

   @Override
   public SummaryStatistics getCollectorSummary(final String collectorName) {
      return collectorSummaries.get(collectorName);
   }

   @Override
   public List<Map<String, Long>> getRealValues() {
      return realValues;
   }

   @Override
   public List<Long> getExecutionStartTimes() {
      return iterationStartTimes;
   }

   @Override
   public Map<String, Number> getFinalValues() {
      return finalValues;
   }

   @Override
   public void clear(final String key) {
      if (realValues != null) {
         for (int i = 0; i < realValues.size(); i++) {
            realValues.get(i).remove(key);
         }
      }
   }

   @Override
   public void deleteTempFile() {
      if (!file.delete()) {
         System.out.println("Warning: File " + file.getAbsolutePath() + " could not be deleted, existing: " + file.exists() + "!");
      }
   }
}
