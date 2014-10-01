package de.dagere.kopeme.junit3.example.tests;

import java.io.File;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTest;

public class TestFileWriting extends TestCase {

	public void testNormalWriting() {
		TestRunner.run(JUnitAdditionTest.class);

		String name = JUnitAdditionTest.class.getName() + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
		// f.delete();
	}

	public void testDoubleWriting() {
		TestRunner.run(JUnitAdditionTest.class);
		TestRunner.run(JUnitAdditionTest.class);

		String name = JUnitAdditionTest.class.getName() + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
		// f.delete();
	}
}
