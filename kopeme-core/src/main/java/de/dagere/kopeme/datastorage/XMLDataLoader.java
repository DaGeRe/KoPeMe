package de.dagere.kopeme.datastorage;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datacollection.tempfile.WrittenResultReader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Chunk;

/**
 * Loads XML-Performance-Data.
 * 
 * @author reichelt
 *
 */
public final class XMLDataLoader implements DataLoader {
   private static final Logger LOG = LogManager.getLogger(XMLDataLoader.class);
   private final File file;
   private Kopemedata data;

   /**
    * Initializes the XMLDataLoader with the given file.
    * 
    * @param f File that should be loaded
    * @throws JAXBException Thrown if the File countains errors
    */
   public XMLDataLoader(final File file) throws JAXBException {
      this.file = file;
      loadData();
   }

   // static Unmarshaller unmarshaller;
   static JAXBContext jc;
   static {
      try {
         jc = JAXBContext.newInstance(Kopemedata.class);
      } catch (final JAXBException e) {
         e.printStackTrace();
      }

   }

   /**
    * Loads the data.
    * 
    * @throws JAXBException Thrown if the File countains errors
    */
   private void loadData() throws JAXBException {
      if (file.exists()) {
         final Unmarshaller unmarshaller = jc.createUnmarshaller();
         data = (Kopemedata) unmarshaller.unmarshal(file);
         LOG.trace("Daten geladen, Daten: {}", data);
      } else {
         LOG.info("Datei {} existiert nicht", file.getAbsolutePath());
         data = new Kopemedata();
         data.setTestcases(new Testcases());
         final Testcases tc = data.getTestcases();
         LOG.trace("TC: {}", tc);
         tc.setClazz(file.getName());
      }
   }

   @Override
   public Map<String, Map<Date, Long>> getData() {
      final Map<String, Map<Date, Long>> map = new HashMap<>();
      final Testcases testcases = data.getTestcases();
      for (final TestcaseType tct : testcases.getTestcase()) {
         final Map<Date, Long> measures = new HashMap<>();
         for (final Result s : tct.getDatacollector().get(0).getResult()) {
            measures.put(new Date(s.getDate()), (long) s.getValue());
         }
         map.put(tct.getName(), measures);
      }
      return map;
   }

   /**
    * Returns a mapping from all testcases to their results for a certain collectorName.
    * 
    * @param collectorName The name of the collector for loading the Results
    * @return Mapping from all testcases to their results
    */
   public Map<String, Map<Date, Long>> getData(final String collectorName) {
      final Map<String, Map<Date, Long>> map = new HashMap<>();
      final Testcases testcases = data.getTestcases();
      for (final TestcaseType tct : testcases.getTestcase()) {
         final Map<Date, Long> measures = new HashMap<>();
         final List<Datacollector> collectorMap = tct.getDatacollector();
         Datacollector collector = null;
         for (final Datacollector dc : collectorMap) {
            if (dc.getName().equals(collectorName)) {
               collector = dc;
            }
         }
         if (collector == null) {
            LOG.error("Achtung: Datenkollektor " + collectorName + " nicht vorhanden");
         } else {
            for (final Result s : collector.getResult()) {
               measures.put(new Date(s.getDate()), (long) s.getValue());
            }
            map.put(tct.getName(), measures);
         }
      }
      return map;
   }

   /**
    * Returns all datacollectors that are used in the resultfile.
    * 
    * @return Names of all datacollectors
    */
   public Set<String> getCollectors() {
      final Set<String> collectors = new HashSet<String>();
      final Testcases testcases = data.getTestcases();
      for (final TestcaseType tct : testcases.getTestcase()) {
         for (final Datacollector collector : tct.getDatacollector()) {
            collectors.add(collector.getName());
         }
      }
      return collectors;
   }

   /**
    * Returns all data.
    * 
    * @return Object containing all data from the file
    */
   public Kopemedata getFullData() {
      return data;
   }

   public static Kopemedata loadData(final File dataFile) throws JAXBException {
      final Unmarshaller unmarshaller = jc.createUnmarshaller();
      final Kopemedata data = (Kopemedata) unmarshaller.unmarshal(dataFile);
      return data;
   }
   
   public static Kopemedata loadWarmedupData(final File dataFile) throws JAXBException {
      final Unmarshaller unmarshaller = jc.createUnmarshaller();
      final Kopemedata data = (Kopemedata) unmarshaller.unmarshal(dataFile);
      for (TestcaseType testcase : data.getTestcases().getTestcase()) {
         for (Result result : testcase.getDatacollector().get(0).getResult()) {
            if (result.getFulldata().getFileName() != null) {
               Fulldata replacedFulldata = readFulldata(dataFile, (int) (result.getExecutionTimes() / 2), testcase, result);
               result.setFulldata(replacedFulldata);
            }
         }
      }
      return data;
   }

   public static Kopemedata loadData(final File dataFile, int warmup) throws JAXBException {
      final Unmarshaller unmarshaller = jc.createUnmarshaller();
      final Kopemedata data = (Kopemedata) unmarshaller.unmarshal(dataFile);
      for (TestcaseType testcase : data.getTestcases().getTestcase()) {
         for (Result result : testcase.getDatacollector().get(0).getResult()) {
            if (result.getFulldata().getFileName() != null) {
               Fulldata replacedFulldata = readFulldata(dataFile, warmup, testcase, result);
               result.setFulldata(replacedFulldata);
            }
         }
      }

      return data;
   }

   public void readFulldataValues() {
      for (TestcaseType testcase : data.getTestcases().getTestcase()) {
         for (Datacollector collector : testcase.getDatacollector()) {
            for (Chunk chunk : collector.getChunk()) {
               for (Result result : chunk.getResult()) {
                  replaceFulldata(collector, result);
               }
            }
            for (Result result : collector.getResult()) {
               replaceFulldata(collector, result);
            }
         }
      }
   }

   private void replaceFulldata(Datacollector collector, Result result) {
      if (result.getFulldata() != null && result.getFulldata().getFileName() != null) {
         File dataFile = new File(file.getParentFile(), result.getFulldata().getFileName());
         
         Fulldata replacedFulldata = executeReading(collector.getName(), dataFile, 0);
         result.setFulldata(replacedFulldata);
      }
   }

   private static Fulldata readFulldata(File xmlFile, int warmup, TestcaseType testcase, Result result) {
      final File file = new File(result.getFulldata().getFileName());
      final File dataFile = new File(xmlFile.getParentFile(), file.getName());

      Fulldata replacedFulldata = executeReading(testcase.getDatacollector().get(0).getName(), dataFile, warmup);
      return replacedFulldata;
   }

   private static Fulldata executeReading(String currentDatacollector, File dataFile, int warmup) {
      final WrittenResultReader reader = new WrittenResultReader(dataFile);
      final Set<String> dataCollectors = new HashSet<>();
      dataCollectors.add(currentDatacollector);
      reader.read(null, dataCollectors);
      
      Fulldata replacedFulldata = reader.createFulldata(warmup, currentDatacollector);
      return replacedFulldata;
   }

}
