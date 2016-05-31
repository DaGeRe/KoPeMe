package de.dagere.kopeme.kieker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.supercsv.io.CsvBeanReader;
import org.supercsv.prefs.CsvPreference;

/**
 * Class containing the in memory representation of a kieker trace file.
 * We use a mapping of the method names with a head / tail list, to represent the stack. 
 * 
 * @author dhaeb
 *
 */
public class KiekerTrace {

	private Map<String, List<KiekerTraceEntry>> entries = new HashMap<>();
	
	public KiekerTrace(final InputStream resource) throws IOException {
		try(CsvBeanReader reader = new CsvBeanReader(new BufferedReader(new InputStreamReader(resource)), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE)){
			KiekerTraceEntry currentEntry = null; 
			while((currentEntry = getNextKiekerTraceEntry(reader)) != null){
				handleEntry(currentEntry);
			}
		}
	}

	private KiekerTraceEntry getNextKiekerTraceEntry(final CsvBeanReader reader) throws IOException {
		return reader.read(KiekerTraceEntry.class, KiekerTraceEntry.getFieldDescription(), KiekerTraceEntry.getCellProcessors());
	}

	private void handleEntry(KiekerTraceEntry currentEntry) {
		String entryName = currentEntry.getEntryName();
		List<KiekerTraceEntry> addable;
		if(entries.containsKey(entryName)){
			addable = entries.get(entryName);
		} else {
			addable = new ArrayList<>();
		}
		addable.add(0, currentEntry);
		entries.put(entryName, addable);
	}

	public Map<String, List<KiekerTraceEntry>> getEntries() {
		return entries;
	}

}
