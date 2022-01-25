package de.dagere.kopeme.kieker.probe;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.aspectj.util.FileUtil;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * This test checks the correctness of the DurationRecordAspectFull. Since this requires the aspect to be built, it is in kopeme-junit instead directly in kopeme-core.
 * 
 * @author DaGeRe
 *
 */
public class DurationRecordAspectIT {

   public static final String ASPECTJ_VERSION = "1.9.7";
   public static final String KIEKER_VERSION = "1.15";

   @Test
   public void testAspect() throws IOException {
      File kopemeFile = findKopemeFile();

      String mavenRepo = getRepository().getAbsolutePath() + "/";

      String aspectJRT = mavenRepo + "org/aspectj/aspectjrt/" + ASPECTJ_VERSION + "/aspectjrt-" + ASPECTJ_VERSION + ".jar";
      String aspectJWeaver = mavenRepo + "org/aspectj/aspectjweaver/" + ASPECTJ_VERSION + "/aspectjweaver-" + ASPECTJ_VERSION + ".jar";
      String kieker = mavenRepo + "net/kieker-monitoring/kieker/" + KIEKER_VERSION + "/kieker-" + KIEKER_VERSION + "-aspectj.jar";
      
      String classpath = aspectJRT + ":" + aspectJWeaver + ":" + kieker;
      String javaagent = "-javaagent:" + kopemeFile.getAbsolutePath();

      File tempDir = getTempDir();

      ProcessBuilder processBuilder = new ProcessBuilder("java",
            "-Djava.io.tmpdir=" + tempDir.getAbsolutePath(),
            "-cp", "target/test-0.1-SNAPSHOT.jar:" + classpath,
            javaagent,
            "de.test.MainWithError");
      processBuilder.directory(new File("src/test/resources/woven-project"));

      Process process = processBuilder.start();

      waitForProcessEnd(process);

      Assert.assertEquals(0, process.exitValue());
      
      checkResultFolder(tempDir);
   }

   private void checkResultFolder(File tempDir) throws IOException {
      File resultFolder = tempDir.listFiles()[0];
      File resultFile = resultFolder.listFiles((FileFilter) new WildcardFileFilter("*.dat"))[0];
      String content = FileUtils.readFileToString(resultFile, StandardCharsets.UTF_8);
      
      MatcherAssert.assertThat(content, Matchers.containsString("de.test.FinalFieldConstructorExample.getParameters"));
      MatcherAssert.assertThat(content, Matchers.containsString("de.test.MainWithError.main"));
      MatcherAssert.assertThat(content, Matchers.containsString("de.test.FinalFieldConstructorExample.<init>"));
   }

   private File getTempDir() {
      File tempDir = new File("target/temp");
      if (tempDir.exists()) {
         FileUtil.deleteContents(tempDir);
      }
      tempDir.mkdirs();
      return tempDir;
   }
   
   private static File getRepository() {
      String maven_home = System.getenv("HOME");
      final String mavenRepo;
      if (maven_home != null) {
         mavenRepo = maven_home + File.separator + ".m2" + File.separator + "repository" + File.separator;
      } else {
         final String home = System.getProperty("user.home");
         mavenRepo = home + File.separator + ".m2" + File.separator + "repository" + File.separator;
      }
      return new File(mavenRepo);
   }

   private void waitForProcessEnd(final Process process) {
      StreamGobbler.getFullProcess(process, true);

      int i = 0;
      while (process.isAlive() && i < 10) {
         try {
            i++;
            Thread.sleep(10);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   private File findKopemeFile() {
      File kopemeFile = null;
      for (File candidate : FileUtil.listFiles(new File("../kopeme-core/target/"), new WildcardFileFilter("kopeme-core-*.jar"))) {
         if (!candidate.getName().contains("-javadoc") &&
               !candidate.getName().contains("-sources") &&
               !candidate.getName().contains("-tests")) {
            kopemeFile = candidate;
         }
      }
      Assert.assertNotNull("KoPeMe needs to be built", kopemeFile);
      return kopemeFile;
   }
}
