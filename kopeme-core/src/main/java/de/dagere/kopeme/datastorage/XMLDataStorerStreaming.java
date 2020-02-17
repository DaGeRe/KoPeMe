package de.dagere.kopeme.datastorage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

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
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXWriter;
import org.dom4j.io.XMLWriter;
import org.dom4j.tree.DefaultElement;
import org.xml.sax.SAXException;

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
   public void storeData() {
      // TODO Auto-generated method stub

   }

   @Override
   public void storeValue(PerformanceDataMeasure performanceDataMeasure, Fulldata fulldata) {

   }

   public void storeValue(Result additionalResult) throws DocumentException {
      System.out.println("Store: " + file.getAbsolutePath());
      SAXReader reader = new SAXReader();
      Document document = reader.read(file);

      final XPath xpath = DocumentHelper.createXPath("/kopemedata/testcases/testcase/datacollector");
      List<Node> nodes = xpath.selectNodes(document);

      DefaultElement node = (DefaultElement) nodes.get(0);
      final DefaultElement result = new DefaultElement("result");

      addField(result, "value", "" + additionalResult.getValue());
      addField(result, "deviation", "" + additionalResult.getDeviation());
      addField(result, "min", "" + additionalResult.getMin());
      addField(result, "max", "" + additionalResult.getMax());
      addField(result, "warmupExecutions", "" + additionalResult.getWarmupExecutions());
      addField(result, "repetitions", "" + additionalResult.getRepetitions());
      addField(result, "executionTimes", "" + additionalResult.getExecutionTimes());
      
      buildFulldata(additionalResult, result);

      node.add(result);

      writeChangedXML(document);
   }

   private void addField(final DefaultElement result, String name, String value) {
      final DefaultElement valueMean = new DefaultElement(name);
      valueMean.addText(value);
      result.add(valueMean);
   }

   private void writeChangedXML(Document document) {
      try {
         final XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file));
         xmlWriter.write(document);
         xmlWriter.flush();

         new XMLWriter(System.out).write(document);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private void buildFulldata(Result additionalResult, final DefaultElement result) {
      if (additionalResult.getFulldata() != null) {
         final DefaultElement fulldata = new DefaultElement("fulldata");
         for (Value value : additionalResult.getFulldata().getValue()) {
            final Element xmlValue = fulldata.addElement("value");
            xmlValue.addAttribute("start", "" + value.getStart());
            xmlValue.setText(value.getValue());
         }
         result.add(fulldata);
      }
   }

   public static void storeData(final File file, final Result additionalResult) {
      final XMLDataStorerStreaming xmlDataStorerStreaming = new XMLDataStorerStreaming(file);
      try {
         xmlDataStorerStreaming.storeValue(additionalResult);
      } catch (DocumentException e) {
         e.printStackTrace();
      }
   }

}
