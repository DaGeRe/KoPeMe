package de.dagere.kopeme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import de.dagere.kopeme.datastorage.FolderProvider;

public class AndroidConfigurationTest {

   public static final String TEST_RESOURCES_FOLDER = "target/test-classes";
   public static final String TEST_ANDROID_CONFIG_FOLDER_NAME = "android_configreader";
   public static final String TEST_ANDROID_RESOURCES_FOLDER = TEST_RESOURCES_FOLDER + "/" + TEST_ANDROID_CONFIG_FOLDER_NAME;
   public static final String KOPEME_CONFIG_FILE = "kopeme_config.json";
   public static final String KOPEME_CONFIG_FILE_FOLDER = TEST_ANDROID_CONFIG_FOLDER_NAME + "/" + KOPEME_CONFIG_FILE;
   public static final String KOPEME_HOME = "/storage/emulated/0/Documents/peass/measurementsTemp/";

   // Copies the kopeme_config.json into root resources folder,
   // to make it available for AndroidConfiguration.read() method.
   @BeforeClass
   public static void copyKopemeConfig() throws IOException, InterruptedException {
      Path sourcePath = Paths.get(TEST_ANDROID_RESOURCES_FOLDER, KOPEME_CONFIG_FILE);
      Path destinationPath = Paths.get(TEST_RESOURCES_FOLDER, KOPEME_CONFIG_FILE);
      Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
      FolderProvider.getInstance().resetKopemeDefaultFolder();
   }

   @Test
   public void testRead() throws Exception {
      assertEquals(KOPEME_HOME, AndroidConfiguration.read("KOPEME_HOME"));

      assertEquals(KOPEME_HOME, FolderProvider.getInstance().getKopemeDefaultFolder());

      assertNull(AndroidConfiguration.read("invalid field"));
   }

   // Deleting the kopeme_config.json file from root resources folder, so other tests will not fail.
   // Because FolderProvider will use the KOPEME_HOME variable defined inside this file instead of the default variables.
   @AfterClass
   public static void deleteKopemeConfig() throws IOException {
      Path kopemeConfigFile = Paths.get(TEST_RESOURCES_FOLDER, KOPEME_CONFIG_FILE);
      Files.deleteIfExists(kopemeConfigFile);
      FolderProvider.getInstance().resetKopemeDefaultFolder();
   }
}