package de.dagere.kopeme.example.tests;

import java.io.File;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.exampletests.junit.JUnitAdditionTest;

public class TestFileWriting {
	
	@Test
	public void testNormalWriting(){
		JUnitCore jc = new JUnitCore();
		jc.run(JUnitAdditionTest.class);
		
		String name = JUnitAdditionTest.class.getName() + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
	}
	
	@Test
	public void testDoubleWriting(){
		JUnitCore jc = new JUnitCore();
		jc.run(JUnitAdditionTest.class);
		
		jc.run(JUnitAdditionTest.class);
		
		String name = JUnitAdditionTest.class.getName() + ".yaml";
		File f = new File(name);
		Assert.assertTrue("Datei " + name + " sollte existieren", f.exists());
	}
	
	@Test
	public void testExceptionWriting(){
		
	}
}
