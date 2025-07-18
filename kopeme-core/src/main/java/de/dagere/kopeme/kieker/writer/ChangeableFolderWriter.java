package de.dagere.kopeme.kieker.writer;

import java.io.File;
import java.io.IOException;
import java.nio.BufferUnderflowException;
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
import kieker.monitoring.writer.filesystem.FileWriter;

/**
 * This class enables Kieker writing in different folders for KoPeMe purposes. It does so by creating a new {@link SyncFsWriter} with every new folder that is set to the
 * {@link ChangeableFolderWriter}. For storing all mapping data that is produced, every {@link RegistryRecord} that is measured is saved to a List and written to every new
 * {@link SyncFsWriter} that is created with a new folder.
 * 
 * @author reichelt
 *
 */
public class ChangeableFolderWriter extends AbstractMonitoringWriter implements ChangeableFolder {

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

   private static final Logger LOG = LogManager.getLogger(ChangeableFolderWriter.class);

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
      LOG.info("Writer: " + currentWriter.getClass());
   }

   private AbstractMonitoringWriter createWriter(final Configuration configuration) {
      final String writerName = configuration.getStringProperty(REAL_WRITER);
      try {
         if (writerName.equals(FileWriter.class.getSimpleName())) {
            final Configuration newConfig = toWriterConfiguration(configuration, FileWriter.class);
            FileWriter fsWriter = new FileWriter(newConfig);
            return fsWriter;
         } else {
            LOG.warn("Defined writer " + writerName + " not found - using default " + FileWriter.class.getSimpleName());
            final Configuration newConfig = toWriterConfiguration(configuration, FileWriter.class);
            final FileWriter syncFsWriter = new FileWriter(newConfig);
            return syncFsWriter;
         }
      } catch (IOException e) {
         e.printStackTrace();
         return null;
      }
   }

   Configuration toWriterConfiguration(final Configuration configuration, final Class<?> writerClass) {
      final Configuration returnable = new Configuration();
      for (final Iterator<Entry<Object, Object>> iterator = configuration.entrySet().iterator(); iterator.hasNext();) {
         final Entry<Object, Object> entry = iterator.next();
         final String keyAsString = entry.getKey().toString();
         final String replacedPropertyName = keyAsString.replace(getClass().getName(), writerClass.getName());
         if (entry.getValue() != null && !(entry.getValue() instanceof String && entry.getValue().equals(""))) {
            returnable.setProperty(replacedPropertyName, entry.getValue().toString());
         }
      }
      return returnable;
   }

   @Override
   public void onStarting() {
      LOG.info("Initializing " + getClass());
      currentWriter.onStarting();
   }

   @Override
   public void writeMonitoringRecord(final IMonitoringRecord record) {
      if (record instanceof KiekerMetadataRecord && !full) {
         addMappingRecord(record);
      }
      if (currentWriter != null) {
         LOG.trace("Record: " + record);
         // LOG.info("Change writing to: " + System.identityHashCode(currentWriter));
         currentWriter.writeMonitoringRecord(record);
      }
   }

   private synchronized void addMappingRecord(final IMonitoringRecord record) {
      final KiekerMetadataRecord mappingRecord = (KiekerMetadataRecord) record;
      mappingRecords.add(mappingRecord);
   }

   @Override
   public void onTerminating() {
      if (currentWriter != null) {
         LOG.info("Terminating writing");
         currentWriter.onTerminating();
      }
   }

   @Override
   public void setFolder(final File writingFolder) {
      if (currentWriter != null) {
         LOG.info("Terminating old writer");
         LOG.info("writer: " + currentWriter.getClass());
         try {
            currentWriter.onTerminating();
         } catch (BufferUnderflowException e) {
            LOG.info("Kieker exeption occured during closing old writer; ignoring");
            e.printStackTrace();
         }
      }
      LOG.info("Writing to: " + writingFolder + " " + System.identityHashCode(currentWriter));
      final String absolutePath = writingFolder.getAbsolutePath();
      configuration.setProperty(CONFIG_PATH, absolutePath);
      final AbstractMonitoringWriter writer = createWriter(configuration);
      LOG.info("New writer " + System.identityHashCode(writer) + " created; old writer " + System.identityHashCode(currentWriter));
      addRecordsToNewWriter(writer);
      full = true;
      currentWriter = writer;
      LOG.info("Change writing to: " + System.identityHashCode(currentWriter));
   }

   private synchronized void addRecordsToNewWriter(final AbstractMonitoringWriter writer) {
      for (final KiekerMetadataRecord record : mappingRecords) {
         LOG.info("Adding registry record: " + record);
         writer.writeMonitoringRecord(record);
      }
   }
}
