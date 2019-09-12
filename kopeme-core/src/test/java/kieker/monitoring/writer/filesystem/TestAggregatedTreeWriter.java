package kieker.monitoring.writer.filesystem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.kieker.KoPeMeKiekerSupport;
import kieker.common.configuration.Configuration;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.MonitoringController;

/**
 * Writes Kieker example results for the {@link AggregatedTreeWriter}
 * 
 * @author reichelt
 *
 */
public class TestAggregatedTreeWriter {

	private static final File DEFAULT_FOLDER = new File("target/test-classes/kieker_testresults");

	@BeforeClass
	public static void setupClass() {
		emptyFolder(DEFAULT_FOLDER);
		
		final Configuration config = ConfigurationFactory.createSingletonConfiguration();
		final String absolutePath = DEFAULT_FOLDER.getAbsolutePath();
		config.setProperty("kieker.monitoring.writer", AggregatedTreeWriter.class.getName());
		config.setProperty(AggregatedTreeWriter.CONFIG_PATH, absolutePath);
		config.setProperty(AggregatedTreeWriter.CONFIG_WRITEINTERVAL, "5");
		Sample.MONITORING_CONTROLLER = MonitoringController.createInstance(config);
		Sample.MONITORING_CONTROLLER.enableMonitoring();
	}

   private static void emptyFolder(final File folder) {
      TestUtils.deleteRecursively(folder);
      folder.mkdirs();
   }

	static void createAndWriteOperationExecutionRecord(final long tin, final long tout, final String methodSignature) {
		final OperationExecutionRecord e = new OperationExecutionRecord(
				methodSignature,
				OperationExecutionRecord.NO_SESSION_ID,
				OperationExecutionRecord.NO_TRACE_ID,
				tin, tout, "myHost",
				OperationExecutionRecord.NO_EOI_ESS,
				OperationExecutionRecord.NO_EOI_ESS);
		Sample.MONITORING_CONTROLLER.newMonitoringRecord(e);
	}

	@Test
	public void testWriting() throws Exception {
		runFixture(15);
		KoPeMeKiekerSupport.finishMonitoring(Sample.MONITORING_CONTROLLER);
		assertJSONFileContainsMethods(DEFAULT_FOLDER, 3); // TODO due to the meta data entry, which are written to every folder
	}

	private void runFixture(final int rounds) throws InterruptedException,
			ExecutionException {
		for (int i = 0; i < rounds / 3; i++) {
			final Sample fixture = new Sample();
			final long tin = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
			fixture.a();
			final long tout = Sample.MONITORING_CONTROLLER.getTimeSource().getTime();
			createAndWriteOperationExecutionRecord(tin, tout, "public void " + Sample.class.getName() + ".a()");
		}
		Thread.sleep(5);//TODO: Remove dirty workaround..
	}

	private void assertJSONFileContainsMethods(final File kiekerFolder, final int methods) throws IOException {
		final File currentMeasureFile = assertOneMeasureFile(kiekerFolder);
	   System.out.println("File: " + currentMeasureFile.getAbsolutePath());
	   
	   final Map<CallTreeNode, SummaryStatistics> data = new ObjectMapper().readValue(currentMeasureFile, Map.class);
	   
		assertEquals(data.keySet().size(), 3);
	}

   private File assertOneMeasureFile(final File kiekerFolder) {
      final File[] listFiles = kiekerFolder.listFiles();
		assertEquals(1, listFiles.length); // only the kieker root dir
		final File kiekerRootDir = listFiles[0];
		assertTrue("Kieker root dir should be a directory!", kiekerRootDir.isDirectory());
		final File[] kiekerFiles = kiekerRootDir.listFiles();
		assertEquals("There should be one kieker file!", 1, kiekerFiles.length);
		final File[] measureFile = kiekerRootDir.listFiles(new FileFilter() {

			@Override
			public boolean accept(final File pathname) {
				return !pathname.getName().equals("kieker.map");
			}
		});
		assertEquals(1, measureFile.length);
		final File currentMeasureFile = measureFile[0];
      return currentMeasureFile;
   }

}
