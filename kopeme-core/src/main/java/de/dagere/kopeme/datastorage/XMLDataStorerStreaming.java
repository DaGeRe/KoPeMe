package de.dagere.kopeme.datastorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Fulldata;
import de.dagere.kopeme.generated.Result.Fulldata.Value;

public class XMLDataStorerStreaming implements DataStorer {

   File file;

   public XMLDataStorerStreaming(File file) {
      this.file = file;

   }

   @Override
   public void storeValue(final Result performanceDataMeasure, String testcase, String collectorName) {

      // TODO Test correctness testcase + collectorName
      final File temporaryFile = new File(file.getParentFile(), "temp.xml");
      try {
         SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory.newInstance();
         TransformerHandler serializer = factory.newTransformerHandler();

         StreamResult result = new StreamResult(temporaryFile);
         serializer.setResult(result);

         XMLFilterImpl filter = new XMLFilterImpl() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
               super.startElement(uri, localName, qName, atts);
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
               if ("datacollector".equals(localName)) {
                  super.startElement("", "result", "result", new AttributesImpl());
                  newline();

                  writeFields(performanceDataMeasure);

                  if (performanceDataMeasure.getFulldata() != null) {
                     writeFulldata(performanceDataMeasure);
                  }
                  indent3();
                  super.endElement("", "result", "result");
                  newline();
               }
               super.endElement(uri, localName, qName);
            }

            private void writeFulldata(final Result result) throws SAXException {
               indent4();
               super.startElement("", "fulldata", "fulldata", new AttributesImpl());
               newline();
               for (Value value : result.getFulldata().getValue()) {
                  AttributesImpl attributes = new AttributesImpl();
                  attributes.addAttribute("", "start", "start", "CDATA", "" + value.getStart());
                  indent5();
                  super.startElement("", "value", "value", attributes);
                  super.characters(value.getValue().toCharArray(), 0, value.getValue().length());
                  super.endElement("", "value", "value");
                  newline();
               }
               indent4();
               super.endElement("", "fulldata", "fulldata");
               newline();
            }

            private void writeFields(final Result result) throws SAXException {
               writeField("value", "" + result.getValue());
               writeField("deviation", "" + result.getDeviation());
               writeField("min", "" + result.getMin());
               writeField("max", "" + result.getMax());
               writeField("warmupExecutions", "" + result.getWarmupExecutions());
               writeField("repetitions", "" + result.getRepetitions());
               writeField("executionTimes", "" + result.getExecutionTimes());
            }

            private void indent3() throws SAXException {
               super.characters("   ".toCharArray(), 0, "   ".length());
            }

            private void indent4() throws SAXException {
               super.characters("    ".toCharArray(), 0, "    ".length());
            }

            private void indent5() throws SAXException {
               super.characters("     ".toCharArray(), 0, "     ".length());
            }

            private void newline() throws SAXException {
               super.characters("\n".toCharArray(), 0, "\n".length());
            }

            private void writeField(final String attributeName, final String attributeValue) throws SAXException {
               indent3();
               super.startElement("", attributeName, attributeName, new AttributesImpl());
               super.characters(attributeValue.toCharArray(), 0, attributeValue.length());
               super.endElement("", attributeName, attributeName);
               newline();
            }
         };
         filter.setContentHandler(serializer);

         XMLReader xmlreader = XMLReaderFactory.createXMLReader();
         xmlreader.setContentHandler(filter);

         xmlreader.parse(new InputSource(new FileInputStream(file)));

         file.delete();
         temporaryFile.renameTo(file);

      } catch (TransformerConfigurationException | SAXException e) {
         e.printStackTrace();
      } catch (FileNotFoundException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void storeData(final File file, final Result additionalResult, String testcase, String collectorName) {
      final XMLDataStorerStreaming xmlDataStorerStreaming = new XMLDataStorerStreaming(file);
      xmlDataStorerStreaming.storeValue(additionalResult, testcase, collectorName);
   }

}
