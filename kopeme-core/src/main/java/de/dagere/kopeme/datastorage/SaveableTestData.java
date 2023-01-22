package de.dagere.kopeme.datastorage;

import java.io.File;

import de.dagere.kopeme.datacollection.TestResult;

public abstract class SaveableTestData {

   private static FolderProvider PROVIDER = FolderProvider.getInstance();

   static class SaveableTestDataFactory {

      public static SaveableTestData createTestData(SaveableTestData returnable, final File folder, final String testcasename,
            final String filename, final TestResult tr, final RunConfiguration configuration) {
         returnable.setFolder(folder);
         returnable.setTestcasename(testcasename);
         returnable.setFilename(filename);
         returnable.setTr(tr);
         returnable.setConfiguration(configuration);
         return returnable;
      }
   }

   public static SaveableTestData createFineTestData(final String testcasename, final String filename, final TestResult tr, final RunConfiguration configuration) {
      return createFineTestData(createDefaultFolder(filename), testcasename, filename, tr, configuration);
   }

   private static File createDefaultFolder(final String filename) {
      final File folder = new File(PROVIDER.getFolderFor(filename));
      folder.mkdirs();
      return folder;
   }

   public static SaveableTestData createAssertFailedTestData(final String testcasename, final String filename, final TestResult tr, final RunConfiguration configuration) {
      return createAssertFailedTestData(createDefaultFolder(filename), testcasename, filename, tr, configuration);
   }

   public static SaveableTestData createErrorTestData(final String testcasename, final String filename, final TestResult tr, final RunConfiguration configuration) {
      return createErrorTestData(createDefaultFolder(filename), testcasename, filename, tr, configuration);
   }

   public static SaveableTestData createFineTestData(final File folder, final String testcasename, final String filename, final TestResult tr, final RunConfiguration configuration) {
      FineTestData testData = new FineTestData();
      return SaveableTestDataFactory.createTestData(testData, folder, testcasename, filename, tr, configuration);
   }

   public static SaveableTestData createAssertFailedTestData(final File folder, final String testcasename, final String filename, final TestResult tr,
         final RunConfiguration configuration) {
      TestErrorData errorData = new TestErrorData(false, false, true);
      return SaveableTestDataFactory.createTestData(errorData, folder, testcasename, filename, tr, configuration);
   }

   public static SaveableTestData createErrorTestData(final File folder, final String testcasename, final String filename, final TestResult tr,
         final RunConfiguration configuration) {
      TestErrorData errorData = new TestErrorData(true, false, false);
      return SaveableTestDataFactory.createTestData(errorData, folder, testcasename, filename, tr, configuration);
   }
   
   public static SaveableTestData createSubprocessTimeoutData(final String testcasename, final String filename, final TestResult tr,
         final RunConfiguration configuration) {
      TestErrorData errorData = new TestErrorData(false, true, false);
      return SaveableTestDataFactory.createTestData(errorData, createDefaultFolder(filename), testcasename, filename, tr, configuration);
   }

   private File folder;
   private String testcasename, filename;
   private TestResult tr;
   private RunConfiguration configuration;

   public RunConfiguration getConfiguration() {
      return configuration;
   }

   public void setConfiguration(final RunConfiguration configuration) {
      this.configuration = configuration;
   }

   public static class FineTestData extends SaveableTestData {
   }

   public static class TestErrorData extends SaveableTestData {
      private final boolean error;
      private final boolean subthreadTimeout;
      private final boolean assertFailed;

      public TestErrorData(boolean error, boolean subthreadTimeout, boolean assertFailed) {
         this.error = error;
         this.subthreadTimeout = subthreadTimeout;
         this.assertFailed = assertFailed;
      }

      public boolean isAssertFailed() {
         return assertFailed;
      }

      public boolean isError() {
         return error;
      }

      public boolean isSubthreadTimeout() {
         return subthreadTimeout;
      }
   }

   public File getFolder() {
      return folder;
   }

   public void setFolder(final File folder) {
      this.folder = folder;
   }

   public String getTestcasename() {
      return testcasename;
   }

   public void setTestcasename(final String testcasename) {
      this.testcasename = testcasename;
   }

   public String getFilename() {
      return filename;
   }

   public void setFilename(final String filename) {
      this.filename = filename;
   }

   public TestResult getTr() {
      return tr;
   }

   public void setTr(final TestResult tr) {
      this.tr = tr;
   }
}
