package de.dagere.kopeme.kieker.writer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import de.dagere.kopeme.kieker.aggregateddata.AggregatedDataNode;
import de.dagere.kopeme.kieker.aggregateddata.DataNode;
import de.dagere.kopeme.kieker.aggregateddata.DataWriter;
import de.dagere.kopeme.kieker.aggregateddata.SimpleFileDataManagerBin;
import de.dagere.kopeme.kieker.aggregateddata.AggregatedFileDataManagerBin;
import de.dagere.kopeme.kieker.aggregateddata.AggregatedFileDataManagerCSV;
import de.dagere.kopeme.kieker.record.DurationRecord;
import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.writer.AbstractMonitoringWriter;

/**
 * This class creates a tree with statistical summaries from the data.
 * 
 * The warmup is set directly via KoPeMe-annotations, setting it via Kieker config is ignored.
 * 
 * @author reichelt
 *
 */
public class AggregatedTreeWriter extends AbstractMonitoringWriter implements ChangeableFolder {

   public static final String PREFIX = AggregatedTreeWriter.class.getName() + ".";
   public static final String CONFIG_PATH = PREFIX + "customStoragePath";
   public static final String CONFIG_WRITE_INTERVAL = PREFIX + "writeInterval";
   public static final String CONFIG_IGNORE_EOIS = PREFIX + "ignoreEOIs";
   public static final String CONFIG_OUTLIER = PREFIX + "outlier";
   public static final String CONFIG_ENTRIESPERFILE = PREFIX + "entriesPerFile";
   public static final String WRITING_TYPE = PREFIX + "writingType";

   private static AggregatedTreeWriter instance;
   private final StatisticConfig statisticConfig;
   private final boolean ignoreEOIs;
   private File resultFolder;
   private final String writingType;
   private DataWriter dataManager;

   private Thread writerThread;

   public static synchronized AggregatedTreeWriter getInstance() {
      return instance;
   }

   private static final Logger LOG = Logger.getLogger(AggregatedTreeWriter.class.getName());

   public AggregatedTreeWriter(final Configuration configuration) throws IOException {
      super(configuration);
      LOG.info("Init..");
      instance = this;
      final Path kiekerPath = WriterUtil.buildKiekerLogFolder(configuration.getStringProperty(CONFIG_PATH), configuration);
      resultFolder = kiekerPath.toFile();
      resultFolder.mkdirs();

      int writeInterval = configuration.getIntProperty(CONFIG_WRITE_INTERVAL, 5000);
      int entriesPerFile = configuration.getIntProperty(CONFIG_ENTRIESPERFILE, 100);
      statisticConfig = new StatisticConfig(-1, configuration.getDoubleProperty(CONFIG_OUTLIER, -1), writeInterval, entriesPerFile);
      ignoreEOIs = configuration.getBooleanProperty(CONFIG_IGNORE_EOIS, true);

      writingType = configuration.getStringProperty(WRITING_TYPE, WritingType.BinaryAggregated.name());
      createDataWriter();
   }

   private void createDataWriter() throws IOException {
      if (WritingType.BinaryAggregated.name().equals(writingType)) {
         dataManager = new AggregatedFileDataManagerBin(statisticConfig, resultFolder);
      } else if (WritingType.BinarySimple.name().equals(writingType)) {
         dataManager = new SimpleFileDataManagerBin(statisticConfig, resultFolder);
      } else if (WritingType.CSVAggregated.name().equals(writingType)) {
         dataManager = new AggregatedFileDataManagerCSV(statisticConfig, resultFolder);
      } else if (WritingType.CSVSimple.name().equals(writingType)) {
         throw new RuntimeException("Not implemented yet");
      }
   }

   @Override
   public void onStarting() {
      System.out.println("Initializing " + getClass());
      writerThread = new Thread(dataManager);
      writerThread.setPriority(Thread.MIN_PRIORITY);
      writerThread.start();
   }

   @Override
   public void writeMonitoringRecord(final IMonitoringRecord record) {
      if (record instanceof OperationExecutionRecord) {
         final OperationExecutionRecord operation = (OperationExecutionRecord) record;
         final int eoi = ignoreEOIs ? -1 : operation.getEoi();
         final AggregatedDataNode node = new AggregatedDataNode(eoi, operation.getEss(), operation.getOperationSignature());
         final long timeInMikroseconds = (operation.getTout() - operation.getTin());
         dataManager.write(node, timeInMikroseconds);
      } else if (record instanceof DurationRecord) {
         DurationRecord operation = (DurationRecord) record;
         final DataNode node = new DataNode(operation.getOperationSignature());
         final long timeInMikroseconds = (operation.getTout() - operation.getTin());
         dataManager.write(node, timeInMikroseconds);
      }
   }

   @Override
   public synchronized void onTerminating() {
      try {
         if (writerThread != null) {
            LOG.info("Finishing AggregatedTreeWriter");
            dataManager.finish();
            writerThread.interrupt();
            dataManager.close();
         } else {
            if (dataManager != null) {
               dataManager.finish();
               dataManager.close();
            }
         }
      } catch (final IOException e) {
         e.printStackTrace();
      }
   }

   @Override
   public synchronized void setFolder(final File writingFolder) throws IOException {
      LOG.info("Writing to: " + writingFolder);
      onTerminating();
      final Path kiekerPath = WriterUtil.buildKiekerLogFolder(writingFolder.getAbsolutePath(), configuration);
      resultFolder = kiekerPath.toFile();
      resultFolder.mkdirs();
      createDataWriter();
      onStarting();
   }

   public Thread getWriterThread() {
      return writerThread;
   }

   public File getResultFolder() {
      return resultFolder;
   }

   public StatisticConfig getStatisticConfig() {
      return statisticConfig;
   }

   public boolean isIgnoreEOI() {
      return ignoreEOIs;
   }

}
