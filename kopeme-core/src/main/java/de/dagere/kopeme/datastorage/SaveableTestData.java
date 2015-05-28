package de.dagere.kopeme.datastorage;

import java.io.File;

import de.dagere.kopeme.datacollection.TestResult;

public abstract class SaveableTestData {

	private static class SaveableTestDataFactory <T extends SaveableTestData> {
		private final Class<T> type;
			
		public SaveableTestDataFactory(Class<T> type) {
			this.type = type;
		}

		public T createTestData(File folder, String testcasename, String filename, TestResult tr, boolean saveValues){
			T returnable;
			try {
				returnable = type.newInstance();
				setFields(returnable, folder, testcasename, filename, tr, saveValues);
				return returnable;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e); // should never happen
			}
		}
	}
	
	private static FolderProvider PROVIDER = FolderProvider.getInstance();
	
	public static FineTestData createFineTestData(String testcasename, String filename, TestResult tr, boolean saveValues){
		return createFineTestData(createDefaultFolder(filename), testcasename, filename, tr, saveValues);
	}
	
	private static File createDefaultFolder(String filename) {
		File folder = new File(PROVIDER.getFolderFor(filename));
		folder.mkdirs();
		return folder;
	}

	public static AssertFailureTestData createAssertFailedTestData(String testcasename, String filename, TestResult tr, boolean saveValues){
		return createAssertFailedTestData(createDefaultFolder(filename), testcasename, filename, tr, saveValues);
	}
	
	public static TestErrorTestData createErrorTestData(String testcasename, String filename, TestResult tr, boolean saveValues){
		return createErrorTestData(createDefaultFolder(filename), testcasename, filename, tr, saveValues);
	}
	
	public static FineTestData createFineTestData(File folder, String testcasename, String filename, TestResult tr, boolean saveValues){
		return new SaveableTestDataFactory<>(FineTestData.class).createTestData(folder, testcasename, filename, tr, saveValues);
	}
	
	public static AssertFailureTestData createAssertFailedTestData(File folder, String testcasename, String filename, TestResult tr, boolean saveValues){
		return new SaveableTestDataFactory<>(AssertFailureTestData.class).createTestData(folder, testcasename, filename, tr, saveValues);
	}
	
	public static TestErrorTestData createErrorTestData(File folder, String testcasename, String filename, TestResult tr, boolean saveValues){
		return new SaveableTestDataFactory<>(TestErrorTestData.class).createTestData(folder, testcasename, filename, tr, saveValues);
	}
	
	private static void setFields(SaveableTestData returnable, File folder, String testcasename, String filename, TestResult tr, boolean saveValues) {
		returnable.setFolder(folder);
		returnable.setTestcasename(testcasename);
		returnable.setFilename(filename);
		returnable.setTr(tr);
		returnable.setSaveValues(saveValues);
	}
	
	private File folder;
	private String testcasename, filename; 
	private TestResult tr;
	private boolean saveValues;
	
	public static class FineTestData extends SaveableTestData {}
	
	public static class AssertFailureTestData extends SaveableTestData {}
	
	public static class TestErrorTestData extends SaveableTestData {}
	
	public File getFolder() {
		return folder;
	}
	public void setFolder(File folder) {
		this.folder = folder;
	}
	public String getTestcasename() {
		return testcasename;
	}
	public void setTestcasename(String testcasename) {
		this.testcasename = testcasename;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public TestResult getTr() {
		return tr;
	}
	public void setTr(TestResult tr) {
		this.tr = tr;
	}
	public boolean isSaveValues() {
		return saveValues;
	}
	public void setSaveValues(boolean saveValues) {
		this.saveValues = saveValues;
	}
	
}
