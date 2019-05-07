package de.dagere.kopeme;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import de.dagere.kopeme.parsing.BuildtoolProjectNameReader;

public class TestPomProjectNameReader {

   public static final String PATH_TO_TESTPOM_SUBFOLDER = "src/test/resources/pomreader/test";
   public static final String PATH_TO_GRADLE_SUBFOLDER = "src/test/resources/gradlereader/test";
   public static final String TESTPOM_EXPECTED_PROJECT_NAME = "de.kopeme/testpom";
   public static final String GRADLE_EXPECTED_PROJECT_NAME = "de.kopeme/testpom";
   
	@Test
	public void testFoundPom() throws Exception {
		BuildtoolProjectNameReader testable = new BuildtoolProjectNameReader();
		assertTrue("pom should be found", testable.foundPomXml(new File(PATH_TO_TESTPOM_SUBFOLDER), 1));
		assertEquals(TESTPOM_EXPECTED_PROJECT_NAME, testable.getProjectName());
	}
	
	@Test
   public void testFoundGradle() throws Exception {
      BuildtoolProjectNameReader testable = new BuildtoolProjectNameReader();
      assertTrue("pom should be found", testable.foundPomXml(new File(PATH_TO_GRADLE_SUBFOLDER), 1));
      assertEquals(GRADLE_EXPECTED_PROJECT_NAME, testable.getProjectName());
   }
	
	@Test
	public void testDepth() throws Exception {
		BuildtoolProjectNameReader testable = new BuildtoolProjectNameReader();
		assertFalse("pom should not be found", testable.foundPomXml(new File(PATH_TO_TESTPOM_SUBFOLDER), 0));
	}
	
	@Test
	public void testPomNotParsable() throws Exception {
		BuildtoolProjectNameReader testable = new BuildtoolProjectNameReader();
		assertTrue("pom should be found", testable.foundPomXml(new File(PATH_TO_TESTPOM_SUBFOLDER + File.separator + "test_bad_pom"), 0));
		assertEquals(KoPeMeConfiguration.DEFAULT_PROJECTNAME, testable.getProjectName());
	}
	
}
