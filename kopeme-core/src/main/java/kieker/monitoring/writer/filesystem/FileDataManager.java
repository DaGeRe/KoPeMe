package kieker.monitoring.writer.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

class FileDataManager implements Runnable {

   static final ObjectMapper MAPPER = new ObjectMapper();
   static {
      MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
   }

   private final AggregatedTreeWriter aggregatedTreeWriter;
   private File destination;
   private Map<CallTreeNode, File> fileMapping = new HashMap<>();
   private Map<File, Map<CallTreeNode, AggregatedData>> fileData = new HashMap<>();
   private Set<File> changedFiles = new HashSet<>();
   private final Map<CallTreeNode, AggregatedData> nodeMap = new ConcurrentHashMap<>();

   private int currentEntries = 0;
   private int fileIndex = 0;

   /**
    * @param aggregatedTreeWriter
    */
   FileDataManager(final AggregatedTreeWriter aggregatedTreeWriter) {
      this.aggregatedTreeWriter = aggregatedTreeWriter;
      destination = new File(aggregatedTreeWriter.getResultFolder(), "measurement-0.json");
      fileData.put(destination, new HashMap<>());

   }

   public void reportChange(final CallTreeNode node) {
      final File changedFile = fileMapping.get(node);
      changedFiles.add(changedFile);
   }

   public void write() {
      // TODO Auto-generated method stub
   }

   private boolean running = true;

   void finish() {
      running = false;
   }

   @Override
   public void run() {
      while (running) {
         try {
            Thread.sleep(aggregatedTreeWriter.getWriteInterval());
         } catch (final InterruptedException e) {
            System.out.println("Writing is finished...");
         }
         if (running) {
            try {
               synchronized (nodeMap) {
                  final Set<File> oldFiles = changedFiles;
                  changedFiles = new HashSet<>();
                  for (final File file : oldFiles) {
                     final Map<CallTreeNode, AggregatedData> partialData = fileData.get(file);
                     System.out.println("Writing: " + file + " " + partialData);
                     MAPPER.writeValue(file, partialData);
                  }
               }
            } catch (final IOException e) {
               e.printStackTrace();
            }
         }
      }

   }

   public void write(final CallTreeNode node, final long duration) {
      final AggregatedData data = getData(node);
      data.addValue(duration);
      final File changedNode = fileMapping.get(node);
      System.out.println("Need to write to: " + changedNode + " " + node);
      changedFiles.add(changedNode);
   }

   private AggregatedData getData(final CallTreeNode node) {
      AggregatedData data = nodeMap.get(node);
      if (data == null) {
         if (currentEntries >= aggregatedTreeWriter.getEntriesPerFile()) {
            currentEntries = 0;
            fileIndex++;
            destination = new File(aggregatedTreeWriter.getResultFolder(), "measurement-" + fileIndex + ".json");
            fileData.put(destination, new HashMap<>());
         } 
         data = new AggregatedData(destination, aggregatedTreeWriter.getWarmup());
         nodeMap.put(node, data);
         fileMapping.put(node, destination);
         
         final Map<CallTreeNode, AggregatedData> partialData = fileData.get(destination);
         partialData.put(node, data);
         currentEntries++;
         
      }
      return data;
   }

   public void finalWriting() throws JsonGenerationException, JsonMappingException, IOException {
      synchronized (nodeMap) {
         for (final Entry<File, Map<CallTreeNode, AggregatedData>> entry : fileData.entrySet()) {
            MAPPER.writeValue(entry.getKey(), entry.getValue());
         }
      }
   }
}