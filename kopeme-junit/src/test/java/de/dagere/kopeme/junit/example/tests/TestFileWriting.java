package de.dagere.kopeme.junit.example.tests;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTest;

public class TestFileWriting {
	
	@Rule
    public TemporaryFolder folder= new TemporaryFolder();
	
	@Test
	public void testNormalWriting(){
		JUnitCore jc = new JUnitCore();
		jc.run(JUnitAdditionTest.class);
		
		String name = JUnitAdditionTest.class.getName() + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
		f.delete();
	}
	
	@Test
	public void testDoubleWriting(){
		JUnitCore jc = new JUnitCore();
		jc.run(JUnitAdditionTest.class);
		
		jc.run(JUnitAdditionTest.class);
		
		String name = JUnitAdditionTest.class.getName() + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
		f.delete();
	}
	
	@Test
	public void testExceptionWriting(){
		
	}
}
