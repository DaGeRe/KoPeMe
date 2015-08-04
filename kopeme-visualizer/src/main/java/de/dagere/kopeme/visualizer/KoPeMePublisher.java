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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dagere.kopeme.visualizer.data.GraphVisualizer;

/**
 * The publisher for KoPeMe, which takes the input from the user
 * 
 * @author dagere
 *
 */
public class KoPeMePublisher extends Recorder {

	private enum TestcasesSortOrder {
		TESTCLASSNAME, COLLECTORNAME, NONE
	}

	private static final Logger log = Logger.getLogger(KoPeMePublisher.class.getName());
	private List<GraphVisualizer> testcases = new LinkedList<GraphVisualizer>();
	private AbstractProject<?, ?> lastProject = null;
	private VisualizeAction lastVisulizeAction = null;
	private TestcasesSortOrder lastTestcasesSortOrder = TestcasesSortOrder.TESTCLASSNAME;
	private List<String> collectorNames = new ArrayList<String>();
	private List<String> testNames = new ArrayList<String>();

	public KoPeMePublisher() {
		log.log(Level.INFO, "Constructor KoPeMePublisher");
	}

	private Object readResolve() {
		log.info("Lade alte Testfälle: " + testcases.size());
		setTestcases(testcases);
		return this;
	}

	public List<GraphVisualizer> getTestcases() {
		log.info("Getting Testcases, Anzahl: " + testcases.size());
		return testcases;
	}

	public void setTestcases(final List<GraphVisualizer> testcases) {
		this.testcases = testcases;
	}

	public List<String> getCollectorNames() {
		return collectorNames;
	}

	public List<String> getTestNames() {
		return testNames;
	}

	public String getLastTestcasesSortOrder() {
		return lastTestcasesSortOrder.name();
	}

	public void setLastTestcasesSortOrder(final String testcasesSortOrderValue) {
		this.lastTestcasesSortOrder = TestcasesSortOrder.valueOf(testcasesSortOrderValue);
	}

	@Override
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean perform(final AbstractBuild build, final Launcher launcher,
			final BuildListener listener) throws InterruptedException, IOException {
		// String buildLog = build.getLog();
		listener.getLogger().println("Performing Post build task...");
		Result pr = build.getResult();
		return true;
	}

	public boolean isInitialized() {
		return lastVisulizeAction != null;
	}

	@Override
	public Action getProjectAction(final AbstractProject<?, ?> project) {
		log.info("Gebe Projektaktion aus, Klasse: " + project.getClass() + " Instanz von Projekt: " + (project instanceof Project));
		if (project instanceof AbstractProject)
		{
			lastProject = project;
			VisualizeAction va = new VisualizeAction(project, this);
			lastVisulizeAction = va;
			log.info("Visualizer: " + va.getVisualizer().size());

			collectorNames = va.getCollectorNames();
			testNames = va.getTestNames();

			testcases = new LinkedList<GraphVisualizer>();
			for (GraphVisualizer gv : va.getVisualizer()) {
				log.info("Füge Testcase hinzu: " + gv.getName());
				testcases.add(gv);
			}
			log.info("Aktion: " + va);
			return va;
		}
		else {
			log.log(Level.ALL, "Unexpected Class: " + project.getClass());
		}
		return null;
	}

	public boolean fileExists(final String value) {
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
