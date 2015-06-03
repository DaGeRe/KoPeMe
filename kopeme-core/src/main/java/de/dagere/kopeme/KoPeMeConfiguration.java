package de.dagere.kopeme;

import java.io.File;

/**
 * Configuration Singleton class which can return the projectname of the current project, 
 * set by VM parameter or project model files (e.g. pom.xml) 
 * 
 * @author dhaeb
 *
 */
public class KoPeMeConfiguration {



	private static KoPeMeConfiguration INSTANCE = null;
	
	/**
	 * The public interface to retrieve the configuration instance. 
	 * 
	 * @return
	 */
	public static synchronized KoPeMeConfiguration getInstance(){
		if(INSTANCE == null){
			INSTANCE = new KoPeMeConfiguration();
		}
		return INSTANCE;
	}
	
	/**
	 * Default projectname if no VM parameter is given or no pom.xml can be extracted.
	 */
	public final static String DEFAULT_PROJECTNAME = "default";
	
	/**
	 * VM property name for the projectname.
	 * If given, this property will be used, regardless if there exists a pom.xml
	 */
	static final String KOPEME_PROJECTNAME_PROPNAME = "kopeme.projectname";
	
	/**
	 * VM property name for the depth, who many filde kopeme should maximal go up to find the project config file (e.g. pom.xml)
	 */
	static final String KOPEME_SEARCHDEPTH_PROPNAME = "kopeme.searchdepth";


	/**
	 * VM proeprty name for specify the working dir of the current project, the default is .
	 */
	static final String KOPEME_WORKINGDIR_PROPNAME = "kopeme.workingdir";
	
	private String projectName;
	
	/**
	 * package scope for testing purposes
	 */
	KoPeMeConfiguration() {
		projectName = System.getProperty(KOPEME_PROJECTNAME_PROPNAME);
		File workingDir = new File(getWorkingDirAsString());
		int searchDepth = getIntSystemProperty(KOPEME_SEARCHDEPTH_PROPNAME, 10);
		if(projectName == null){
			PomProjectNameReader reader = new PomProjectNameReader();
			if(reader.foundPomXml(workingDir, searchDepth)){
				projectName = reader.getProjectName();
			} else {
				projectName = DEFAULT_PROJECTNAME;
			}
		} 
	}

	private String getWorkingDirAsString() {
		return System.getProperty(KOPEME_WORKINGDIR_PROPNAME, new File(".").getAbsolutePath());
	}

	private int getIntSystemProperty(String propName, int defaultValue) {
		 String propertyValue = System.getProperty(propName);
		 try {
			return Integer.parseInt(propertyValue);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public String getProjectName() {
		return projectName;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
