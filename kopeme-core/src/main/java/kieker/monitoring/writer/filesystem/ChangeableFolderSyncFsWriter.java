package kieker.monitoring.writer.filesystem;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.common.record.misc.RegistryRecord;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.writer.AbstractMonitoringWriter;

/**
 * This class enables Kieker writing in different folders for KoPeMe purposes. It does so by creating a new {@link SyncFsWriter} with every new folder that is
 * set to the {@link ChangeableFolderSyncFsWriter}. For storing all mapping data that is produced, every {@link RegistryRecord} that is measured is saved to a
 * List and written to every new {@link SyncFsWriter} that is created with a new folder.
 * 
 * @author reichelt
 *
 */
public class ChangeableFolderSyncFsWriter extends AbstractMonitoringWriter {

	public static final String PREFIX = ChangeableFolderSyncFsWriter.class.getName() + ".";
	public static final String CONFIG_PATH = PREFIX + "customStoragePath";
	public static final String CONFIG_MAXENTRIESINFILE = PREFIX + "maxEntriesInFile";
	public static final String CONFIG_MAXLOGSIZE = PREFIX + "maxLogSize";

	public static final String CONFIG_MAXLOGFILES = PREFIX + "maxLogFiles";
	public static final String CONFIG_FLUSH = PREFIX + "flush";
	public static final String CONFIG_BUFFER = PREFIX + "bufferSize";

	private static final Map<IMonitoringController, ChangeableFolderSyncFsWriter> instanceMapping = new HashMap<>();

	public static synchronized ChangeableFolderSyncFsWriter getInstance(final IMonitoringController controler) {
		return instanceMapping.get(controler);
	}

	private final List<RegistryRecord> mappingRecords = new LinkedList<>();
	private final Configuration configuration;
	private static final Logger LOG = Logger.getLogger(ChangeableFolderSyncFsWriter.class.getName());
	private SyncFsWriter currentWriter;

	public ChangeableFolderSyncFsWriter(final Configuration configuration) {
		super(configuration);
		LOG.info("Initialisiere..");
		this.configuration = configuration;
		currentWriter = new SyncFsWriter(toSyncFsWriterConfiguration(configuration));
	}

	Configuration toSyncFsWriterConfiguration(final Configuration c) {
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
	public synchronized boolean newMonitoringRecord(final IMonitoringRecord record) {
		
		if (record instanceof RegistryRecord) {
			mappingRecords.add((RegistryRecord) record);
		}
//		else{
//			if (record instanceof OperationExecutionRecord){
//				OperationExecutionRecord operationExecutionRecord = (OperationExecutionRecord)record;
//				System.out.println("schreibe: " + operationExecutionRecord.getOperationSignature() + " ");
//			}
//		}
		if (currentWriter == null) {
			return true;
		} else {
			return currentWriter.newMonitoringRecord(record);
		}
	}

	@Override
	public synchronized void terminate() {
		if (currentWriter != null) {
			currentWriter.terminate();
		}
	}

	@Override
	protected void init() throws Exception {
		currentWriter.setController(monitoringController);
		instanceMapping.put(monitoringController, this);
	}

	public synchronized void setFolder(final File writingFolder) throws Exception {
		if (currentWriter != null) {
			currentWriter.terminate();
		}
		writingFolder.mkdirs();
		String absolutePath = writingFolder.getAbsolutePath();
		Configuration resultingConfig = createTempConfigWithNewFolder(absolutePath);
		currentWriter = new SyncFsWriter(resultingConfig);
		currentWriter.setController(monitoringController);
		for (RegistryRecord record : mappingRecords) {
			LOG.info("Füge registery records dem neuen fs writer hinzu: " + record);
			currentWriter.newMonitoringRecord(record);
		}
	}

	private Configuration createTempConfigWithNewFolder(final String absolutePath) {
		Configuration tempConfig = toSyncFsWriterConfiguration(configuration);
		tempConfig.setProperty(SyncFsWriter.CONFIG_PATH, absolutePath);
		return tempConfig;
	}

	public IMonitoringController getController() {
		return monitoringController;
	}
}
