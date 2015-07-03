package de.dagere.kopeme.instrumentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileFilter;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.writer.filesystem.ChangeableFolderSyncFsWriter;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.FolderProvider;

public class TestKiekerMeasureUtil {

	private static File WRITING_FOLDER;

	@BeforeClass
	public static void initClass() throws Exception{
		TestUtils.cleanAndSetKoPeMeOutputFolder();
		WRITING_FOLDER = new File(FolderProvider.getInstance().getFolderFor(TestKiekerMeasureUtil.class.getName()));
		ChangeableFolderSyncFsWriter inst = ChangeableFolderSyncFsWriter.getInstance(KiekerMeasureUtil.CTRLINST);
		inst.setFolder(WRITING_FOLDER);
	}
	
	@Test 
	public void testSignature() throws Exception { // don't rename this method!
		String thisClassName = TestKiekerMeasureUtil.class.getName();
		KiekerMeasureUtil testable = createTestable();
		assertEquals(thisClassName + ".createTestable(TestKiekerMeasureUtil.java:47)", testable.signature);
		testable = new KiekerMeasureUtil();
		assertEquals(thisClassName + ".testSignature(TestKiekerMeasureUtil.java:42)", testable.signature);
	}
	
	private KiekerMeasureUtil createTestable() { // don't rename this method!
		return new KiekerMeasureUtil(); // don't move this around, the line number is important to verify!
	}
	
	@Test
	public void testMeasureRecords() throws Exception {
		KiekerMeasureUtil testable = createTestable();
		testable.measureBefore();
		long sleepDur = 10;
		Thread.sleep(sleepDur);
		OperationExecutionRecord record = testable.measureAfter();
		long micros = TimeUnit.NANOSECONDS.toMillis(record.getTout() - record.getTin());
		assertThat(micros, Matchers.greaterThanOrEqualTo(sleepDur));
		assertEquals(testable.signature, record.getOperationSignature());
		FileFilter kiekerFiFi = new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				return pathname.getName().startsWith("kieker");
			}
		};
		File[] r = WRITING_FOLDER.listFiles(kiekerFiFi);
		assertEquals(1, r.length);
		File kiekerMainDir = r[0];
		assertTrue(kiekerMainDir.isDirectory());
		System.out.println(r[0].getAbsolutePath());
		File[] kiekerMainDirContent = kiekerMainDir.listFiles(kiekerFiFi);
		assertEquals(1, r.length);
		File kiekerDatFile = kiekerMainDirContent[0];
		try(Scanner s = new Scanner(kiekerDatFile).useDelimiter("\n")){
			assertNotNull(s.next());
			assertNotNull(s.next());
			try {
				s.next();
				fail("shoulld only contain 3 lines...");
			} catch (NoSuchElementException e) {
				//expected
			}
		}
		
	}
	
}
