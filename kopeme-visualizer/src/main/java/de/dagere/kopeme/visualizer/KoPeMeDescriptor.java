package de.dagere.kopeme.visualizer;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Describes the plugin
 * 
 * @author reichelt
 *
 */
@Extension
public final class KoPeMeDescriptor extends
		BuildStepDescriptor<Publisher> {

	static final Logger log = Logger.getLogger(KoPeMePublisher.class.getName());

	private KoPeMePublisher publisher;

	public KoPeMeDescriptor() {
		super(KoPeMePublisher.class);
		log.info("Initialize KoPeMeDescriptor");
		load();
	}

	public boolean isApplicable(Class<? extends AbstractProject> jobType) {
		return true;
	}

	@Override
	public String getDisplayName() {
		// LOGGER.log(Level.INFO,"getDisplayName");
		return "Performanzmaße visualisieren";
	}

	@Override
	public String getHelpFile() {
		return "/plugin/postbuild-task/help/main.html";
	}

	public static JSONArray getArray(Object data) {
		JSONArray result;
		if (data instanceof JSONArray)
			result = (JSONArray) data;
		else {
			result = new JSONArray();
			if (data != null)
				result.add(data);
		}
		return result;
	}

	public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
		log.info("Checking Name: " + value);
		if (publisher == null)
			return FormValidation.ok();
		if (publisher.fileExists(value)) {
			return FormValidation.ok();
		} else {
			return FormValidation.error("Das ist keine Datei");
		}
	}

	@Override
	public KoPeMePublisher newInstance(StaplerRequest req, JSONObject formData)
			throws FormException {
		log.info("Creating new Instance");
		publisher = new KoPeMePublisher();
		JSONArray dataArray = getArray(formData.get("testcases"));
		log.info("Erzeuge neue Publisher-Instanz, Daten: " + dataArray);
		for (Object data : dataArray) {
			log.info("Füge alte Daten hinzu für " + data);
			// if (data instanceof JSONObject)
			// {
			// publisher.addTestcase(new GraphVisualizer(((JSONObject) data).getString("name")));
			// }
			// else
			// {
			// publisher.addTestcase(new GraphVisualizer());
			// }
		}
		// if (publisher.getTestcases().isEmpty()) {
		// publisher.addTestcase(new GraphVisualizer());
		// }
		return publisher;
	}
}