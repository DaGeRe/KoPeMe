package de.kopeme.datastorage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

public class YAMLDataStorer implements DataStorer{
	
	private Map<String, Map<Date, Long>> data;
	private File f;
	
	public YAMLDataStorer( String filename )
	{
		loadData(filename);
		f = new File(filename);
	}
	
	private void loadData( String filename )
	{
//		data = new HashMap<String, Map<Date, Long>>();
		YAMLDataLoader xdl = new YAMLDataLoader(filename);
		data = xdl.getData();
	}
	
	public Map<Date, Long> getHistoricalData(String measurementName)
	{
		return data.get(measurementName);
	}
	
	@Override
	public void storeValue(String name, long value) {
		Map<Date, Long> dataList = data.get(name);
		System.out.println("Speichere " + value + " für " + name);
		if ( dataList == null )
			dataList = new HashMap<Date, Long>();
		dataList.put(new Date(), value);
		data.put(name, dataList);
	}
	
	public void storeData()
	{
		Representer r = new Representer();
		DumperOptions dumperO = new DumperOptions();
		dumperO.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		Yaml yam = new Yaml(r, dumperO);
		
		FileWriter fw;
		try {
			fw = new FileWriter(f);
			fw.write(yam.dump(data));
			fw.flush();
			System.out.println("Schreibe: " + data.size() + " Werte in " + f.getPath());
		} catch (IOException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
	}
	
}
