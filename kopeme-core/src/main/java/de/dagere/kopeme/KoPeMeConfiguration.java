package de.dagere.kopeme;

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
	
	private String projectName;
	
	/**
	 * package scope for testinig purposes
	 */
	KoPeMeConfiguration() {
		projectName = System.getProperty(KOPEME_PROJECTNAME_PROPNAME, DEFAULT_PROJECTNAME) ;
	}
	
	public String getProjectName() {
		return projectName;
	}
}
