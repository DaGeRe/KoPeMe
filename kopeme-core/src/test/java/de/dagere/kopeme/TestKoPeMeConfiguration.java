package de.dagere.kopeme;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestKoPeMeConfiguration {
	private static final String FIXTURE = "myProjectName";

	@Test
	public void testProjectNameProperty() throws Exception {
		assertEquals(KoPeMeConfiguration.DEFAULT_PROJECTNAME, KoPeMeConfiguration.getInstance().getProjectName());
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
