package de.dagere.kopeme.junit.example.tests;

import java.io.File;

import junit.framework.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTest;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTestFullData;

public class TestFulldataFunctionality extends TestCase {

	public void testNoFullWriting() {
		TestRunner.run(JUnitAdditionTest.class);

		String name = JUnitAdditionTest.class.getName() + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
		f.delete();
	}

	public void testFullWriting() {
		TestRunner.run(JUnitAdditionTestFullData.class);

		String name = JUnitAdditionTestFullData.class.getName() + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
		f.delete();
	}

}
