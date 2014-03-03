package de.kopeme.datastorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;


public class YAMLDataLoader implements DataLoader{

	File f;
	
	public YAMLDataLoader( String filename )
	{
		f = new File( filename );
	}
	
	@Override
	public Map<String, Map<Date, Long>> getData() {
		try {
			if ( !f.exists() )
			{
				Map<String, Map<Date, Long>> m = new HashMap<String, Map<Date,Long>>();
				return m;
			}
			InputStream is = new FileInputStream(f);
			Yaml yaml = new Yaml();
			Object o = yaml.load(is);
			if ( o instanceof Map )
			{
				Map<String, Map<Date, Long>> m = (Map<String, Map<Date, Long>>) o;
				return m;
			}
		} catch (FileNotFoundException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
		// TODO Automatisch generierter Methodenstub
		return null;
	}
}
