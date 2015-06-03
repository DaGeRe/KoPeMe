package de.dagere.kopeme;

public class KoPeMeConfiguration {

	private static KoPeMeConfiguration INSTANCE = null;
	
	public static synchronized KoPeMeConfiguration getInstance(){
		if(INSTANCE == null){
			INSTANCE = new KoPeMeConfiguration();
		}
		return INSTANCE;
	}
	
	public final static String DEFAULT_PROJECTNAME = "default";
	static final String KOPEME_PROJECTNAME_PROPNAME = "kopeme.projectname";
	
	private String projectName;
	
	KoPeMeConfiguration() {
		projectName = System.getProperty(KOPEME_PROJECTNAME_PROPNAME, DEFAULT_PROJECTNAME) ;
	}
	
	public String getProjectName() {
		return projectName;
	}
}
