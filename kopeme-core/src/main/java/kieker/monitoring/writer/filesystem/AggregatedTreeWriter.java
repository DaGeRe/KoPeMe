package kieker.monitoring.writer.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.writer.AbstractMonitoringWriter;

/**
 * This class creates a tree with statistical summaries from the data.
 * 
 * @author reichelt
 *
 */
public class AggregatedTreeWriter extends AbstractMonitoringWriter {

   public static final String PREFIX = AggregatedTreeWriter.class.getName() + ".";
   public static final String CONFIG_PATH = PREFIX + "customStoragePath";
   public static final String CONFIG_WRITEINTERVAL = PREFIX + "writeInterval";

   private static AggregatedTreeWriter instance;

   private final File destination;
   private final int writeInterval;
   private final FinishableWriter writer = new FinishableWriter();
   private final Map<CallTreeNode, SummaryStatistics> nodeMap = new ConcurrentHashMap<>();

   private static final ObjectMapper MAPPER = new ObjectMapper();
   static {
      MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
   }

   private Thread writerThread;

   public static synchronized AggregatedTreeWriter getInstance() {
      return instance;
   }

   private static final Logger LOG = Logger.getLogger(AggregatedTreeWriter.class.getName());

   // private final Configuration configuration;

   public AggregatedTreeWriter(final Configuration configuration) {
      super(configuration);
      LOG.info("Init..");
      // this.configuration = configuration;
      instance = this;
      final String pathname = configuration.getStringProperty(CONFIG_PATH).equals("") ? "default.json" : configuration.getStringProperty(CONFIG_PATH);
      if (pathname.endsWith(".json")) {
         destination = new File(pathname);
         destination.getParentFile().mkdirs();
      } else {
         final Path kiekerPath = KiekerLogFolder.buildKiekerLogFolder(configuration.getStringProperty(CONFIG_PATH), configuration);
         final File parentFolder = kiekerPath.toFile();
         parentFolder.mkdirs();
         destination = new File(kiekerPath.toFile(), "measurements.json");
      }

      writeInterval = configuration.getIntProperty(CONFIG_WRITEINTERVAL, 5000);

   }

   class FinishableWriter implements Runnable {
      private boolean running = true;

      private void finish() {
         running = false;
      }

      @Override
      public void run() {
         while (running) {
            try {
               Thread.sleep(writeInterval);
            } catch (final InterruptedException e) {
               e.printStackTrace();
            }
            if (running) {
               try {
                  synchronized (nodeMap) {
                     MAPPER.writeValue(destination, nodeMap);
                  }
               } catch (final IOException e) {
                  e.printStackTrace();
               }
            }
         }

      }
   }

   @Override
   public void onStarting() {
      System.out.println("Initializing " + getClass());
      writerThread = new Thread(writer);
      writerThread.start();
   }

   @Override
   public void writeMonitoringRecord(final IMonitoringRecord record) {
      if (record instanceof OperationExecutionRecord) {
         final OperationExecutionRecord operation = (OperationExecutionRecord) record;
         final CallTreeNode node = new CallTreeNode(operation.getEoi(), operation.getEss(), operation.getOperationSignature());
         SummaryStatistics statistics = nodeMap.get(node);
         if (statistics == null) {
            statistics = new SummaryStatistics();
            nodeMap.put(node, statistics);
         }
         statistics.addValue(operation.getTin() - operation.getTout());
      }

   }

   @Override
   public void onTerminating() {
      try {
         writer.finish();
         writerThread.interrupt();
         synchronized (nodeMap) {
            MAPPER.writeValue(destination, nodeMap);
         }
      } catch (final IOException e) {
         e.printStackTrace();
      }
   }

}
