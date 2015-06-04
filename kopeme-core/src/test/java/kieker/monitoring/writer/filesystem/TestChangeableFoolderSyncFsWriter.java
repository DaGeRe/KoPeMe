package kieker.monitoring.writer.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;

import kieker.common.configuration.Configuration;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dagere.kopeme.TestUtils;

public class TestChangeableFoolderSyncFsWriter {

	private static IMonitoringController MONITORING_CONTROLLER;

	private static final File DEFAULT_FOLDER = new File("target/test-classes/kieker_testresults");
	private static final File NEW_FOLDER_AT_RUNTIME = new File("target/test-classes/kieker_testresults_changed_folder");

	@BeforeClass
	public static void setupClass() {
		TestUtils.deleteRecursively(DEFAULT_FOLDER);
		TestUtils.deleteRecursively(NEW_FOLDER_AT_RUNTIME);
		DEFAULT_FOLDER.mkdirs();
		NEW_FOLDER_AT_RUNTIME.mkdirs();
		Configuration config = ConfigurationFactory.createSingletonConfiguration();
		String absolutePath = DEFAULT_FOLDER.getAbsolutePath();
		config.setProperty("kieker.monitoring.writer", ChangeableFolderSyncFsWriter.class.getName());
		config.setProperty(ChangeableFolderSyncFsWriter.CONFIG_PATH, absolutePath);
		config.setProperty(ChangeableFolderSyncFsWriter.CONFIG_MAXENTRIESINFILE, "100");
		config.setProperty(ChangeableFolderSyncFsWriter.CONFIG_MAXLOGFILES, "-1");
		config.setProperty(ChangeableFolderSyncFsWriter.CONFIG_MAXLOGSIZE, "1");
		config.setProperty(ChangeableFolderSyncFsWriter.CONFIG_FLUSH, "true");
		config.setProperty(ChangeableFolderSyncFsWriter.CONFIG_BUFFER, "8192");
		MONITORING_CONTROLLER = MonitoringController.createInstance(config);
	}

	private static void createAndWriteOperationExecutionRecord(final long tin, final long tout, String methodSignature) {
		final OperationExecutionRecord e = new OperationExecutionRecord(
				methodSignature,
				OperationExecutionRecord.NO_SESSION_ID,
				OperationExecutionRecord.NO_TRACE_ID,
				tin, tout, "myHost",
				OperationExecutionRecord.NO_EOI_ESS,
				OperationExecutionRecord.NO_EOI_ESS);
		MONITORING_CONTROLLER.newMonitoringRecord(e);
	}

	public static class Sample {
		public void a() throws InterruptedException, ExecutionException {
			final long tin = MONITORING_CONTROLLER.getTimeSource().getTime();
			b();
			final long tout = MONITORING_CONTROLLER.getTimeSource().getTime();
			createAndWriteOperationExecutionRecord(tin, tout, "public void " + Sample.class.getName() + ".b()");
			Thread.sleep(5L);
		}

		private void b() throws InterruptedException {
			final long tin = MONITORING_CONTROLLER.getTimeSource().getTime();
			c();
			final long tout = MONITORING_CONTROLLER.getTimeSource().getTime();
			createAndWriteOperationExecutionRecord(tin, tout, "public void " + Sample.class.getName() + ".c)");
		}

		protected void c() throws InterruptedException {
			Thread.sleep(5L);
		}
	}

	@Test
	public void testConfigConvertion() throws Exception {
		Configuration c = new Configuration();
		int fixture = 100;
		c.setProperty(ChangeableFolderSyncFsWriter.CONFIG_MAXENTRIESINFILE, Integer.toString(fixture));
		ChangeableFolderSyncFsWriter testable = ChangeableFolderSyncFsWriter.getInstance(MONITORING_CONTROLLER);
		Configuration result = testable.toSyncFsWriterConfiguration(c);
		int intResult = result.getIntProperty(SyncFsWriter.CONFIG_MAXENTRIESINFILE);
		assertEquals(fixture, intResult);
	}

	@Test
	public void testChangesFolderCorrectly() throws Exception {
		ChangeableFolderSyncFsWriter testable = ChangeableFolderSyncFsWriter.getInstance(MONITORING_CONTROLLER);
		int rounds = 10, lines = rounds / 2 * 3;
		for (int i = 0; i < rounds; i++) {
			if ((i + 1) % 6 == 0) {
				testable.setFolder(NEW_FOLDER_AT_RUNTIME);
			}
			Sample fixture = new Sample();
			final long tin = MONITORING_CONTROLLER.getTimeSource().getTime();
			fixture.a();
			final long tout = MONITORING_CONTROLLER.getTimeSource().getTime();
			createAndWriteOperationExecutionRecord(tin, tout, "public void " + Sample.class.getName() + ".a()");
		}
		assertKiekerFileConstainsLines(DEFAULT_FOLDER, lines + 1); // TODO due to the meta data entry, which is not written when changing the folder
		assertKiekerFileConstainsLines(NEW_FOLDER_AT_RUNTIME, lines);
	}

	private void assertKiekerFileConstainsLines(File kiekerFolder, int lines) throws IOException {
		File[] listFiles = kiekerFolder.listFiles();
		assertEquals(1, listFiles.length); // only the kieker root dir
		File kiekerRootDir = listFiles[0];
		assertTrue("Kieker root dir should be a directory!", kiekerRootDir.isDirectory());
		File[] kiekerFiles = kiekerRootDir.listFiles();
		assertEquals("There should be 2 kieker files!", 2, kiekerFiles.length);
		File[] measureFile = kiekerRootDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return !pathname.getName().equals("kieker.map");
			}
		});
		assertEquals(1, measureFile.length);
		File currentMeasureFile = measureFile[0];
		assertEquals(lines, Files.readAllLines(currentMeasureFile.toPath(), StandardCharsets.UTF_8).size());
	}

}
