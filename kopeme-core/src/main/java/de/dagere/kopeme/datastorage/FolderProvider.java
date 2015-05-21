package de.dagere.kopeme.datastorage;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class FolderProvider {
	
	private static final String USER_HOME = System.getenv("HOME");
	private static final String KO_PE_ME = ".KoPeMe";
	
	static final String KOPEME_DEFAULT_FOLDER = USER_HOME + File.separator + KO_PE_ME + File.separator;
	
	private static FolderProvider INSTANCE; 
	
	static FolderProvider getInstance(String defaultFolder){ if(INSTANCE == null){
			INSTANCE = new FolderProvider(defaultFolder);
		} 
		return INSTANCE;
	}
	
	public static FolderProvider getInstance(){
		return getInstance(KOPEME_DEFAULT_FOLDER);
	}
	
	private String kopemeDefaultFolder;
	
	private FolderProvider(String kopemeDefaultFolder){
		setKopemeDefaultFolder(kopemeDefaultFolder);
	}
	
	public File getFolderForNewPerformanceresult(String filename) {
		String nowAsString = Long.valueOf(System.currentTimeMillis()).toString();
		File returnable = new File(getFolderFor(filename) + nowAsString);
		return returnable;
	}
	
	public String getFolderFor(String filename){
		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder.append(kopemeDefaultFolder);
		pathBuilder.append(File.separator);
		pathBuilder.append(filename);
		pathBuilder.append(File.separator);
		return pathBuilder.toString();
	}

	public Collection<File> getPerformanceResultFolders(String filename){
		File perfromanceResultsContainingFolder = new File(getFolderFor(filename));
		if(!perfromanceResultsContainingFolder.isDirectory()){
			throw new RuntimeException();
		}
		Map<Long, File> timeFileMapping = new TreeMap<Long,File>();
		for(File f : perfromanceResultsContainingFolder.listFiles()){
			try {
				if(f.isDirectory()){
					long time = Long.parseLong(f.getName());
					timeFileMapping.put(time, f);
				}
			} catch (NumberFormatException e) {
				System.err.println("The folder " + f.getAbsolutePath() + " may not be placed there, need timestamp folder name!");
			}
		}
		return timeFileMapping.values();
	}
	
	public File getLastPerformanceResultFolder(String filename){
		Collection<File> performanceResults = getPerformanceResultFolders(filename);
		File[] result = performanceResults.toArray(new File[performanceResults.size()]);
		return result[performanceResults.size() - 1];
	}

	public String getKopemeDefaultFolder() {
		return kopemeDefaultFolder;
	}

	public void setKopemeDefaultFolder(String kopemeDefaultFolder) {
		this.kopemeDefaultFolder = kopemeDefaultFolder;
	}
	
}