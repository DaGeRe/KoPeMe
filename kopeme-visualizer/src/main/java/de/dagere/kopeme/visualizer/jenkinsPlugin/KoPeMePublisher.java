
package de.dagere.kopeme.visualizer.jenkinsPlugin;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Project;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import de.dagere.kopeme.Testcase;

/**
 * The publisher for KoPeMe, which takes the input from the user
 * @author dagere
 *
 */
public class KoPeMePublisher extends Recorder {

	private static final Logger LOGGER = Logger.getLogger(KoPeMePublisher.class.getName());
	private List<Testcase> testcases = new LinkedList<Testcase>();
	
	public KoPeMePublisher() {
		LOGGER.log(Level.INFO,
				"Konstruktor KoPeMePublisher");
	}
	
	private Object readResolve() {
		setTestcases(testcases);
        return this;
    }
	
	public void addTestcase( Testcase t)
	{
		testcases.add(t);
	}
	
	public List<Testcase> getTestcases() {
		LOGGER.info("Getting Testcases" + testcases.size());
		return testcases;
	}

	public void setTestcases(List<Testcase> testcases) {
		this.testcases = testcases;
	}
	
	public String getTest()
	{
		return "test";
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean perform(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
//		String buildLog = build.getLog();
		listener.getLogger().println("Performing Post build task...");
		Result pr = build.getResult();

		return true;
	}
	
	@Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
		LOGGER.info("Gebe Projektaktion aus" + project.getClass() + " " + (project instanceof Project));
		if ( project instanceof AbstractProject )
		{
			VisualizeAction va = new VisualizeAction((AbstractProject)project, this);
			LOGGER.info("Aktion: " +va);
			return va;
		}
//		if (project instanceof MavenModuleSet){
//			MavenModuleSet mms = (MavenModuleSet) project;
////			mms.getWorkspace()
////			mms.getPro
//		}
		return null;
    }
	
	@Extension
	public static final class DescriptorImpl extends
			BuildStepDescriptor<Publisher> {
		

		public DescriptorImpl() {
			super(KoPeMePublisher.class);
			load();
		}

		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}
		
		@Override
		public String getDisplayName() {
//			LOGGER.log(Level.INFO,"getDisplayName");
			return "Performanzmaße visualisieren";
		}

		@Override
		public String getHelpFile() {
			return "/plugin/postbuild-task/help/main.html";
		}
		
		public static JSONArray getArray(Object data) {
	        JSONArray result;
	        if (data instanceof JSONArray) result = (JSONArray)data;
	        else {
	            result = new JSONArray();
	            if (data != null) result.add(data);
	        }
	        return result;
	    }
		
		@Override
		public KoPeMePublisher newInstance(StaplerRequest req, JSONObject formData)
				throws FormException {
			
			KoPeMePublisher publisher = new KoPeMePublisher();
            JSONArray dataArray = getArray(formData.get("testcases"));
            LOGGER.info("Erzeuge neue Publisher-Instanz, Daten: " + dataArray);
			for (Object data : dataArray) {
            	LOGGER.info("Füge hinzu für " + data);
            	if ( data instanceof JSONObject )
            	{
            		publisher.addTestcase(new Testcase(((JSONObject)data).getString("name")));
            	}
            	else
            	{
            		publisher.addTestcase(new Testcase());
            	}
            }
			return publisher;
		}
	}
}
