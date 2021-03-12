package de.dagere.kopeme;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.dagere.kopeme.parsing.BuildtoolProjectNameReader;

public class TestBuildtoolProjectNameReader {

   public static final String PATH_TO_TESTPOM_SUBFOLDER = "src/test/resources/pomreader/test";
   public static final String PATH_TO_GRADLE_SUBFOLDER = "src/test/resources/gradlereader/test";
   public static final String PATH_TO_GRADLE_PROJECTGROUP = "src/test/resources/gradlereader_projectgroup";
   public static final String PATH_TO_ANT_SUBFOLDER = "src/test/resources/antreader/test";
   public static final String PATH_TO_ANT_SUBFOLDER_NONAME = "src/test/resources/antreader_noname/test";
   
   public static final String TESTPOM_EXPECTED_PROJECT_NAME = "de.kopeme/testpom";
   public static final String GRADLE_EXPECTED_PROJECT_NAME = "de.kopeme/testpom";
   public static final String ANT_EXPECTED_PROJECT_NAME = "Testproject";
   
	@Test
	public void testFoundPom() throws Exception {
		final BuildtoolProjectNameReader testable = new BuildtoolProjectNameReader();
		assertTrue("pom.xml should be found", testable.searchBuildfile(new File(PATH_TO_TESTPOM_SUBFOLDER), 1));
		assertEquals(TESTPOM_EXPECTED_PROJECT_NAME, testable.getProjectName());
	}
	
	@Test
   public void testFoundGradle() throws Exception {
      final BuildtoolProjectNameReader testable = new BuildtoolProjectNameReader();
      assertTrue("build.gradle should be found", testable.searchBuildfile(new File(PATH_TO_GRADLE_SUBFOLDER), 1));
      assertEquals(GRADLE_EXPECTED_PROJECT_NAME, testable.getProjectName());
   }
	
	@Test
   public void testGradleProjectgroup() throws Exception {
      final BuildtoolProjectNameReader testable = new BuildtoolProjectNameReader();
      assertTrue("build.gradle should be found", testable.searchBuildfile(new File(PATH_TO_GRADLE_PROJECTGROUP), 1));
      assertEquals("projectgroup/gradlereader_projectgroup", testable.getProjectName());
   }
	
	@Test
   public void testFoundAnt() throws Exception {
      final BuildtoolProjectNameReader testable = new BuildtoolProjectNameReader();
      assertTrue("build.xml should be found", testable.searchBuildfile(new File(PATH_TO_ANT_SUBFOLDER), 1));
      assertEquals(ANT_EXPECTED_PROJECT_NAME, testable.getProjectName());
   }
	
	@Test
   public void testAntNoName() throws Exception {
      final BuildtoolProjectNameReader testable = new BuildtoolProjectNameReader();
      assertTrue("build.xml should be found", testable.searchBuildfile(new File(PATH_TO_ANT_SUBFOLDER_NONAME), 1));
      assertEquals("default", testable.getProjectName());
   }
	
	@Test
	public void testDepth() throws Exception {
		final BuildtoolProjectNameReader testable = new BuildtoolProjectNameReader();
		assertFalse("pom.xml should not be found", testable.searchBuildfile(new File(PATH_TO_TESTPOM_SUBFOLDER), 0));
	}
	
	@Test
	public void testPomNotParsable() throws Exception {
		final BuildtoolProjectNameReader testable = new BuildtoolProjectNameReader();
		assertTrue("pom.xml should be found", testable.searchBuildfile(new File(PATH_TO_TESTPOM_SUBFOLDER + File.separator + "test_bad_pom"), 0));
		assertEquals(KoPeMeConfiguration.DEFAULT_PROJECTNAME, testable.getProjectName());
	}
	
}
