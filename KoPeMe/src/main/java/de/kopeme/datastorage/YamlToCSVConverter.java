package de.kopeme.datastorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.yaml.snakeyaml.Yaml;

public class YamlToCSVConverter {
	private Yaml y;
	private Map<String, Map<Date, Long>> data;
	
	public YamlToCSVConverter(String filename)
	{
		File f = new File(filename);
		InputStream is;
		try {
			is = new FileInputStream(f);
			y = new Yaml();
			Object o = y.load(is);
			data = (Map<String, Map<Date, Long>>) o;
		} catch (FileNotFoundException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
		
	}
	
	public void writeTo(String outputFilename, String nameOfParameter)
	{
		File outpoutFile = new File(outputFilename);
		try {
			BufferedWriter bw = new BufferedWriter( new FileWriter(outpoutFile));
			Map<Date, Long> m = data.get(nameOfParameter);
			
			if ( m != null )
			{
				for ( Map.Entry<Date, Long> entry : m.entrySet() )
				{
					bw.write(entry.getKey() + ", " + entry.getValue() + "\n");
				}
			}
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Set<String> getParameters()
	{
		return data.keySet();
	}
	
	public static void main( String args[] )
	{
		String filename = args[0];
		if ( args.length > 2 )
		{
			String outputFile = args[1];
			String parameter = args[2];
			YamlToCSVConverter ytcc = new YamlToCSVConverter(filename);
			ytcc.writeTo(outputFile, parameter);
		}
		else
		{
			YamlToCSVConverter ytcc = new YamlToCSVConverter(filename);
			for ( String parameter: ytcc.getParameters() )
			{
				ytcc.writeTo(parameter.replace(" ", "").replace(".", "") + ".csv", parameter);
			}
		}
		
	}
}
