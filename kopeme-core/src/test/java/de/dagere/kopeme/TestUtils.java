package de.dagere.kopeme;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import de.dagere.kopeme.datastorage.FolderProvider;

public class TestUtils {

   private static FolderProvider provider = FolderProvider.getInstance();

   public static final String KOPEME_DEFAULT_FOLDER = "target" + File.separator + "test-classes" + File.separator + ".KoPeMe";
   public static final String TEST_ADDITION = "testAddition";

   public static void deleteRecursively(final File file) throws IOException {
      if (file.exists()) {
         if (!file.isDirectory()) {
            if (!file.delete()) {
               throw new RuntimeException("Could not delete " + file.getAbsolutePath() + " Exists: " + file.exists());
            }
         } else {
            FileUtils.deleteDirectory(file);
         }
      }

   }

   public static void cleanAndSetKoPeMeOutputFolder() throws IOException {
      provider.setKopemeDefaultFolder(KOPEME_DEFAULT_FOLDER);
      deleteRecursively(Paths.get(KOPEME_DEFAULT_FOLDER).toFile());
   }

   /**
    * Returns the File for the test with the given canonical test class name and the given name of the test method.
    * 
    * @param canonicalName name of the testclass
    * @param testCaseName name of the test method
    * @return File containing the test result
    */
   public static File xmlFileForKoPeMeTest(final String canonicalName, final String testCaseName) {
      final String folder = provider.getFolderFor("");
      return Paths.get(folder, canonicalName, testCaseName + ".xml").toFile();
   }

}
