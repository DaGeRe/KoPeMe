package de.dagere.kopeme.kieker;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

/**
 * Class representing an entry in the kieker csv data file.
 * 
 * @author dhaeb
 *
 */
public class KiekerTraceEntry {

	private static String[] FIELDS;  
	
	/**
	 * Returns a description for the Super CSV parser, how to map a column of the csv to this type. 
	 * 
	 * @return array with field names representing the order in the csv
	 */
	public static String[] getFieldDescription() {
		if(FIELDS == null){
			ArrayList<String> fields = new ArrayList<String>();
			for(Field field : KiekerTraceEntry.class.getDeclaredFields()){
				if(!Modifier.isStatic(field.getModifiers())){
					fields.add(field.getName());
				}
			}
			FIELDS = fields.toArray(new String[fields.size()]);
		}
		return FIELDS;
	}
	
	/**
	 * Provide this array to parse the columns of the csv into the right type using the Super CSV framework. 
	 * 
	 * @return
	 */
	public static CellProcessor[] getCellProcessors(){
		return new CellProcessor[] {
			new ParseInt(){
				@Override
				public Object execute(Object value, CsvContext context) {
					String content = value.toString();
					String[] split = content.split("\\$");
					return super.execute(split[1], context);
				}
			},
			new ParseLong(),
			null,
			null,
			new ParseLong(),
			new ParseLong(),
			new ParseLong(),
			null,
			new ParseInt(),
			new ParseInt()
		};
	}
	
	private int mappningNumber;
	private long loggingTime;
	private String entryName;
	private String sessionId;
	private long traceId;
	private long tin;
	private long tout;
	private String host;
	private int eoi;
	private int ess;

	public int getMappningNumber() {
		return mappningNumber;
	}

	public void setMappningNumber(int mappningNumber) {
		this.mappningNumber = mappningNumber;
	}

	public long getLoggingTime() {
		return loggingTime;
	}

	public void setLoggingTime(long loggingTime) {
		this.loggingTime = loggingTime;
	}

	public String getEntryName() {
		return entryName;
	}

	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public long getTraceId() {
		return traceId;
	}

	public void setTraceId(long traceId) {
		this.traceId = traceId;
	}

	public long getTin() {
		return tin;
	}

	public void setTin(long tin) {
		this.tin = tin;
	}

	public long getTout() {
		return tout;
	}

	public void setTout(long tout) {
		this.tout = tout;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getEoi() {
		return eoi;
	}

	public void setEoi(int eoi) {
		this.eoi = eoi;
	}

	public int getEss() {
		return ess;
	}

	public void setEss(int ess) {
		this.ess = ess;
	}
	
}
