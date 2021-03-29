package de.dagere.kopeme.parsing;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.dagere.kopeme.KoPeMeConfiguration;

/**
 * This class is used to extract a project name string from the pom xml. First you need to class foundPomXml to locate the pom.xml. Second, you can get the project name by calling
 * getProjectName
 * 
 *
 */
public class BuildtoolProjectNameReader {
   private static final Logger LOG = LogManager.getLogger(BuildtoolProjectNameReader.class);

   private File pathToConfigFile;
   ProjectInfo projectInfo;

   public BuildtoolProjectNameReader() {
   }

   /**
    * Tries to find the pom recursively by going up in the directory tree.
    * 
    * @param directory The start folder where to search
    * @param depth how many times should we try to go up to find the pom?
    * @return a boolean denoting if the pom was found / side effect setting the pom file
    */
   public boolean searchBuildfile(final File directory, final int depth) {
      LOG.debug("Directory: {}", directory);
      if (depth == -1 || directory == null || !directory.isDirectory()) {
         return false;
      } else {
         File[] buildFile = findBuildfile(directory, "pom.xml");
         if (buildFile.length != 1) {
            buildFile = GradleParseHelper.searchGradleFiles(directory);
         }
         if (buildFile.length != 1) {
            buildFile = findBuildfile(directory, "build.xml");
         }
         if (buildFile.length == 1) {
            try {
               pathToConfigFile = buildFile[0].getCanonicalFile();
               projectInfo = getProjectInfo(pathToConfigFile);
               return true;
            } catch (final IOException e) {
               e.printStackTrace();
            }
            return false;
         } else {
            return searchBuildfile(directory.getParentFile(), depth - 1);
         }
      }
   }

   private File[] findBuildfile(final File directory, final String filename) {
      final File[] pomFiles = directory.listFiles(new FileFilter() {
         @Override
         public boolean accept(final File pathname) {
            return filename.equals(pathname.getName());
         }
      });
      return pomFiles;
   }
   
   /**
    * @return the projectname extract by the pom.xml as groupid/artifactid
    */
   public String getProjectName() {
      return !projectInfo.getGroupId().equals("") ? projectInfo.getGroupId() + "/" + projectInfo.getArtifactId() : projectInfo.getArtifactId();
   }

   private String readGradleProperty(final String line) {
      final String shortString;
      if (line.contains("'")) {
         shortString = line.substring(line.indexOf("'") + 1, line.lastIndexOf('\''));
      } else if (line.contains("\"")) {
         shortString = line.substring(line.indexOf("\"") + 1, line.lastIndexOf('"'));
      } else {
         shortString = line.substring(line.indexOf("=") + 1);
      }
      final String shortened = shortString.trim();
      return shortened;
   }

   private String getGroupid(final Model model) {
      if (model.getGroupId() != null) {
         return model.getGroupId();
      } else {
         return model.getParent().getGroupId();
      }
   }

   public ProjectInfo getProjectInfo(final File buildFile) {
      ProjectInfo result = new ProjectInfo(KoPeMeConfiguration.DEFAULT_PROJECTNAME, "");
      if (buildFile.getName().equals("pom.xml")) {
         result = readMaven(buildFile, result);
      } else if (buildFile.getName().equals("build.gradle")) {
         result = readGradle(buildFile, result);
      } else if (buildFile.getName().equals("build.xml")) {
         result = readAnt(buildFile, result);
      }
      return result;
   }

   private ProjectInfo readAnt(final File buildFile, ProjectInfo result) {
      try {
         final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         final DocumentBuilder builder = factory.newDocumentBuilder();
         final Document doc = builder.parse(buildFile);
         final NodeList projectNodes = doc.getElementsByTagName("project");
         final Node projectNode = projectNodes.item(0);
         final Node nameAttribute = projectNode.getAttributes().getNamedItem("name");
         if (nameAttribute != null) {
            result = new ProjectInfo(nameAttribute.getNodeValue(), "");
         }
      } catch (SAXException | IOException | ParserConfigurationException e) {
         e.printStackTrace();
      }
      return result;
   }

   private ProjectInfo readGradle(final File pomXmlFile, ProjectInfo result) {
      try {
         String groupId = null;
         String name = null;
         final List<String> lines = Files.readAllLines(Paths.get(pomXmlFile.toURI()));
         for (final String line : lines) {
            if (line.contains("group") && line.contains("=")) {
               groupId = readGradleProperty(line);
            }
         }
         name = readSettingsfile(pomXmlFile, name);
         final File propertyFile = new File(pomXmlFile.getParentFile(), "gradle.properties");
         if (propertyFile.exists()) {
            final List<String> linesProperties = Files.readAllLines(Paths.get(propertyFile.toURI()));
            for (final String line : linesProperties) {
               if (line.contains("theGroup")) {
                  groupId = readGradleProperty(line);
               }
               if (line.contains("theName")) {
                  name = readGradleProperty(line);
               }
            }
         }

         if (name == null) {
            name = pomXmlFile.getParentFile().getName();
         }
         if (groupId != null) {
            result = new ProjectInfo(name, groupId);
         } else {
            result = new ProjectInfo(name, "");
         }
      } catch (final IOException e) {
         e.printStackTrace();
      }
      return result;
   }

   private String readSettingsfile(final File pomXmlFile, String name) throws IOException {
      final File settingsFile = new File(pomXmlFile.getParentFile(), "settings.gradle");
      if (settingsFile.exists()) {
         final List<String> linesSettings = Files.readAllLines(Paths.get(settingsFile.toURI()));
         for (final String line : linesSettings) {
            if (line.contains("rootProject.name")) {
               name = readGradleProperty(line);
            }
         }
      }
      return name;
   }

   private ProjectInfo readMaven(final File pomXmlFile, ProjectInfo result) {
      final MavenXpp3Reader reader = new MavenXpp3Reader();
      try (InputStreamReader inputStream = new InputStreamReader(new FileInputStream(pomXmlFile), Charset.defaultCharset())) {
         final Model model = reader.read(inputStream);
         final String groupId = getGroupid(model);
         result = new ProjectInfo(model.getArtifactId(), groupId);
         // return groupId + File.separator + model.getArtifactId();
      } catch (IOException | XmlPullParserException e) {
         System.err.println("There was a problem while reading the pom.xml file!");
         e.printStackTrace();
      }
      return result;
   }

}