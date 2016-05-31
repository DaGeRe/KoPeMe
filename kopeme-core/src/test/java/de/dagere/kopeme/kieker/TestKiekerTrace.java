package de.dagere.kopeme.kieker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

public class TestKiekerTrace {

	@Ignore
	@Test
	public void testCsvCreationConstructor() throws Exception {
		InputStream resource = getClass().getResourceAsStream(
				"/kieker-testresources/kieker-20150618-064830855-UTC-dhaeb-Latitude-E7450-KIEKER/kieker20150618-064830860-UTC-000.dat");
		assertNotNull(resource);
		KiekerTrace testable = new KiekerTrace(resource);
		Map<String, List<KiekerTraceEntry>> entries = testable.getEntries();
		assertEquals(3, entries.size());
		for (List<KiekerTraceEntry> values : entries.values()) {
			assertEquals(5, values.size());
		}

		List<KiekerTraceEntry> aList = entries.get("public void kieker.monitoring.writer.filesystem.TestChangeableFoolderSyncFsWriter$Sample.a()");
		// $1;1434610110865620192;public void
		// kieker.monitoring.writer.filesystem.TestChangeableFoolderSyncFsWriter$Sample.a();<no-session-id>;-1;1434610110854837208;1434610110865603981;myHost;-1;-1
		KiekerTraceEntry verifiable = aList.get(4);
		assertEquals(1, verifiable.getMappningNumber());
		assertEquals(1434610110865620192L, verifiable.getLoggingTime());
		assertEquals(1434610110854837208L, verifiable.getTin());
		assertEquals(1434610110865603981L, verifiable.getTout());
		KiekerTraceEntry verifiable2 = aList.get(0);
		assertEquals(1, verifiable2.getMappningNumber());
		assertEquals(1434610110907520135L, verifiable2.getLoggingTime());
		assertEquals(1434610110897086288L, verifiable2.getTin());
		assertEquals(1434610110907503780L, verifiable2.getTout());
	}
}
