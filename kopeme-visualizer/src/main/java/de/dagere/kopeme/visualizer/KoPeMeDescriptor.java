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

import de.dagere.kopeme.visualizer.data.Testcase;

@Extension
public final class KoPeMeDescriptor extends
		BuildStepDescriptor<Publisher> {

	static final Logger log = Logger.getLogger(KoPeMePublisher.class.getName());

	public KoPeMeDescriptor() {
		super(KoPeMePublisher.class);
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

	private KoPeMePublisher publisher;

	public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
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
		publisher = new KoPeMePublisher();
		JSONArray dataArray = getArray(formData.get("testcases"));
		log.info("Erzeuge neue Publisher-Instanz, Daten: " + dataArray);
		for (Object data : dataArray) {
			log.info("Füge hinzu für " + data);
			if (data instanceof JSONObject)
			{
				publisher.addTestcase(new Testcase(((JSONObject) data).getString("name")));
			}
			else
			{
				publisher.addTestcase(new Testcase());
			}
		}
		if (publisher.getTestcases().isEmpty()) {
			publisher.addTestcase(new Testcase());
		}
		return publisher;
	}
}