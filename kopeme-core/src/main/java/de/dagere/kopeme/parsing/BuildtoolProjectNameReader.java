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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import de.dagere.kopeme.KoPeMeConfiguration;

/**
 * This class is used to extract a project name string from the pom xml. First you need to class foundPomXml to locate the pom.xml. Second, you can get the project name by calling
 * getProjectName
 * 
 * 
 * @author dhaeb
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
   public boolean foundPomXml(final File directory, final int depth) {
      LOG.debug("Directory: {}", directory);
      if (depth == -1 || directory == null || !directory.isDirectory()) {
         return false;
      } else {
         final File[] pomFiles = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(final File pathname) {
               return "pom.xml".equals(pathname.getName()) || "build.gradle".equals(pathname.getName());
            }
         });
         if (pomFiles.length == 1) {
            try {
               pathToConfigFile = pomFiles[0].getCanonicalFile();
               projectInfo = getProjectInfo(pathToConfigFile);
               return true;
            } catch (final IOException e) {
               e.printStackTrace();
            }
            return false;
         } else {
            return foundPomXml(directory.getParentFile(), depth - 1);
         }
      }
   }

   /**
    * @return the projectname extract by the pom.xml as groupid/artifactid
    */
   public String getProjectName() {
      return !projectInfo.getGroupId().equals("") ? projectInfo.getGroupId() + "/" + projectInfo.getArtifactId() : projectInfo.getArtifactId();
   }
   
   public String getArtifactId() {
      return projectInfo.getArtifactId();
   }

   public String getGroupId() {
      return projectInfo.getGroupId();
   }

   private String readGradleProperty(final String line) {
      final String shortString = line.substring(line.indexOf("'") + 1, line.lastIndexOf('\''));
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

   public ProjectInfo getProjectInfo(final File pomXmlFile) {
      ProjectInfo result = new ProjectInfo(KoPeMeConfiguration.DEFAULT_PROJECTNAME, "");
      if (pomXmlFile.getName().equals("pom.xml")) {
         final MavenXpp3Reader reader = new MavenXpp3Reader();
         try {
            final Model model = reader.read(new InputStreamReader(new FileInputStream(pomXmlFile), Charset.defaultCharset()));
            final String groupId = getGroupid(model);
            result = new ProjectInfo(model.getArtifactId(), groupId);
            // return groupId + File.separator + model.getArtifactId();
         } catch (IOException | XmlPullParserException e) {
            System.err.println("There was a problem while reading the pom.xml file!");
            e.printStackTrace();
         }
      } else if (pomXmlFile.getName().equals("build.gradle")) {
         try {
            String groupId = null;
            String name = null;
            final List<String> lines = Files.readAllLines(Paths.get(pomXmlFile.toURI()));
            for (final String line : lines) {
               if (line.contains("group") && line.contains("=")) {
                  groupId = readGradleProperty(line);
               }
            }
            final File settingsFile = new File(pomXmlFile.getParentFile(), "settings.gradle");
            if (settingsFile.exists()) {
               final List<String> linesSettings = Files.readAllLines(Paths.get(settingsFile.toURI()));
               for (final String line : linesSettings) {
                  if (line.contains("rootProject.name")) {
                     name = readGradleProperty(line);
                  }
               }
            }
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

      }
      return result;
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