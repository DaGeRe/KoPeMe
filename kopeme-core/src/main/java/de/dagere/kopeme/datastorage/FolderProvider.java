package de.dagere.kopeme.datastorage;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import de.dagere.kopeme.KoPeMeConfiguration;

public class FolderProvider {

	static final Long MEASURE_TIME = Long.valueOf(System.currentTimeMillis());

	static final String KOPEME_DEFAULT_FOLDER = System.getenv("KOPEME_HOME") != null ? System.getenv("KOPEME_HOME")
			: System.getenv("HOME") + File.separator + ".KoPeMe" + File.separator;

	private static FolderProvider INSTANCE;

	private final KoPeMeConfiguration config = KoPeMeConfiguration.getInstance();

	public static FolderProvider getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FolderProvider(KOPEME_DEFAULT_FOLDER);
		}
		return INSTANCE;
	}

	private String kopemeDefaultFolder;

	private FolderProvider(final String kopemeDefaultFolder) {
		setKopemeDefaultFolder(kopemeDefaultFolder);
	}

	public File getFolderForCurrentPerformanceresults(final String filename) {
		String nowAsString = MEASURE_TIME.toString();
		File returnable = new File(getFolderFor(filename) + nowAsString);
		return returnable;
	}

	public File getFolderForCurrentPerformanceresults(final String testFileName, final String testCaseName) {
		return new File(getFolderForCurrentPerformanceresults(testFileName).getPath(), testCaseName);
	}

	public String getFolderFor(final String filename) {
		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder.append(kopemeDefaultFolder);
		pathBuilder.append(File.separator);
		pathBuilder.append(config.getProjectName());
		pathBuilder.append(File.separator);
		pathBuilder.append(filename);
		pathBuilder.append(File.separator);
		return pathBuilder.toString();
	}

	public Collection<File> getPerformanceResultFolders(final String filename) {
		File perfromanceResultsContainingFolder = new File(getFolderFor(filename));
		if (!perfromanceResultsContainingFolder.isDirectory()) {
			throw new RuntimeException();
		}
		Map<Long, File> timeFileMapping = new TreeMap<Long, File>();
		for (File file : perfromanceResultsContainingFolder.listFiles()) {
			try {
				if (file.isDirectory()) {
					long time = Long.parseLong(file.getName());
					timeFileMapping.put(time, file);
				}
			} catch (NumberFormatException e) {
				System.err.println("The folder " + file.getAbsolutePath() + " may not be placed there, need timestamp folder name!");
			}
		}
		return timeFileMapping.values();
	}

	public File getLastPerformanceResultFolder(final String filename) {
		Collection<File> performanceResults = getPerformanceResultFolders(filename);
		File[] result = performanceResults.toArray(new File[performanceResults.size()]);
		return result[performanceResults.size() - 1];
	}

	public String getKopemeDefaultFolder() {
		return kopemeDefaultFolder;
	}

	public void setKopemeDefaultFolder(final String kopemeDefaultFolder) {
		this.kopemeDefaultFolder = kopemeDefaultFolder;
	}

}
