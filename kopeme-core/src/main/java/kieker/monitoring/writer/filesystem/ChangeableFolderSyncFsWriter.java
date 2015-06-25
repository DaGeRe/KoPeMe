package kieker.monitoring.writer.filesystem;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
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
	public static final String CONFIG_PATH = PREFIX + "customStoragePath"; // NOCS (afterPREFIX)
	public static final String CONFIG_MAXENTRIESINFILE = PREFIX + "maxEntriesInFile"; // NOCS (afterPREFIX)
	public static final String CONFIG_MAXLOGSIZE = PREFIX + "maxLogSize"; // NOCS (afterPREFIX)
	public static final String CONFIG_MAXLOGFILES = PREFIX + "maxLogFiles"; // NOCS (afterPREFIX)
	public static final String CONFIG_FLUSH = PREFIX + "flush"; // NOCS (afterPREFIX)
	public static final String CONFIG_BUFFER = PREFIX + "bufferSize"; // NOCS (afterPREFIX)

	private static final Map<IMonitoringController, ChangeableFolderSyncFsWriter> instanceMapping = new HashMap<>();

	public static synchronized ChangeableFolderSyncFsWriter getInstance(final IMonitoringController controler) {
		return instanceMapping.get(controler);
	}

	private final static List<RegistryRecord> MAPPING_RECORDS = new LinkedList<>();
	private SyncFsWriter currentWriter;
	private final Configuration configuration;

	public ChangeableFolderSyncFsWriter(final Configuration configuration) {
		super(configuration);
		this.configuration = configuration;
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
			MAPPING_RECORDS.add((RegistryRecord) record);
		}
		if (currentWriter == null) {
			return true;
		} else {
			return currentWriter.newMonitoringRecord(record);
		}
	}

	@Override
	public synchronized void terminate() {
		// defaultWriter.terminate();
		if (currentWriter != null) {
			currentWriter.terminate();
		}
	}

	@Override
	protected void init() throws Exception {
		instanceMapping.put(monitoringController, this);
	}

	public synchronized void setFolder(final File writingFolder) throws Exception {
		if (currentWriter != null) {
			currentWriter.terminate();
		}
		writingFolder.mkdirs();
		String absolutePath = writingFolder.getAbsolutePath();
		Configuration resultingConfig = createTempConfigWithNewFolder(absolutePath);
		synchronized (this) {
			currentWriter = new SyncFsWriter(resultingConfig);
			currentWriter.setController(monitoringController);
			for (RegistryRecord record : MAPPING_RECORDS) {
				System.out.println("Füge hinzu: " + record);
				currentWriter.newMonitoringRecord(record);
			}
		}
	}

	private Configuration createTempConfigWithNewFolder(final String absolutePath) {
		Configuration tempConfig = toSyncFsWriterConfiguration(configuration);
		tempConfig.setProperty(SyncFsWriter.CONFIG_PATH, absolutePath);
		return tempConfig;
	}

	public synchronized void reset() {
		if (currentWriter != null) {
			currentWriter.terminate();
		}
		currentWriter = null;
	}

	public SyncFsWriter getWriter() {
		return currentWriter;
	}

	public IMonitoringController getController() {
		return monitoringController;
	}
}
