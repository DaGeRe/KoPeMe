package de.dagere.kopeme.junit.tests;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.JUnitCore;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.XMLDataLoader;
import de.dagere.kopeme.generated.Kopemedata;
import de.dagere.kopeme.generated.Kopemedata.Testcases;
import de.dagere.kopeme.generated.TestcaseType;
import de.dagere.kopeme.generated.TestcaseType.Datacollector;
import de.dagere.kopeme.generated.TestcaseType.Datacollector.Result;
import de.dagere.kopeme.junit.exampletests.runner.JUnitAdditionTest;
import de.dagere.kopeme.junit.exampletests.runner.JUnitMultiplicationTest;

/**
 * Testet das Schreiben in Dateien
 * 
 * @author reichelt
 *
 */
public class TestFileWriting {

	private static final String TEST_MULTIPLICATION = "testMultiplication";
	private static final String TEST_ADDITION = "testAddition";
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@BeforeClass
	public static void initClass() throws IOException {
		TestUtils.cleanAndSetKoPeMeOutputFolder();
	}

	@Test
	public void testNormalWriting() {
		JUnitCore jc = new JUnitCore();
		jc.run(JUnitAdditionTest.class);
		String testClass = JUnitAdditionTest.class.getName();
		File f = TestUtils.xmlfileForKoPeMeRuleTest(testClass, TEST_ADDITION);
		Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());
		f.delete();
	}

	@Test
	public void testDoubleWriting() {
		JUnitCore jc = new JUnitCore();
		jc.run(JUnitAdditionTest.class);
		jc.run(JUnitAdditionTest.class);
		File f = TestUtils.xmlfileForKoPeMeRuleTest(JUnitAdditionTest.class.getCanonicalName(), TEST_ADDITION);
		Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());
		f.delete();
	}

	@Test
	public void testResults() {
		JUnitCore jc = new JUnitCore();
		jc.run(JUnitMultiplicationTest.class);
		File f = TestUtils.xmlfileForKoPeMeRuleTest(JUnitMultiplicationTest.class.getCanonicalName(), TEST_MULTIPLICATION);
		Assert.assertTrue("Datei " + f + " sollte existieren", f.exists());

		try {
			Kopemedata kd = new XMLDataLoader(f).getFullData();
			Testcases tc = kd.getTestcases();
			Assert.assertEquals(JUnitMultiplicationTest.class.getCanonicalName(), tc.getClazz());

			TestcaseType tct = null;
			for (TestcaseType t : tc.getTestcase()) {
				if (t.getName().equals(TEST_MULTIPLICATION)) {
					tct = t;
					break;
				}
			}
			Assert.assertNotNull(tct);

			Datacollector timeCollector = null;
			for (Datacollector dc : tct.getDatacollector()) {
				if (dc.getName().equals("de.dagere.kopeme.datacollection.TimeDataCollector")) {
					timeCollector = dc;
					break;
				}
			}
			Assert.assertNotNull(timeCollector);

			for (Result r : timeCollector.getResult()) {
				int val = new Integer(r.getValue());
				int min = (int) r.getMin();
				int max = (int) r.getMax();
				Assert.assertThat(val, Matchers.greaterThan(0));
				Assert.assertThat(max, Matchers.greaterThanOrEqualTo(val));
				Assert.assertThat(val, Matchers.greaterThanOrEqualTo(min));
			}

		} catch (JAXBException e1) {
			e1.printStackTrace();
		}
	}

	@Test
	public void testExceptionWriting() {
		//FIXME why is this empty?
	}
}
