package de.dagere.kopeme.kieker.writer.onecall;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import de.dagere.kopeme.kieker.record.OneCallRecord;
import de.dagere.kopeme.kieker.writer.AggregatedTreeWriter;
import de.dagere.kopeme.kieker.writer.ChangeableFolder;
import de.dagere.kopeme.kieker.writer.WriterUtil;
import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.writer.AbstractMonitoringWriter;

public class OneCallWriter extends AbstractMonitoringWriter implements ChangeableFolder {
   public static final String PREFIX = AggregatedTreeWriter.class.getName() + ".";
   public static final String CONFIG_PATH = PREFIX + "customStoragePath";
   public static final String CONFIG_ENTRIESPERFILE = PREFIX + "entriesPerFile";

   private static OneCallWriter instance;

   private final int entriesPerFile;
   private int index = 0, fileIndex = 0;
   private File resultFolder;
   private BufferedWriter currentWriter;
   
   private final Set<String> writtenMethods = new HashSet<>();

   public static synchronized OneCallWriter getInstance() {
      return instance;
   }

   private static final Logger LOG = Logger.getLogger(OneCallWriter.class.getName());

   public OneCallWriter(final Configuration configuration) throws IOException {
      super(configuration);
      LOG.info("Init..");
      instance = this;
      String configPath = configuration.getStringProperty(CONFIG_PATH);
      if (configPath.isEmpty()) { // if the property does not exist or if the path is empty
         configPath = System.getProperty("java.io.tmpdir");
      }
      final Path kiekerPath = WriterUtil.buildKiekerLogFolder(configPath, configuration);
      resultFolder = kiekerPath.toFile();
      resultFolder.mkdirs();

      entriesPerFile = configuration.getIntProperty(CONFIG_ENTRIESPERFILE, 100);
   }

   @Override
   public void setFolder(final File writingFolder) throws IOException {
      LOG.info("Writing to: " + writingFolder);
      onTerminating();
      final Path kiekerPath = WriterUtil.buildKiekerLogFolder(writingFolder.getAbsolutePath(), configuration);
      resultFolder = kiekerPath.toFile();
      resultFolder.mkdirs();
      onStarting();
   }

   @Override
   public void onStarting() {
      newWriter();
   }

   @Override
   public void writeMonitoringRecord(final IMonitoringRecord record) {
      if (record instanceof OneCallRecord) {
         OneCallRecord oneCallRecord = (OneCallRecord) record;
         String signature = oneCallRecord.getOperationSignature();
         if (!writtenMethods.contains(signature)) {
            try {
               currentWriter.write(signature + "\n");
               index++;
               if (index > entriesPerFile) {
                  newWriter();
               }
               writtenMethods.add(signature);
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      }
      
   }

   private void newWriter() {
      index = 0;
      fileIndex++;
      Path path = new File(resultFolder, "oneCall-" + fileIndex + ".dat").toPath();
      try {
         if (currentWriter != null) {
            currentWriter.close();
         }
         currentWriter = Files.newBufferedWriter(path);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void onTerminating() {
      try {
         currentWriter.flush();
         currentWriter.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
