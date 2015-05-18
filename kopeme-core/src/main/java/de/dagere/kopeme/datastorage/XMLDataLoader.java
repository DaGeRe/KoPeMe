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

import de.dagere.kopeme.PerformanceTestUtils;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;

/**
 * Loads XML-Performance-Data.
 * 
 * @author reichelt
 *
 */
public final class XMLDataLoader implements DataLoader {
	private static final Logger LOG = LogManager.getLogger(XMLDataLoader.class);
	private File file;
	private Kopemedata data;

	/**
	 * Initializes the XMLDataLoader with the given file.
	 * 
	 * @param f
	 * @throws JAXBException
	 */
	public XMLDataLoader(final File f) throws JAXBException {
		this.file = f;
		loadData();
	}

	/**
	 * Loads the data.
	 * 
	 * @throws JAXBException
	 */
	private void loadData() throws JAXBException {

		if (!file.getAbsolutePath().contains(PerformanceTestUtils.PERFORMANCEFOLDER)) {
			File temp = new File(file.getParentFile(), PerformanceTestUtils.PERFORMANCEFOLDER + "/" + file.getName());
			if (temp.exists()) {
				file = temp;
			}
		}

		if (!file.exists()) {
			LOG.info("Datei {} existiert nicht", file.getAbsolutePath());
			data = new Kopemedata();
			data.setTestcases(new Testcases());
			Testcases tc = data.getTestcases();
			LOG.trace("TC: " + tc);
			tc.setClazz(file.getName());
		} else {
			JAXBContext jc;
			jc = JAXBContext.newInstance(Kopemedata.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			data = (Kopemedata) unmarshaller.unmarshal(file);
			LOG.trace("Daten geladen, Daten: " + data);
		}
	}

	@Override
	public Map<String, Map<Date, Long>> getData() {
		Map<String, Map<Date, Long>> map = new HashMap<>();
		Testcases testcases = data.getTestcases();
		for (TestcaseType tct : testcases.getTestcase()) {
			Map<Date, Long> measures = new HashMap<>();
			for (Result s : tct.getDatacollector().get(0).getResult()) {
				measures.put(new Date(s.getDate()), new Long(s.getValue()));
			}
			map.put(tct.getName(), measures);
		}
		return map;
	}

	/**
	 * Returns a mapping from all testcases to their results for a certain collectorName
	 * 
	 * @param collectorName The name of the collector for loading the Results
	 * @return Mapping from all testcases to their results
	 */
	public Map<String, Map<Date, Long>> getData(final String collectorName) {
		Map<String, Map<Date, Long>> map = new HashMap<>();
		Testcases testcases = data.getTestcases();
		for (TestcaseType tct : testcases.getTestcase()) {
			Map<Date, Long> measures = new HashMap<>();
			List<Datacollector> collectorMap = tct.getDatacollector();
			Datacollector collector = null;
			for (Datacollector dc : collectorMap) {
				if (dc.getName().equals(collectorName)) {
					collector = dc;
				}
			}
			if (collector == null) {
				LOG.error("Achtung: Datenkollektor " + collectorName + " nicht vorhanden");
			} else {
				for (Result s : collector.getResult()) {
					measures.put(new Date(s.getDate()), new Long(s.getValue()));
				}
				map.put(tct.getName(), measures);
			}
		}
		return map;
	}

	/**
	 * Returns all datacollectors that are used in the resultfile
	 * 
	 * @return
	 */
	public Set<String> getCollectors() {
		Set<String> collectors = new HashSet<String>();
		Testcases testcases = data.getTestcases();
		for (TestcaseType tct : testcases.getTestcase()) {
			for (Datacollector collector : tct.getDatacollector()) {
				collectors.add(collector.getName());
			}
		}
		return collectors;
	}

	/**
	 * Returns all data
	 * 
	 * @return Object containing all data from the file
	 */
	public Kopemedata getFullData() {
		return data;
	}

}
