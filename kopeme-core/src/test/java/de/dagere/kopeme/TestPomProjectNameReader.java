package de.dagere.kopeme;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import static de.dagere.kopeme.TestUtils.PATH_TO_TESTPOM_SUBFOLDER;

public class TestPomProjectNameReader {

	@Test
	public void testFoundPom() throws Exception {
		PomProjectNameReader testable = new PomProjectNameReader();
		assertTrue("pom should be found", testable.foundPomXml(new File(PATH_TO_TESTPOM_SUBFOLDER), 1));
		assertEquals(TestUtils.TESTPOM_EXPECTED_PROJECT_NAME, testable.getProjectName());
	}
	
	@Test
	public void testDepth() throws Exception {
		PomProjectNameReader testable = new PomProjectNameReader();
		assertFalse("pom should not be found", testable.foundPomXml(new File(PATH_TO_TESTPOM_SUBFOLDER), 0));
	}
	
	@Test
	public void testPomNotParsable() throws Exception {
		PomProjectNameReader testable = new PomProjectNameReader();
		assertTrue("pom should be found", testable.foundPomXml(new File(PATH_TO_TESTPOM_SUBFOLDER + File.separator + "test_bad_pom"), 0));
		assertEquals(KoPeMeConfiguration.DEFAULT_PROJECTNAME, testable.getProjectName());
	}
	
}
