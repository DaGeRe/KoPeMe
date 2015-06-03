package de.dagere.kopeme;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestKoPeMeConfiguration {
	private static final String FIXTURE = "myProjectName";

	@Test
	public void testProjectNameProperty() throws Exception {
		try {
			System.setProperty(KoPeMeConfiguration.KOPEME_WORKINGDIR_PROPNAME, "src/test/resources");
			System.setProperty(KoPeMeConfiguration.KOPEME_SEARCHDEPTH_PROPNAME, "0");
			assertEquals(KoPeMeConfiguration.DEFAULT_PROJECTNAME, new KoPeMeConfiguration().getProjectName());
		} finally {
			System.clearProperty(KoPeMeConfiguration.KOPEME_WORKINGDIR_PROPNAME);
			System.clearProperty(KoPeMeConfiguration.KOPEME_SEARCHDEPTH_PROPNAME);
		}
	}
	
	@Test
	public void testFindProjectNameUsingPom() throws Exception {
		try {
			System.setProperty(KoPeMeConfiguration.KOPEME_WORKINGDIR_PROPNAME, TestUtils.PATH_TO_TESTPOM_SUBFOLDER);
			System.setProperty(KoPeMeConfiguration.KOPEME_SEARCHDEPTH_PROPNAME, "1");
			assertEquals(TestUtils.TESTPOM_EXPECTED_PROJECT_NAME, new KoPeMeConfiguration().getProjectName());
		} finally {
			System.clearProperty(KoPeMeConfiguration.KOPEME_WORKINGDIR_PROPNAME);
			System.clearProperty(KoPeMeConfiguration.KOPEME_SEARCHDEPTH_PROPNAME);
		}
	}
	
	@Test
	public void testProjectNameIsSettable() throws Exception {
		System.setProperty(KoPeMeConfiguration.KOPEME_PROJECTNAME_PROPNAME, FIXTURE);
		try {
			assertEquals(FIXTURE, new KoPeMeConfiguration().getProjectName());
		} finally {
			System.clearProperty(KoPeMeConfiguration.KOPEME_PROJECTNAME_PROPNAME);
		}
	}
	
}
