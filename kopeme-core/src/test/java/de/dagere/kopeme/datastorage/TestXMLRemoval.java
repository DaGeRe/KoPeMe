package de.dagere.kopeme.datastorage;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.kopemedata.Kopemedata;

public class TestXMLRemoval {
   
   @Test
   public void testXMLReadingAndRemoval() throws IOException {
      File testFolder = new File("target/xmlFileTest");
      testFolder.mkdirs();
      FileUtils.cleanDirectory(testFolder);
      
      File xmlExampleFile = new File("src/test/resources/default/folderprovider/de.test.ExampleTest1/de.test.ExampleTest1.test1.xml");
      File deletableFile = new File(testFolder, "test1.xml");
      FileUtils.copyFile(xmlExampleFile, deletableFile);
      
      Kopemedata data = JSONDataLoader.loadData(deletableFile);
      
      Assert.assertEquals("de.test.ExampleTest1", data.getClazz());
      Assert.assertFalse(deletableFile.exists());
      
      File jsonFile = new File(testFolder, "test1.json");
      Assert.assertTrue(jsonFile.exists());
   }
}
