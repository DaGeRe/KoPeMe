package kieker.monitoring.writer.filesystem.aggregateddata;

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
import com.fasterxml.jackson.databind.module.SimpleModule;

import kieker.monitoring.writer.filesystem.AggregatedTreeWriter;

public class FileDataManager implements Runnable {

   public static final ObjectMapper MAPPER = new ObjectMapper();
   static {
      final SimpleModule keyDeserializer = new SimpleModule();
      keyDeserializer.addKeyDeserializer(AggregatedDataNode.class, new CallTreeNodeDeserializer());
      MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
      MAPPER.registerModule(keyDeserializer);
   }
   
   private final AggregatedTreeWriter aggregatedTreeWriter;
   
   private File currentDestination;
   private Map<AggregatedDataNode, File> fileMapping = new HashMap<>();
   private Map<File, Map<AggregatedDataNode, AggregatedData>> fileData = new ConcurrentHashMap<>();
   private Set<File> changedFiles = new HashSet<>();
   private final Map<AggregatedDataNode, AggregatedData> nodeMap = new ConcurrentHashMap<>();

   private int currentEntries = 0;
   private int fileIndex = 0;

   /**
    * @param aggregatedTreeWriter
    */
   public FileDataManager(final AggregatedTreeWriter aggregatedTreeWriter) {
      this.aggregatedTreeWriter = aggregatedTreeWriter;
      currentDestination = new File(aggregatedTreeWriter.getResultFolder(), "measurement-0.json");
      fileData.put(currentDestination, new HashMap<>());
   }

   public void reportChange(final AggregatedDataNode node) {
      final File changedFile = fileMapping.get(node);
      changedFiles.add(changedFile);
   }


   private boolean running = true;

   public void finish() {
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
                     final Map<AggregatedDataNode, AggregatedData> partialData = fileData.get(file);
                     MAPPER.writeValue(file, partialData);
                  }
               }
            } catch (final IOException e) {
               e.printStackTrace();
            }
         }
      }
   }

   public void write(final AggregatedDataNode node, final long duration) {
      final AggregatedData data = getData(node);
      data.addValue(duration);
      final File changedNode = fileMapping.get(node);
      changedFiles.add(changedNode);
   }

   private AggregatedData getData(final AggregatedDataNode node) {
      AggregatedData data = nodeMap.get(node);
      if (data == null) {
         if (currentEntries >= aggregatedTreeWriter.getEntriesPerFile()) {
            currentEntries = 0;
            fileIndex++;
            currentDestination = new File(aggregatedTreeWriter.getResultFolder(), "measurement-" + fileIndex + ".json");
            fileData.put(currentDestination, new HashMap<>());
         } 
         data = new AggregatedData(currentDestination, aggregatedTreeWriter.getWarmup());
         nodeMap.put(node, data);
         fileMapping.put(node, currentDestination);
         
         final Map<AggregatedDataNode, AggregatedData> partialData = fileData.get(currentDestination);
         partialData.put(node, data);
         currentEntries++;
         
      }
      return data;
   }

   public void finalWriting() throws JsonGenerationException, JsonMappingException, IOException {
      synchronized (nodeMap) {
         for (final Entry<File, Map<AggregatedDataNode, AggregatedData>> entry : fileData.entrySet()) {
            System.out.println("Final writing to " + entry.getKey());
            MAPPER.writeValue(entry.getKey(), entry.getValue());
         }
      }
   }
}