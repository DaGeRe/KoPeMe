package de.dagere.kopeme;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

/**
 * This class is used to extract a project name string from the pom xml. First you need to class foundPomXml to locate the pom.xml. Second, you can get the
 * project name by calling getProjectName
 * 
 * 
 * @author dhaeb
 *
 */
public class PomProjectNameReader {
	private static final Logger LOG = LogManager.getLogger(PomProjectNameReader.class);

	private File pathToPomXml;

	public PomProjectNameReader() {
	}

	/**
	 * Tries to find the pom recursively by going up in the directory tree.
	 * 
	 * @param directory The start folder where to search
	 * @param depth how many times should we try to go up to find the pom?
	 * @return a boolean denoting if the pom was found / side effect setting the pom file
	 */
	public boolean foundPomXml(final File directory, final int depth) {
		LOG.debug("Directory: {}", directory);
		if (depth == -1 || directory == null || !directory.isDirectory()) {
			return false;
		} else {
			File[] pomFiles = directory.listFiles(new FileFilter() {
				@Override
				public boolean accept(final File pathname) {
					return "pom.xml".equals(pathname.getName());
				}
			});
			if (pomFiles.length == 1) {
				pathToPomXml = pomFiles[0];
				return true;
			} else {
				return foundPomXml(directory.getParentFile(), depth - 1);
			}
		}
	}

	/**
	 * @return the projectname extract by the pom.xml as groupid/artifactid
	 */
	public String getProjectName() {
		return getProjectName(pathToPomXml);
	}

	public String getProjectName(final File pomXmlFile) {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		try {
			Model model = reader.read(new InputStreamReader(new FileInputStream(pomXmlFile), Charset.defaultCharset()));
			final String groupId = getGroupid(model);
			return groupId + File.separator + model.getArtifactId();
		} catch (IOException | XmlPullParserException e) {
			System.err.println("There was a problem while reading the pom.xml file!");
			e.printStackTrace();
			return KoPeMeConfiguration.DEFAULT_PROJECTNAME;
		}
	}

	public String getGroupid(final Model model) {
		if (model.getGroupId() != null) {
			return model.getGroupId();
		} else {
			return model.getParent().getGroupId();
		}
	}

	public ProjectInfo getProjectInfo(final File pomXmlFile) {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		try {
			Model model = reader.read(new InputStreamReader(new FileInputStream(pomXmlFile), Charset.defaultCharset()));
			final String groupId = getGroupid(model);
			return new ProjectInfo(model.getArtifactId(), groupId);
		} catch (IOException | XmlPullParserException e) {
			System.err.println("There was a problem while reading the pom.xml file!");
			e.printStackTrace();
			throw new RuntimeException("No or unreadable pom.xml found");
		}
	}

	public static class ProjectInfo {
		final String artifactId, groupId;

		public ProjectInfo(final String artifactId, final String groupId) {
			super();
			this.artifactId = artifactId;
			this.groupId = groupId;
		}

		/**
		 * @return the artifactId
		 */
		public String getArtifactId() {
			return artifactId;
		}

		/**
		 * @return the groupId
		 */
		public String getGroupId() {
			return groupId;
		}
	}

}