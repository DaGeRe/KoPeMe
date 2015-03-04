package de.dagere.kopeme.visualizer;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Project;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dagere.kopeme.visualizer.data.Testcase;

/**
 * The publisher for KoPeMe, which takes the input from the user
 * 
 * @author dagere
 *
 */
public class KoPeMePublisher extends Recorder {

	private static final Logger log = Logger.getLogger(KoPeMePublisher.class.getName());
	private List<Testcase> testcases = new LinkedList<Testcase>();
	private AbstractProject<?, ?> lastProject = null;

	public KoPeMePublisher() {
		log.log(Level.INFO,
				"Konstruktor KoPeMePublisher");
	}

	private Object readResolve() {
		log.info("Lade alte Testfälle: " + testcases.size());
		setTestcases(testcases);
		return this;
	}

	public void addTestcase(Testcase t)
	{
		testcases.add(t);
	}

	public List<Testcase> getTestcases() {
		log.info("Getting Testcases, Anzahl: " + testcases.size());
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
		// String buildLog = build.getLog();
		listener.getLogger().println("Performing Post build task...");
		Result pr = build.getResult();
		return true;
	}

	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) {
		log.info("Gebe Projektaktion aus, Klasse: " + project.getClass() + " Instanz von Projekt: " + (project instanceof Project));
		if (project instanceof AbstractProject)
		{
			lastProject = project;
			VisualizeAction va = new VisualizeAction((AbstractProject) project, this);
			log.info("Aktion: " + va);
			return va;
		}
		// if (project instanceof MavenModuleSet){
		// MavenModuleSet mms = (MavenModuleSet) project;
		// // mms.getWorkspace()
		// // mms.getPro
		// }
		return null;
	}

	public boolean fileExists(String value) {
		if (lastProject != null) {
			FilePath workspace = lastProject.getSomeWorkspace();
			log.info("Suche in: " + workspace.toString());
			try {
				if (workspace.list(value).length > 0) {
					return true;
				} else {
					return false;
				}
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		} else {
			return true;
		}
	}
}
