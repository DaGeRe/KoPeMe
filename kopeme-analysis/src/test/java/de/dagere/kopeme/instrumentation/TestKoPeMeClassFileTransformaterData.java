package de.dagere.kopeme.instrumentation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.dagere.kopeme.instrumentation.generic.KoPeMeClassFileTransformaterData;

public class TestKoPeMeClassFileTransformaterData {

	private static final String STRING_FORAMT_PATTERN = " %s";
	
	private String iClassFixture = "MyClass", 	
			   iMethodFixture = "MyMethod",
			   codeBefore = "before, before2; ", // the whitespaces are important here! ; and ;; need must be distinguishable!
			   codeAfter = "after; ",
			   varname1 = "i",
			   varname2 = "j",
			   typeOfVar = Integer.class.getName();
	
	private int level = 1;

	private VarDeclarationData dec1 = new VarDeclarationData(typeOfVar, varname1);
	private VarDeclarationData dec2 = new VarDeclarationData(typeOfVar, varname2);
	
	private KoPeMeClassFileTransformaterData createTestable() {
		return createTestable(new Object[]{
				iClassFixture, 
				iMethodFixture, 
				codeBefore, 
				codeAfter, 
				level, 
				dec1, 
				dec2
		});
	}
	
	
	private KoPeMeClassFileTransformaterData createTestable(Object[] vals ) {
		String fixtureInput = String.format(createSeparatorPattern(vals.length), vals);
		return new KoPeMeClassFileTransformaterData(fixtureInput);
	}
	@Test
	public void testWithoutVarDeclarations() throws Exception {
		KoPeMeClassFileTransformaterData testable = createTestable(new Object[]{
				iClassFixture, 
				iMethodFixture, 
				codeBefore, 
				codeAfter, 
				level});
		assertFixtureValuesAreSavedProperly(testable);
		assertEquals(0, testable.getDeclarations().size());
	}
	
	@Test
	public void testSplitConstructor() throws Exception {
		KoPeMeClassFileTransformaterData testable = createTestable();
		assertFixtureValuesAreSavedProperly(testable);
		assertDeclations(testable);
	}

	private void assertFixtureValuesAreSavedProperly(KoPeMeClassFileTransformaterData testable) {
		assertEquals(iClassFixture, testable.getInstrumentableClass());
		assertEquals(iMethodFixture, testable.getInstrumentableMethod());
		assertEquals(codeBefore.trim(), testable.getCodeBefore());
		assertEquals(codeAfter.trim(), testable.getCodeAfter());
		assertEquals(level, testable.getLevel());
	}


	private void assertDeclations(KoPeMeClassFileTransformaterData testable) {
		assertEquals(dec1, testable.getDeclarations().get(0));
		assertEquals(dec2, testable.getDeclarations().get(1));
	}
	
	@Test
	public void toStringAndBack() throws Exception {
		KoPeMeClassFileTransformaterData testable = new KoPeMeClassFileTransformaterData(createTestable().toString());
		assertFixtureValuesAreSavedProperly(testable);
		assertDeclations(testable);
	}

	private String createSeparatorPattern(int times) {
		if(times < 1){
			throw new IllegalArgumentException("times < 1");
		}
		String sep = KoPeMeClassFileTransformaterData.DEFAULT_ARG_SEPARATOR;
		StringBuilder returnableBuilder = new StringBuilder();
		returnableBuilder.append(STRING_FORAMT_PATTERN);
		for(int i = 0; i < times - 1; i++){
			returnableBuilder.append(sep);
			returnableBuilder.append(STRING_FORAMT_PATTERN);
		}
		return returnableBuilder.toString();
	}
	
	
}
