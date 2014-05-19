package de.dagere.kopeme.datastorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;

public class XMLDataLoader implements DataLoader {
	private static final Logger log = LogManager.getLogger(XMLDataLoader.class);
	
	private File f;
	private Kopemedata data;

	public XMLDataLoader(String filename) {
		f = new File(filename);
		log.info("Laden von {}", f.getAbsoluteFile());
		
		if (!f.exists()) {
			log.info("Datei existiert nicht");
			data = new Kopemedata();
		}else{
			JAXBContext jc;
			try {
				jc = JAXBContext.newInstance(Kopemedata.class);
				Unmarshaller unmarshaller = jc.createUnmarshaller();
				data = (Kopemedata) unmarshaller.unmarshal(f);
				log.info("Daten geladen, Daten: " + data);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
		}
		

	}

	@Override
	public Map<String, Map<Date, Long>> getData() {
		Map<String, Map<Date, Long>> map = new HashMap<>();
		for (TestcaseType tct : data.getTestcases().getTestcase()){
			Map<Date, Long> measures = new HashMap<>();
			for (Result s : tct.getDatacollector().get(0).getResult() ){
				measures.put(new Date(s.getDate()), new Long(s.getValue()));
			}
			map.put(tct.getName(), measures);
		}
		return map;
	}

	public Kopemedata getFullData() {
		return data;
	}

}