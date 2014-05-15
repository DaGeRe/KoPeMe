package de.dagere.kopeme.datastorage;

import java.io.File;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.corba.se.spi.orb.DataCollector;

import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;

/**
 * Manages the storing of resultdata of KoPeMe-tests in the
 * KoPeMe-XML-format
 * @author reichelt
 *
 */
public class XMLDataStorer implements DataStorer{

	Logger log = LogManager.getLogger(XMLDataStorer.class);
	
	private File f;
	private Kopemedata data;
	
	public XMLDataStorer( String classname )
	{
		String filename = classname+ ".yaml";
		XMLDataLoader loader = new XMLDataLoader(filename);
		data = loader.getFullData();
		f = new File(filename);
	}

	@Override
	public void storeValue(String name, long value) {
		log.error("Speichere Wert falsch");
	}
	
	public void storeValue(String testcase, String collectorname, long value, double deviation, int executionTimes, long min, long max) {
		TestcaseType test = null;
		if (data.getTestcases() == null)
			data.setTestcases(new Testcases());
		for (TestcaseType tc : data.getTestcases().getTestcase()){
			
			if (tc.getName().equals(testcase)){
				test = tc;
			}
		}
		if (test == null){
			log.debug("Test == null, füge hinzu");
			test = new TestcaseType();
			test.setName(testcase);
			data.getTestcases().getTestcase().add(test);
		}
		
		Result r = new Result();
		r.setDate(new Date().getTime());
		r.setValue(""+value);
		r.setDeviation(deviation);
		r.setExecutionTimes(executionTimes);
		
		Datacollector dc = null;
		for (Datacollector dc2 : test.getDatacollector()){
			System.out.println("Name: " + dc2.getName() + " Collectorname: " + collectorname);
			if (dc2.getName().equals(collectorname)){
				System.out.println("Equals");
				dc = dc2;
			}
		}
		
		if (dc == null){
			System.out.println("Erstelle neu");
			dc = new Datacollector();
			dc.setName(collectorname);
			test.getDatacollector().add(dc);
		}
		dc.getResult().add(r);
	}
	
	
	@Override
	public void storeData() {
		JAXBContext jaxbContext;
		try {
			log.info("Storing data to: {}", f.getAbsoluteFile());
			jaxbContext = JAXBContext.newInstance(Kopemedata.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );  
			
			jaxbMarshaller.marshal(data, f);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
}
