package de.dagere.kopeme.instrumentation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class TestKoPeMeClassFileTransformaterData {

	private static final String STRING_FORAMT_PATTERN = " %s";

	private String iClassFixture = "MyClass", 	
			   iMethodFixture = "MyMethod",
			   codeBefore = "before, before2; ", // the whitespaces are important here! ; and ;; need must be distinguishable!
			   codeAfter = "after; ";
	
	private int level = 1;

	private KoPeMeClassFileTransformaterData testable;
	
	@Before
	public void setup(){
		String fixtureInput = String.format(createSeparatorPattern(5), iClassFixture, iMethodFixture, codeBefore, codeAfter, level);
		testable = new KoPeMeClassFileTransformaterData(fixtureInput);
	}
	
	@Test
	public void testSplitConstructor() throws Exception {
		assertFixtureValuesAreSavedProperly(testable);
	}

	private void assertFixtureValuesAreSavedProperly(KoPeMeClassFileTransformaterData testable) {
		assertEquals(iClassFixture, testable.getInstrumentableClass());
		assertEquals(iMethodFixture, testable.getInstrumentableMethod());
		assertEquals(codeBefore.trim(), testable.getCodeBefore());
		assertEquals(codeAfter.trim(), testable.getCodeAfter());
		assertEquals(level, testable.getLevel());
	}
	
	@Test
	public void toStringAndBack() throws Exception {
		KoPeMeClassFileTransformaterData testable = new KoPeMeClassFileTransformaterData(this.testable.toString());
		assertFixtureValuesAreSavedProperly(testable);
	}

	private String createSeparatorPattern(int times) {
		if(times < 1){
			throw new IllegalArgumentException("times < 1");
		}
		String sep = KoPeMeClassFileTransformaterData.DEFAULT_ARG_SEPARATOR;
		StringBuilder returnableBuilder = new StringBuilder();
		returnableBuilder.append(STRING_FORAMT_PATTERN);
		for(int i = 0; i < times - 1; i++){
			//returnableBuilder.append(" ");
			returnableBuilder.append(sep);
			returnableBuilder.append(STRING_FORAMT_PATTERN);
		}
		return returnableBuilder.toString();
	}
	
	
}
