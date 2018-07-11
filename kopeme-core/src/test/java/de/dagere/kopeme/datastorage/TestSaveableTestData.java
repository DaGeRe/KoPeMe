package de.dagere.kopeme.datastorage;

import static de.dagere.kopeme.datastorage.SaveableTestData.createAssertFailedTestData;
import static de.dagere.kopeme.datastorage.SaveableTestData.createErrorTestData;
import static de.dagere.kopeme.datastorage.SaveableTestData.createFineTestData;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.datacollection.TestResult;

public class TestSaveableTestData {

	private static final String RESULT_FILENAME = "myFileName";
	private static final String THIS_DIR = ".";
	private static final String MY_TEST_CASE_NAME = "myTestCaseName";
	private static final TestResult COMPLEX = new TestResult(MY_TEST_CASE_NAME, 1, DataCollectorList.STANDARD);
	private static final boolean SAVE_VALUES = true;

	@Test
	public void testFailureData() throws Exception {
		assertValuesSet(createAssertFailedTestData(new File(THIS_DIR), MY_TEST_CASE_NAME, RESULT_FILENAME, COMPLEX, 0, 1, SAVE_VALUES));
	}

	@Test
	public void testFineData() throws Exception {
		assertValuesSet(createFineTestData(new File(THIS_DIR), MY_TEST_CASE_NAME, RESULT_FILENAME, COMPLEX, 0, 1, SAVE_VALUES));
	}

	@Test
	public void testErrorData() throws Exception {
		assertValuesSet(createErrorTestData(new File(THIS_DIR), MY_TEST_CASE_NAME, RESULT_FILENAME, COMPLEX, 0, 1, SAVE_VALUES));
	}

	private void assertValuesSet(final SaveableTestData data) {
		assertEquals(RESULT_FILENAME, data.getFilename());
		assertEquals(new File(THIS_DIR), data.getFolder());
		assertEquals(COMPLEX, data.getTr());
		assertEquals(SAVE_VALUES, data.isSaveValues());
		assertEquals(MY_TEST_CASE_NAME, data.getTestcasename());
	}
}
