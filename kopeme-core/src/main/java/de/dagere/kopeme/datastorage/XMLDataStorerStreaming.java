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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.InvalidXPathException;
import org.dom4j.Node;
import org.dom4j.Visitor;
import org.dom4j.XPath;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
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
                  attributes.addAttribute("", "start", "start", "CDATA", value.getValue());
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

      // try {
      // SAXReader reader = new SAXReader();
      // Document document = reader.read(file);
      //
      // final XPath xpath = DocumentHelper.createXPath("/kopemedata/testcases/testcase/datacollector");
      // List<Node> nodes = xpath.selectNodes(document);
      //
      // DefaultElement node = (DefaultElement) nodes.get(0);
      // final DefaultElement result = new DefaultElement("result");
      //
      // addField(result, "value", "" + performanceDataMeasure.getValue());
      // addField(result, "deviation", "" + performanceDataMeasure.getDeviation());
      // addField(result, "min", "" + performanceDataMeasure.getMin());
      // addField(result, "max", "" + performanceDataMeasure.getMax());
      // addField(result, "warmupExecutions", "" + performanceDataMeasure.getWarmupExecutions());
      // addField(result, "repetitions", "" + performanceDataMeasure.getRepetitions());
      // addField(result, "executionTimes", "" + performanceDataMeasure.getExecutionTimes());
      //
      // buildFulldata(performanceDataMeasure.getFulldata(), result);
      //
      // node.add(result);
      //
      // writeChangedXML(document);
      // } catch (DocumentException e) {
      // e.printStackTrace();
      // }
   }

   private void addField(final DefaultElement result, String name, String value) {
      final DefaultElement valueMean = new DefaultElement(name);
      valueMean.addText(value);
      result.add(valueMean);
   }

   private void writeChangedXML(Document document) {
      try {
         OutputFormat outformat = OutputFormat.createPrettyPrint();
         outformat.setEncoding("UTF-8");
         outformat.setIndentSize(1);
         final XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file), outformat);
         xmlWriter.write(document);
         xmlWriter.flush();

         // new XMLWriter(System.out, outformat).write(document);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private void buildFulldata(Fulldata fulldata, final DefaultElement result) {
      if (fulldata != null) {
         final DefaultElement xmlFulldata = new DefaultElement("fulldata");
         for (Value value : fulldata.getValue()) {
            final Element xmlValue = xmlFulldata.addElement("value");
            xmlValue.addAttribute("start", "" + value.getStart());
            xmlValue.setText(value.getValue());
         }
         result.add(xmlFulldata);
      }
   }

   public static void storeData(final File file, final Result additionalResult, String testcase, String collectorName) {
      final XMLDataStorerStreaming xmlDataStorerStreaming = new XMLDataStorerStreaming(file);
      xmlDataStorerStreaming.storeValue(additionalResult, testcase, collectorName);
   }

}
