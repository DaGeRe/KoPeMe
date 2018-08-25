package kieker.monitoring.writer.filesystem;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.common.record.misc.RegistryRecord;
import kieker.monitoring.writer.AbstractMonitoringWriter;

/**
 * This class enables Kieker writing in different folders for KoPeMe purposes. It does so by creating a new {@link SyncFsWriter} with every new folder that is set to the
 * {@link ChangeableFolderWriter}. For storing all mapping data that is produced, every {@link RegistryRecord} that is measured is saved to a List and written to every new
 * {@link SyncFsWriter} that is created with a new folder.
 * 
 * @author reichelt
 *
 */
public class ChangeableFolderWriter extends AbstractMonitoringWriter {

   public static final String PREFIX = ChangeableFolderWriter.class.getName() + ".";
   public static final String CONFIG_PATH = PREFIX + "customStoragePath";
   public static final String CONFIG_MAXENTRIESINFILE = PREFIX + "maxEntriesInFile";
   public static final String CONFIG_MAXLOGSIZE = PREFIX + "maxLogSize";
   public static final String REAL_WRITER = PREFIX + "realwriter";

   public static final String CONFIG_MAXLOGFILES = PREFIX + "maxLogFiles";
   public static final String CONFIG_FLUSH = PREFIX + "flush";
   public static final String CONFIG_BUFFER = PREFIX + "bufferSize";

   private static ChangeableFolderWriter instance;

   public static synchronized ChangeableFolderWriter getInstance() {
      return instance;
   }

   private static final Logger LOG = LogManager.getLogger(ChangeableFolderWriter.class.getName());

   private final static List<KiekerMetadataRecord> mappingRecords = new LinkedList<>();
   private static boolean full = false;
   private final Configuration configuration;
   private AbstractMonitoringWriter currentWriter = null; // no writer is needed, until data is saved to where it belongs

   public ChangeableFolderWriter(final Configuration configuration) {
      super(configuration);
      LOG.info("Init..");
      this.configuration = configuration;
      currentWriter = createWriter(configuration);
      instance = this;
   }

   private AbstractMonitoringWriter createWriter(final Configuration configuration) {
      final String writerName = configuration.getStringProperty(REAL_WRITER);
      if (writerName.equals(AsciiFileWriter.class.getSimpleName())) {
         final Configuration newConfig = toWriterConfiguration(configuration, AsciiFileWriter.class);
         final AsciiFileWriter asyncFsWriter = new AsciiFileWriter(newConfig);
         return asyncFsWriter;
      } else if (writerName.equals(BinaryFileWriter.class.getSimpleName())) {
         final Configuration newConfig = toWriterConfiguration(configuration, BinaryFileWriter.class);
         final BinaryFileWriter syncFsWriter = new BinaryFileWriter(newConfig);
         return syncFsWriter;
      } else {
         System.out.println("Defined writer " + writerName + " not found - using default " + AsciiFileWriter.class.getSimpleName());
         final Configuration newConfig = toWriterConfiguration(configuration, AsciiFileWriter.class);
         final AsciiFileWriter syncFsWriter = new AsciiFileWriter(newConfig);
         return syncFsWriter;
      }
   }

   Configuration toWriterConfiguration(final Configuration c, Class<?> writerClass) {
      final Configuration returnable = new Configuration();
      for (final Iterator<Entry<Object, Object>> iterator = c.entrySet().iterator(); iterator.hasNext();) {
         final Entry<Object, Object> entry = iterator.next();
         final String keyAsString = entry.getKey().toString();
         final String replacedPropertyName = keyAsString.replace(getClass().getName(), writerClass.getName());
         returnable.setProperty(replacedPropertyName, entry.getValue().toString());
      }
      return returnable;
   }

   @Override
   public void onStarting() {
      System.out.println("Initializing " + getClass());
      currentWriter.onStarting();
   }

   @Override
   public void writeMonitoringRecord(IMonitoringRecord record) {
      if (record instanceof KiekerMetadataRecord && !full) {
         KiekerMetadataRecord mappingRecord = (KiekerMetadataRecord) record;
         mappingRecords.add(mappingRecord);
      }
      // System.out.println("Writing: " + record);
      // System.out.println(record.getClass());
      if (currentWriter != null) {
         LOG.trace("Record: " + record);
         currentWriter.writeMonitoringRecord(record);
      }
   }

   @Override
   public void onTerminating() {
      if (currentWriter != null) {
         currentWriter.onTerminating();
      }
   }

   public void setFolder(File writingFolder) {
      if (currentWriter != null) {
         currentWriter.onTerminating();
      }
      LOG.debug("Writing to: " + writingFolder);
      final String absolutePath = writingFolder.getAbsolutePath();
      configuration.setProperty(CONFIG_PATH, absolutePath);
      final AbstractMonitoringWriter writer = createWriter(configuration);
      // currentWriter.setController(monitoringController);
      for (final KiekerMetadataRecord record : mappingRecords) {
         LOG.info("Adding registry record: " + record);
         writer.writeMonitoringRecord(record);
      }
      full = true;
      currentWriter = writer;
   }
}
