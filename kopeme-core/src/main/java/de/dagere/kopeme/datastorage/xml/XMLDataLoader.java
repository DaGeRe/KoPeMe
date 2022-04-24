package de.dagere.kopeme.datastorage.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.xml.bind.v2.ContextFactory;

import de.dagere.kopeme.datastorage.DataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.ObjectFactory;

/**
 * Loads XML-Performance-Data
 * 
 * This is only for converting old XML data and should not be used anymore!
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
         jc = ContextFactory.createContext(
               ObjectFactory.class.getPackage().getName(),
               ObjectFactory.class.getClassLoader(), null);
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

   public static Kopemedata loadData(final File dataFile) throws JAXBException {
      final Unmarshaller unmarshaller = jc.createUnmarshaller();
      final Kopemedata data = (Kopemedata) unmarshaller.unmarshal(dataFile);
      return data;
   }


}
