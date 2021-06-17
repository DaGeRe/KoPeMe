package de.dagere.kopeme.kieker.writer;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;
import kieker.common.configuration.Configuration;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.writer.filesystem.FileWriter;

/**
 * Writes Kieker examples result to the kieker monitoring controller for tests.
 * 
 * @author dhaeb
 *
 */
public class TestChangeableFolderWriter {

   public static final File DEFAULT_FOLDER = new File("target" + File.separator + "kieker_testresults");
   private static final File NEW_FOLDER_AT_RUNTIME = new File("target" + File.separator + "kieker_testresults_changed_folder");
   private static final File NEW_FOLDER_AT_RUNTIME2 = new File("target" + File.separator + "kieker_testresults_changed_folder2");

   @BeforeClass
   public static void setupClass() throws IOException {
      KiekerTestHelper.emptyFolder(DEFAULT_FOLDER);
      KiekerTestHelper.emptyFolder(NEW_FOLDER_AT_RUNTIME);
      KiekerTestHelper.emptyFolder(NEW_FOLDER_AT_RUNTIME2);
      final Configuration config = ConfigurationFactory.createSingletonConfiguration();
      final String absolutePath = DEFAULT_FOLDER.getAbsolutePath();
      config.setProperty("kieker.monitoring.writer", ChangeableFolderWriter.class.getName());
      config.setProperty(ChangeableFolderWriter.CONFIG_PATH, absolutePath);
      config.setProperty(ChangeableFolderWriter.CONFIG_MAXENTRIESINFILE, "100");
      config.setProperty(ChangeableFolderWriter.CONFIG_MAXLOGFILES, "-1");
      config.setProperty(ChangeableFolderWriter.CONFIG_MAXLOGSIZE, "1");
      config.setProperty(ChangeableFolderWriter.CONFIG_FLUSH, "true");
      config.setProperty(ChangeableFolderWriter.CONFIG_BUFFER, "8192");
      config.setProperty(ChangeableFolderWriter.REAL_WRITER, "FileWriter");
      Sample.MONITORING_CONTROLLER = MonitoringController.createInstance(config);
      Sample.MONITORING_CONTROLLER.enableMonitoring();
   }

   @Test
   public void testConfigConvertion() throws Exception {
      final Configuration c = new Configuration();
      final int fixture = 100;
      c.setProperty(ChangeableFolderWriter.CONFIG_MAXENTRIESINFILE, Integer.toString(fixture));
      final ChangeableFolderWriter testable = ChangeableFolderWriter.getInstance();
      final Configuration result = testable.toWriterConfiguration(c, FileWriter.class);
      final int intResult = result.getIntProperty(FileWriter.CONFIG_MAXENTRIESINFILE);
      assertEquals(fixture, intResult);
      KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);
   }

   @Ignore
   @Test
   public void testChangesFolderCorrectly() throws Exception {
      final ChangeableFolderWriter testable = ChangeableFolderWriter.getInstance();
      final int rounds = 15, lines = 15;
      KiekerTestHelper.runFixture(rounds);
      testable.setFolder(NEW_FOLDER_AT_RUNTIME);
      KiekerTestHelper.runFixture(rounds);
      testable.setFolder(NEW_FOLDER_AT_RUNTIME2);
      KiekerTestHelper.runFixture(rounds);
      assertKiekerFileConstainsLines(DEFAULT_FOLDER, lines + 2); // TODO due to the meta data entry, which are written to every folder
      assertKiekerFileConstainsLines(NEW_FOLDER_AT_RUNTIME, lines + 1);
      assertKiekerFileConstainsLines(NEW_FOLDER_AT_RUNTIME2, lines + 1);
   }

   private void assertKiekerFileConstainsLines(final File kiekerFolder, final int lines) throws IOException {
      final File currentMeasureFile = KiekerTestHelper.assertKiekerDir(kiekerFolder);
      assertEquals(lines, Files.readAllLines(currentMeasureFile.toPath(), StandardCharsets.UTF_8).size());
   }

}
