package kieker.monitoring.writer.filesystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

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
public class AggregatedTreeWriter extends AbstractMonitoringWriter implements ChangeableFolder {

   public static final String PREFIX = AggregatedTreeWriter.class.getName() + ".";
   public static final String CONFIG_PATH = PREFIX + "customStoragePath";
   public static final String CONFIG_WRITEINTERVAL = PREFIX + "writeInterval";
   public static final String CONFIG_WARMUP = PREFIX + "warmup";
   public static final String CONFIG_ENTRIESPERFILE = PREFIX + "entriesPerFile";

   private static AggregatedTreeWriter instance;

   private File resultFolder;
   private final int writeInterval;
   private final int warmup;
   private final int entriesPerFile;
   private FileDataManager writer;

   private Thread writerThread;

   public static synchronized AggregatedTreeWriter getInstance() {
      return instance;
   }

   private static final Logger LOG = Logger.getLogger(AggregatedTreeWriter.class.getName());

   // private final Configuration configuration;

   public AggregatedTreeWriter(final Configuration configuration) {
      super(configuration);
      LOG.info("Init..");
      instance = this;
      final Path kiekerPath = KiekerLogFolder.buildKiekerLogFolder(configuration.getStringProperty(CONFIG_PATH), configuration);
      resultFolder = kiekerPath.toFile();
      resultFolder.mkdirs();

      writeInterval = configuration.getIntProperty(CONFIG_WRITEINTERVAL, 5000);
      warmup = configuration.getIntProperty(CONFIG_WARMUP, 0);
      entriesPerFile = configuration.getIntProperty(CONFIG_ENTRIESPERFILE, 100);
      
      writer = new FileDataManager(this);
   }

   @Override
   public void onStarting() {
      System.out.println("Initializing " + getClass());
      writerThread = new Thread(writer);
      writerThread.setPriority(Thread.MIN_PRIORITY);
      writerThread.start();
   }

   @Override
   public void writeMonitoringRecord(final IMonitoringRecord record) {
      if (record instanceof OperationExecutionRecord) {
         final OperationExecutionRecord operation = (OperationExecutionRecord) record;
         final CallTreeNode node = new CallTreeNode(operation.getEoi(), operation.getEss(), operation.getOperationSignature());
         writer.write(node, operation.getTout() - operation.getTin());
      }
   }

   @Override
   public void onTerminating() {
      try {
         System.out.println("Finishing AggregatedTreeWriter");
         writer.finish();
         writerThread.interrupt();
         writer.finalWriting();
      } catch (final IOException e) {
         e.printStackTrace();
      }
   }
   
   public void setFolder(final File writingFolder) {
      LOG.info("Writing to: " + writingFolder);
      final Path kiekerPath = KiekerLogFolder.buildKiekerLogFolder(writingFolder.getAbsolutePath(), configuration);
      resultFolder = kiekerPath.toFile();
      resultFolder.mkdirs();
      writer = new FileDataManager(this);
   }

   public Thread getWriterThread() {
      return writerThread;
   }

   public void setWriterThread(final Thread writerThread) {
      this.writerThread = writerThread;
   }

   public int getWriteInterval() {
      return writeInterval;
   }

   public int getWarmup() {
      return warmup;
   }

   public int getEntriesPerFile() {
      return entriesPerFile;
   }
   
   public File getResultFolder() {
      return resultFolder;
   }

}
