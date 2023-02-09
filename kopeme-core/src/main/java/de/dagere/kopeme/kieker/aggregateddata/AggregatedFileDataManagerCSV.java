package de.dagere.kopeme.kieker.aggregateddata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.dagere.kopeme.kieker.writer.AggregatedTreeWriter;

public class AggregatedFileDataManagerCSV implements DataWriter {

   private final AggregatedTreeWriter aggregatedTreeWriter;

   private final File destinationFolder;
   private File currentDestination;
   private BufferedWriter currentWriter;
   private final Map<DataNode, WritingData> nodeMap = new ConcurrentHashMap<>();

   private int currentEntries = 0;
   private int fileIndex = 0;
   private boolean running = true;

   /**
    * @param aggregatedTreeWriter
    * @throws IOException
    */
   public AggregatedFileDataManagerCSV(final AggregatedTreeWriter aggregatedTreeWriter) throws IOException {
      this.aggregatedTreeWriter = aggregatedTreeWriter;
      this.destinationFolder = aggregatedTreeWriter.getResultFolder();
      currentDestination = new File(destinationFolder, "measurement-0.csv");
      currentWriter = new BufferedWriter(new FileWriter(currentDestination));
   }

   public void finish() {
      running = false;
   }

   @Override
   public void run() {
      while (running) {
         try {
            System.out.println("Sleeping: " + aggregatedTreeWriter.getWriteInterval());
            Thread.sleep(aggregatedTreeWriter.getWriteInterval());
         } catch (final InterruptedException e) {
            System.out.println("Writing is finished...");
         }
         if (running) {
            try {
               writeAll();
            } catch (final IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   private synchronized void writeAll() throws IOException {
      for (final Map.Entry<DataNode, WritingData> value : nodeMap.entrySet()) {
         writeLine(value);

         if (currentEntries >= aggregatedTreeWriter.getEntriesPerFile()) {
            startNextFile();
         }
      }
      currentWriter.flush();
   }

   private void writeLine(final Map.Entry<DataNode, WritingData> value) throws IOException {
      if (value.getValue().getCurrentStatistic() != null &&
            !Double.isNaN(value.getValue().getCurrentStatistic().getMean())
            && value.getValue().getCurrentStatistic().getN() != 0) {
         writeHeader(value.getKey());
         writeStatistics(value.getValue());
         currentWriter.write("\n");
         currentEntries++;
         value.getValue().persistStatistic();
      }
   }

   private void startNextFile() throws IOException {
      currentEntries = 0;
      fileIndex++;
      currentWriter.close();
      currentDestination = new File(destinationFolder, "measurement-" + fileIndex + ".csv");
      currentWriter = new BufferedWriter(new FileWriter(currentDestination));
   }

   private void writeHeader(final DataNode node) throws IOException {
      currentWriter.write(node.getCall() + ";");
      if (node instanceof AggregatedDataNode) {
         currentWriter.write(((AggregatedDataNode) node).getEoi() + ";" + ((AggregatedDataNode) node).getEss() + ";");
      } else {
         currentWriter.write("-1;-1;");
      }
   }

   private void writeStatistics(final WritingData value) throws IOException {
      currentWriter.write(value.getCurrentStart() + ";");
      currentWriter.write(value.getCurrentStatistic().getMean() + ";");
      currentWriter.write(value.getCurrentStatistic().getStandardDeviation() + ";");
      currentWriter.write(value.getCurrentStatistic().getN() + ";");
      currentWriter.write(value.getCurrentStatistic().getMin() + ";");
      currentWriter.write(value.getCurrentStatistic().getMax() + "");
   }

   public synchronized void write(final DataNode node, final long duration) {
      final WritingData data = getData(node);
      data.addValue(duration);
   }

   private WritingData getData(final DataNode node) {
      WritingData data = nodeMap.get(node);
      if (data == null) {
         data = new WritingData(currentDestination, aggregatedTreeWriter.getStatisticConfig());
         nodeMap.put(node, data);
      }
      return data;

   }

   @Override
   public void close() throws IOException {
      System.out.println("Writing finally...");
      writeAll();
      currentWriter.close();
      currentWriter = null;
   }
}