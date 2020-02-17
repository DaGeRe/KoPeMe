package de.dagere.kopeme.datastorage;

import java.io.File;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata;
import de.dagere.kopeme.generated.Result.Fulldata.Value;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Chunk;

/**
 * Manages the storing of resultdata of KoPeMe-tests in the KoPeMe-XML-format.
 * 
 * @author reichelt
 *
 */
public final class XMLDataStorer implements DataStorer {

   private static final Logger LOG = LogManager.getLogger(XMLDataStorer.class);
   private final File file;
   private Kopemedata data;

   /**
    * Initializes an XMLDataStorer.
    * 
    * @param foldername Folder where the result should be saved
    * @param classname Name of the test class which was executed
    * @param methodname Name of the method which was executed
    * @throws JAXBException Thrown if an XML Writing error occurs
    */
   public XMLDataStorer(final File foldername, final String classname, final String methodname) throws JAXBException {
      final String filename = methodname + ".xml";
      file = new File(foldername, filename);
      if (file.exists()) {
         final XMLDataLoader loader = new XMLDataLoader(file);
         data = loader.getFullData();
      } else {
         createXMLData(classname);
      }
   }

   /**
    * Initializes XML-Data.
    * 
    * @param classname Name of the testclass
    */
   private void createXMLData(final String classname) {
      data = new Kopemedata();
      data.setTestcases(new Testcases());
      final Testcases tc = data.getTestcases();
      tc.setClazz(classname);
      storeData();
   }

   @Override
   public void storeValue(final Result result, String testcase, String collectorName) {
      if (data.getTestcases() == null) {
         data.setTestcases(new Testcases());
      }
      final TestcaseType test = getOrCreateTestcase(result, testcase);

      final Datacollector dc = getOrCreateDatacollector(collectorName, test);

      if (System.getenv("KOPEME_CHUNKSTARTTIME") != null) {
         final Chunk current = findChunk(dc);
         current.getResult().add(result);
      } else {
         dc.getResult().add(result);
      }
      storeData();
   }

   private Chunk findChunk(final Datacollector dc) {
      final long start = Long.parseLong(System.getenv("KOPEME_CHUNKSTARTTIME"));
      Chunk current = null;
      for (final Chunk chunk : dc.getChunk()) {
       if (chunk.getChunkStartTime() == start){
      	 current = chunk;
       }
      }
      if (current == null) {
         current = new Chunk();
         current.setChunkStartTime(start);
         dc.getChunk().add(current);
      }
      return current;
   }

   private Datacollector getOrCreateDatacollector(final String collector, final TestcaseType test) {
      Datacollector dc = null;
      for (final Datacollector dc2 : test.getDatacollector()) {
         LOG.trace("Name: {} Collectorname: {}", dc2.getName(), collector);
         if (dc2.getName().equals(collector)) {
            LOG.trace("Equals");
            dc = dc2;
         }
      }

      if (dc == null) {
         LOG.trace("Erstelle neu");
         dc = new Datacollector();
         dc.setName(collector);
         test.getDatacollector().add(dc);
      }
      return dc;
   }

   private TestcaseType getOrCreateTestcase(final Result performanceDataMeasure, String testcase) {
      TestcaseType test = null;
      for (final TestcaseType tc : data.getTestcases().getTestcase()) {
         if (tc.getName().equals(testcase)) {
            test = tc;
         }
      }
      if (test == null) {
         LOG.trace("Test == null, füge hinzu");
         test = new TestcaseType();
         test.setName(testcase);
         data.getTestcases().getTestcase().add(test);
      }
      return test;
   }

   private void storeData() {
      try {
         LOG.info("Storing data to: {}", file.getAbsoluteFile());
         final Marshaller jaxbMarshaller = XMLDataLoader.jc.createMarshaller();
         jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
         jaxbMarshaller.marshal(data, file);
      } catch (final JAXBException e) {
         e.printStackTrace();
      }
   }

   /**
    * Stores the data in the given file.
    * 
    * @param file File for saving
    * @param currentdata Data to save
    */
   public static void storeData(final File file, final Kopemedata currentdata) {
      try {
         LOG.info("Storing data to: {}", file.getAbsoluteFile());
         final Marshaller jaxbMarshaller = XMLDataLoader.jc.createMarshaller();
         jaxbMarshaller.setProperty("com.sun.xml.bind.indentString", " ");
         jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

         jaxbMarshaller.marshal(currentdata, file);
      } catch (final JAXBException e) {
         e.printStackTrace();
      }
      LOG.debug("Storing finished.");
   }

}
