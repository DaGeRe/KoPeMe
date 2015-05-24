package kieker.monitoring.writer.filesystem;

import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;

import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.writer.AbstractMonitoringWriter;

public class ChangeableFolderSyncFsWriter extends AbstractMonitoringWriter {
	
	public static final String PREFIX = ChangeableFolderSyncFsWriter.class.getName() + ".";
	public static final String CONFIG_PATH = PREFIX + "customStoragePath"; // NOCS (afterPREFIX)
	public static final String CONFIG_MAXENTRIESINFILE = PREFIX + "maxEntriesInFile"; // NOCS (afterPREFIX)
	public static final String CONFIG_MAXLOGSIZE = PREFIX + "maxLogSize"; // NOCS (afterPREFIX)
	public static final String CONFIG_MAXLOGFILES = PREFIX + "maxLogFiles"; // NOCS (afterPREFIX)
	public static final String CONFIG_FLUSH = PREFIX + "flush"; // NOCS (afterPREFIX)
	public static final String CONFIG_BUFFER = PREFIX + "bufferSize"; // NOCS (afterPREFIX)
	
	private static ChangeableFolderSyncFsWriter INSTANCE = null;

	public static synchronized ChangeableFolderSyncFsWriter getInstance(){
		return INSTANCE;
	}
	
	private SyncFsWriter defaultWriter;
	private SyncFsWriter currentWriter;
	private Configuration configuration;
	
	public ChangeableFolderSyncFsWriter(Configuration configuration) {
		super(configuration);
		this.configuration = configuration;
		defaultWriter = new SyncFsWriter(toSyncFsWriterConfiguration(configuration));
		if(INSTANCE != null){
			throw new IllegalArgumentException("this is a bad singleton, so use it as one!");
		}
		INSTANCE = this;
	}

	Configuration toSyncFsWriterConfiguration(Configuration c){
		Configuration returnable = new Configuration();
		for (Iterator<Entry<Object, Object>> iterator = c.entrySet().iterator(); iterator.hasNext();) {
			Entry<Object, Object> entry = iterator.next();
			String keyAsString = entry.getKey().toString();
			String replacedPropertyName = keyAsString.replace(getClass().getName(), SyncFsWriter.class.getName());
			returnable.setProperty(replacedPropertyName, entry.getValue().toString());
		}
		return returnable;
	}
	
	@Override
	public synchronized boolean newMonitoringRecord(IMonitoringRecord record) {
		if(currentWriter == null){
			return defaultWriter.newMonitoringRecord(record);
		} else {
			return currentWriter.newMonitoringRecord(record);
		}
		
	}

	@Override
	public synchronized void terminate() {
		defaultWriter.terminate();
		if(currentWriter != null){
			currentWriter.terminate();
		}
			
	}

	@Override
	protected void init() throws Exception {
		defaultWriter.setController(monitoringController);
	}
	
	public synchronized void setFolder(File writingFolder) throws Exception{
		if(currentWriter != null){
			currentWriter.terminate();
		} 
		writingFolder.mkdirs();
		String absolutePath = writingFolder.getAbsolutePath();
		Configuration resultingConfig = createTempConfigWithNewFolder(absolutePath);
		currentWriter = new SyncFsWriter(resultingConfig);
		currentWriter.setController(monitoringController);
	}

	private Configuration createTempConfigWithNewFolder(String absolutePath) {
		Configuration tempConfig = toSyncFsWriterConfiguration(configuration);
		tempConfig.setProperty(SyncFsWriter.CONFIG_PATH, absolutePath);
		return tempConfig;
	}
	
	public synchronized void reset(){
		if(currentWriter != null){
			currentWriter.terminate();
		}
		currentWriter = null;
	}

	public IMonitoringController getController(){
		return monitoringController;
	}
}
