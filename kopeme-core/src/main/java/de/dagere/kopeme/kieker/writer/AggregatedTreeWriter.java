package de.dagere.kopeme.kieker.writer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import de.dagere.kopeme.kieker.aggregateddata.AggregatedDataNode;
import de.dagere.kopeme.kieker.aggregateddata.DataNode;
import de.dagere.kopeme.kieker.aggregateddata.FileDataManager;
import de.dagere.kopeme.kieker.record.ReducedOperationExecutionRecord;
import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.common.util.filesystem.FSUtil;
import kieker.monitoring.core.configuration.ConfigurationConstants;
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

   private static AggregatedTreeWriter instance;

   private final int writeInterval;
   private final StatisticConfig statisticConfig;
   private final int entriesPerFile;
   private final boolean ignoreEOIs;
   private File resultFolder;
   private FileDataManager dataManager;

   private Thread writerThread;

   public static synchronized AggregatedTreeWriter getInstance() {
      return instance;
   }

   private static final Logger LOG = Logger.getLogger(AggregatedTreeWriter.class.getName());

   public AggregatedTreeWriter(final Configuration configuration) throws IOException {
      super(configuration);
      LOG.info("Init..");
      instance = this;
      final Path kiekerPath = buildKiekerLogFolder(configuration.getStringProperty(CONFIG_PATH), configuration);
      resultFolder = kiekerPath.toFile();
      resultFolder.mkdirs();

      writeInterval = configuration.getIntProperty(CONFIG_WRITE_INTERVAL, 5000);
      entriesPerFile = configuration.getIntProperty(CONFIG_ENTRIESPERFILE, 100);
      statisticConfig = new StatisticConfig(-1, configuration.getDoubleProperty(CONFIG_OUTLIER, -1));
      ignoreEOIs = configuration.getBooleanProperty(CONFIG_IGNORE_EOIS, true);

      dataManager = new FileDataManager(this);
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
         final long timeInMikroseconds = (operation.getTout() - operation.getTin()) / 1000;
         dataManager.write(node, timeInMikroseconds);
      }
      if (record instanceof ReducedOperationExecutionRecord) {
         ReducedOperationExecutionRecord operation = (ReducedOperationExecutionRecord) record;
         final DataNode node = new DataNode(operation.getOperationSignature());
         final long timeInMikroseconds = (operation.getTout() - operation.getTin()) / 1000;
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
         }
      } catch (final IOException e) {
         e.printStackTrace();
      }
   }

   @Override
   public synchronized void setFolder(final File writingFolder) throws IOException {
      if (dataManager != null) {
         dataManager.finish();
         dataManager.close();
      }
      LOG.info("Writing to: " + writingFolder);
      final Path kiekerPath = buildKiekerLogFolder(writingFolder.getAbsolutePath(), configuration);
      resultFolder = kiekerPath.toFile();
      resultFolder.mkdirs();
      onTerminating();
      dataManager = new FileDataManager(this);
      onStarting();
   }

   public Thread getWriterThread() {
      return writerThread;
   }

   public int getWriteInterval() {
      return writeInterval;
   }

   public int getEntriesPerFile() {
      return entriesPerFile;
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

   public static Path buildKiekerLogFolder(final String customStoragePath, final Configuration configuration) {
      final DateFormat date = new SimpleDateFormat("yyyyMMdd'-'HHmmss", Locale.US);
      date.setTimeZone(TimeZone.getTimeZone("UTC"));
      final String currentDateStr = date.format(new java.util.Date())
            + "-" + System.nanoTime(); // 'SSS' in SimpleDateFormat is not accurate enough for fast unit tests

      final String hostName = configuration.getStringProperty(ConfigurationConstants.HOST_NAME);
      final String controllerName = configuration.getStringProperty(ConfigurationConstants.CONTROLLER_NAME);

      final String filename = String.format("%s-%s-UTC-%s-%s", FSUtil.FILE_PREFIX, currentDateStr, hostName, controllerName);

      return Paths.get(customStoragePath, filename);
   }
}
