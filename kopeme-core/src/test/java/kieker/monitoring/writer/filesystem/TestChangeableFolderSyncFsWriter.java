package kieker.monitoring.writer.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;

import org.junit.BeforeClass;
import org.junit.Test;

import de.dagere.kopeme.TestUtils;
import kieker.common.configuration.Configuration;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;

/**
 * Writes Kieker examples result to the kieker monitoring controller for tests.
 * 
 * @author dhaeb
 *
 */
public class TestChangeableFolderSyncFsWriter {

	private static IMonitoringController MONITORING_CONTROLLER;

	private static final File DEFAULT_FOLDER = new File("target/test-classes/kieker_testresults");
	private static final File NEW_FOLDER_AT_RUNTIME = new File("target/test-classes/kieker_testresults_changed_folder");
	private static final File NEW_FOLDER_AT_RUNTIME2 = new File("target/test-classes/kieker_testresults_changed_folder2");

	@BeforeClass
	public static void setupClass() {
		TestUtils.deleteRecursively(DEFAULT_FOLDER);
		TestUtils.deleteRecursively(NEW_FOLDER_AT_RUNTIME);
		TestUtils.deleteRecursively(NEW_FOLDER_AT_RUNTIME2);
		DEFAULT_FOLDER.mkdirs();
		NEW_FOLDER_AT_RUNTIME.mkdirs();
		NEW_FOLDER_AT_RUNTIME2.mkdirs();
		final Configuration config = ConfigurationFactory.createSingletonConfiguration();
		final String absolutePath = DEFAULT_FOLDER.getAbsolutePath();
		config.setProperty("kieker.monitoring.writer", ChangeableFolderWriter.class.getName());
		config.setProperty(ChangeableFolderWriter.CONFIG_PATH, absolutePath);
		config.setProperty(ChangeableFolderWriter.CONFIG_MAXENTRIESINFILE, "100");
		config.setProperty(ChangeableFolderWriter.CONFIG_MAXLOGFILES, "-1");
		config.setProperty(ChangeableFolderWriter.CONFIG_MAXLOGSIZE, "1");
		config.setProperty(ChangeableFolderWriter.CONFIG_FLUSH, "true");
		config.setProperty(ChangeableFolderWriter.CONFIG_BUFFER, "8192");
		config.setProperty(ChangeableFolderWriter.REAL_WRITER, "AsciiFileWriter");
		MONITORING_CONTROLLER = MonitoringController.createInstance(config);
		MONITORING_CONTROLLER.enableMonitoring();
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
			Thread.sleep(2L);
		}

		private void b() throws InterruptedException {
			final long tin = MONITORING_CONTROLLER.getTimeSource().getTime();
			c();
			final long tout = MONITORING_CONTROLLER.getTimeSource().getTime();
			createAndWriteOperationExecutionRecord(tin, tout, "public void " + Sample.class.getName() + ".c()");
		}

		protected void c() throws InterruptedException {
			Thread.sleep(2L);
		}
	}

	@Test
	public void testConfigConvertion() throws Exception {
		final Configuration c = new Configuration();
		final int fixture = 100;
		c.setProperty(ChangeableFolderWriter.CONFIG_MAXENTRIESINFILE, Integer.toString(fixture));
		final ChangeableFolderWriter testable = ChangeableFolderWriter.getInstance();
		final Configuration result = testable.toWriterConfiguration(c, BinaryFileWriter.class);
		final int intResult = result.getIntProperty(BinaryFileWriter.CONFIG_MAXENTRIESINFILE);
		assertEquals(fixture, intResult);
	}

	@Test
	public void testChangesFolderCorrectly() throws Exception {
		final ChangeableFolderWriter testable = ChangeableFolderWriter.getInstance();
		final int rounds = 15, lines = 15;
		runFixture(rounds);
		testable.setFolder(NEW_FOLDER_AT_RUNTIME);
		runFixture(rounds);
		testable.setFolder(NEW_FOLDER_AT_RUNTIME2);
		runFixture(rounds);
		assertKiekerFileConstainsLines(DEFAULT_FOLDER, lines + 2); // TODO due to the meta data entry, which are written to every folder
		assertKiekerFileConstainsLines(NEW_FOLDER_AT_RUNTIME, lines + 1);
		assertKiekerFileConstainsLines(NEW_FOLDER_AT_RUNTIME2, lines + 1);
	}

	private void runFixture(int rounds) throws InterruptedException,
			ExecutionException {
		for (int i = 0; i < rounds / 3; i++) {
			final Sample fixture = new Sample();
			final long tin = MONITORING_CONTROLLER.getTimeSource().getTime();
			fixture.a();
			final long tout = MONITORING_CONTROLLER.getTimeSource().getTime();
			createAndWriteOperationExecutionRecord(tin, tout, "public void " + Sample.class.getName() + ".a()");
		}
		Thread.sleep(5);//TODO: Remove dirty workaround..
	}

	private void assertKiekerFileConstainsLines(final File kiekerFolder, final int lines) throws IOException {
		final File[] listFiles = kiekerFolder.listFiles();
		assertEquals(1, listFiles.length); // only the kieker root dir
		final File kiekerRootDir = listFiles[0];
		assertTrue("Kieker root dir should be a directory!", kiekerRootDir.isDirectory());
		final File[] kiekerFiles = kiekerRootDir.listFiles();
		assertEquals("There should be 2 kieker files!", 2, kiekerFiles.length);
		final File[] measureFile = kiekerRootDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return !pathname.getName().equals("kieker.map");
			}
		});
		assertEquals(1, measureFile.length);
		final File currentMeasureFile = measureFile[0];
		assertEquals(lines, Files.readAllLines(currentMeasureFile.toPath(), StandardCharsets.UTF_8).size());
	}

}
