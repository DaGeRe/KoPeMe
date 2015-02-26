package de.dagere.kopeme.visualizer;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class TestTrendVisualizer {

	@Ignore
	@Test
	public void testVisualisation() throws JAXBException {
		VisualizationGenerator.visualizeFile("src/test/resources/de.dagere.kopeme.junit.exampletests.runner.ExampleAssertionTest.testAssertionAddition.yaml", 1000, 1000, "");

		File f = new File("testAssertionAddition_de.dagere.kopeme.datacollection.RAMUsageCollector.png");
		Assert.assertEquals(f.exists(), true);
		f.delete();

		File f2 = new File("testAssertionAddition_de.dagere.kopeme.datacollection.CPUUsageCollector.png");
		Assert.assertEquals(f2.exists(), true);
		f2.delete();

		File f3 = new File("testAssertionAddition_de.dagere.kopeme.datacollection.TimeDataCollector.png");
		Assert.assertEquals(f3.exists(), true);
		f3.delete();
	}
}
