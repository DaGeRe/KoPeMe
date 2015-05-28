package de.dagere.kopeme.datastorage;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result.Fulldata;

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
	public XMLDataStorer(final String foldername, final String classname, final String methodname) throws JAXBException {
		String filename = classname + "." + methodname + ".xml";
		file = new File(foldername + File.separator + filename);
		if (file.exists()) {
			XMLDataLoader loader = new XMLDataLoader(file);
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
	public void createXMLData(final String classname) {
		data = new Kopemedata();
		data.setTestcases(new Testcases());
		Testcases tc = data.getTestcases();
		tc.setClazz(classname);
		storeData();
	}

	@Override
	public void storeValue(final String name, final long value) {
		LOG.error("Speichere Wert falsch");
	}

	@Override
	public void storeValue(final PerformanceDataMeasure performanceDataMeasure, final List<Long> values) {
		TestcaseType test = null;
		if (data.getTestcases() == null) data.setTestcases(new Testcases());
		for (TestcaseType tc : data.getTestcases().getTestcase()) {
			if (tc.getName().equals(performanceDataMeasure.testcase)) {
				test = tc;
			}
		}
		if (test == null) {
			LOG.trace("Test == null, füge hinzu");
			test = new TestcaseType();
			test.setName(performanceDataMeasure.testcase);
			data.getTestcases().getTestcase().add(test);
		}

		Result r = new Result();
		r.setDate(new Date().getTime());
		r.setValue("" + performanceDataMeasure.value);
		r.setDeviation(performanceDataMeasure.deviation);
		r.setExecutionTimes(performanceDataMeasure.executionTimes);
		r.setMax(performanceDataMeasure.max);
		r.setMin(performanceDataMeasure.min);
		r.setFirst10Percentile(performanceDataMeasure.first10percentile);
		if (values != null) {
			Fulldata fd = new Fulldata();
			for (Long l : values) {
				fd.getValue().add("" + l);
			}
			r.setFulldata(fd);
		}

		Datacollector dc = null;
		for (Datacollector dc2 : test.getDatacollector()) {
			LOG.trace("Name: {} Collectorname: {}", dc2.getName(), performanceDataMeasure.collectorname);
			if (dc2.getName().equals(performanceDataMeasure.collectorname)) {
				LOG.trace("Equals");
				dc = dc2;
			}
		}

		if (dc == null) {
			LOG.trace("Erstelle neu");
			dc = new Datacollector();
			dc.setName(performanceDataMeasure.collectorname);
			test.getDatacollector().add(dc);
		}
		dc.getResult().add(r);
	}

	@Override
	public void storeData() {
		JAXBContext jaxbContext;
		try {
			LOG.info("Storing data to: {}", file.getAbsoluteFile());
			jaxbContext = JAXBContext.newInstance(Kopemedata.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			jaxbMarshaller.marshal(data, file);
		} catch (JAXBException e) {
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
		JAXBContext jaxbContext;
		try {
			// log.debug("Storing data to: {}", file.getAbsoluteFile());
			jaxbContext = JAXBContext.newInstance(Kopemedata.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			jaxbMarshaller.marshal(currentdata, file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
