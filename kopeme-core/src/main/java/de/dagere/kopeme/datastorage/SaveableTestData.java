package de.dagere.kopeme.datastorage;

import java.io.File;

import de.dagere.kopeme.datacollection.TestResult;

public abstract class SaveableTestData {

   private static FolderProvider PROVIDER = FolderProvider.getInstance();

   private static class SaveableTestDataFactory<T extends SaveableTestData> {
      private final Class<T> type;

      public SaveableTestDataFactory(final Class<T> type) {
         this.type = type;
      }

      public T createTestData(final File folder, final String testcasename, final String filename, final TestResult tr, final int warmup, int repetitions,
            final boolean saveValues) {
         T returnable;
         try {
            returnable = type.newInstance();
            returnable.setFolder(folder);
            returnable.setTestcasename(testcasename);
            returnable.setFilename(filename);
            returnable.setTr(tr);
            returnable.setRepetitions(repetitions);
            returnable.setSaveValues(saveValues);
            returnable.setWarmupExecutions(warmup);
            return returnable;
         } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e); // should never happen
         }
      }
   }

   public static FineTestData createFineTestData(final String testcasename, final String filename, final TestResult tr, final int warmup, int repetitions, final boolean saveValues) {
      return createFineTestData(createDefaultFolder(filename), testcasename, filename, tr, warmup, repetitions, saveValues);
   }

   private static File createDefaultFolder(final String filename) {
      final File folder = new File(PROVIDER.getFolderFor(filename));
      folder.mkdirs();
      return folder;
   }

   public static AssertFailureTestData createAssertFailedTestData(final String testcasename, final String filename, final TestResult tr, final int warmup, int repetitions, 
         final boolean saveValues) {
      return createAssertFailedTestData(createDefaultFolder(filename), testcasename, filename, tr, warmup, repetitions, saveValues);
   }

   public static TestErrorTestData createErrorTestData(final String testcasename, final String filename, final TestResult tr, final int warmup, int repetitions, final boolean saveValues) {
      return createErrorTestData(createDefaultFolder(filename), testcasename, filename, tr, warmup,  repetitions, saveValues);
   }

   public static FineTestData createFineTestData(final File folder, final String testcasename, final String filename, final TestResult tr, final int warmup, int repetitions,
         final boolean saveValues) {
      return new SaveableTestDataFactory<>(FineTestData.class).createTestData(folder, testcasename, filename, tr, warmup, repetitions, saveValues);
   }

   public static AssertFailureTestData createAssertFailedTestData(final File folder, final String testcasename, final String filename, final TestResult tr,
         final int warmup, int repetitions, final boolean saveValues) {
      return new SaveableTestDataFactory<>(AssertFailureTestData.class).createTestData(folder, testcasename, filename, tr, warmup, repetitions, saveValues);
   }

   public static TestErrorTestData createErrorTestData(final File folder, final String testcasename, final String filename, final TestResult tr, final int warmup, int repetitions,
         final boolean saveValues) {
      return new SaveableTestDataFactory<>(TestErrorTestData.class).createTestData(folder, testcasename, filename, tr, warmup, repetitions, saveValues);
   }

   private File folder;
   private String testcasename, filename;
   private TestResult tr;
   private int warmupExecutions, repetitions;

   /**
    * @return the warmupExecutions
    */
   public int getWarmupExecutions() {
      return warmupExecutions;
   }

   /**
    * @param warmupExecutions the warmupExecutions to set
    */
   public void setWarmupExecutions(final int warmupExecutions) {
      this.warmupExecutions = warmupExecutions;
   }

   private boolean saveValues;

   public static class FineTestData extends SaveableTestData {
   }

   public static class AssertFailureTestData extends SaveableTestData {
   }

   public static class TestErrorTestData extends SaveableTestData {
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

   public boolean isSaveValues() {
      return saveValues;
   }

   public void setSaveValues(final boolean saveValues) {
      this.saveValues = saveValues;
   }

   public int getRepetitions() {
      return repetitions;
   }

   public void setRepetitions(int repetitions) {
      this.repetitions = repetitions;
   }


}
