package de.dagere.kopeme.instrumentation;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestKoPeMeClassFileTransformaterDataForKieker {

	private static final String RESULT = "A%sa%s$kiekerMeasurementDataXXX = new de.dagere.kopeme.instrumentation.KiekerMeasureUtil(); $kiekerMeasurementDataXXX.measureBefore(); %s$kiekerMeasurementDataXXX.measureAfter(); %s1%sde.dagere.kopeme.instrumentation.KiekerMeasureUtil $kiekerMeasurementDataXXX".replace("%s", KoPeMeClassFileTransformaterDataForKieker.DEFAULT_ARG_SEPARATOR);

	@Test
	public void testOutputStringIsValid() throws Exception {
		String cmd = "A;;a;;1";
		KoPeMeClassFileTransformaterDataForKieker testable = new KoPeMeClassFileTransformaterDataForKieker(cmd);
		assertEquals(1, testable.getLevel());
		assertEquals(RESULT, testable.toString());
	}
}
