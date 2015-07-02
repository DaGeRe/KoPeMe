package de.dagere.kopeme.instrumentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.Loader;
import javassist.NotFoundException;

import org.junit.Before;
import org.junit.Test;

import de.dagere.kopeme.instrumentation.TestDataSingleton.AfterTestJoinPointData;
import de.dagere.kopeme.instrumentation.TestDataSingleton.BeginTestJoinPointData;
import de.dagere.kopeme.instrumentation.TestDataSingleton.TestJoinPointData;
import de.dagere.kopeme.instrumentation.TestDataSingleton.Transformable;

public class TestKoPeMeClassFileTransformator {

	static final String FIXTURE_BEFORE = "System.out.println(\"davor\");"+ TestDataSingleton.class.getName() + ".INSTANCE.add(new " +  BeginTestJoinPointData.class.getName() +  "());";
	static final String FIXTURE_AFTER = "System.out.println(\"dannach\");"+ TestDataSingleton.class.getName() + ".INSTANCE.add(new " +  AfterTestJoinPointData.class.getName() +  "());";
	
	private static ClassPool pool;
	private static Loader loader;
	
	static {
		pool = ClassPool.getDefault();
		loader = new Loader(pool);
		Thread.currentThread().setContextClassLoader(loader);
	}
	
	private KoPeMeClassFileTransformater testable;
	
	@Before
	public void setup() throws NotFoundException{
		KoPeMeClassFileTransformaterData fixture = new KoPeMeClassFileTransformaterData(Transformable.class.getName(), "a", FIXTURE_BEFORE, FIXTURE_AFTER, 3);
		testable = new KoPeMeClassFileTransformater(fixture);
	}
	
	@Test
	public void testInstrumentation() throws Exception {
		// A java class is addressed by its name (fully qualified package.Classname) AND ITS CLASSLOADER!
		// There is no built in way to reload a class by a classloader (e.g. after we changed it by javassist). 
		// We need to create a new classloader and load the class. 
		// This classloader should also load the class by itself, not passing the loading task to its parent classloader.
		// Javassist provides a classloader (Loader) doing just that.
		
		// I mention this, because we need to use reflection after our TestDataSingleton is reloaded, 
		// the typesafe way points unfortunatly to the old non changed version of our reloaded class.
		
		CtClass ctClass = pool.get(Transformable.class.getName());
		byte[] resultingClassByteCode = testable.transform(getClass().getClassLoader(), Transformable.class.getName().replaceAll("\\.", "/"), Transformable.class, null, ctClass.toBytecode());
		assertNotNull(resultingClassByteCode); // just checking that this is not null, the javassist framework had already reloaded the class so we don't need to use the byte array for further steps
		
		// now the class has been changed, we can test the outcome
		Class<?> klass = loader.loadClass(Transformable.class.getName());
		Runnable r = (Runnable) klass.newInstance();
        r.run(); // run our test instance! this should trigger the datasets to be inserted in the changed singleton class
        
        // this is now the ugly part, which was mentioned by the starting comment
        // we can't write TestDataSingleton r = (TestDataSingleton) Enum.valueOf(enumClass, "INSTANCE")
        // because javassist also replaces the class definitions for the classes used in the injection process
        // this wont be a bigger deal when using the javaagent interface, as this aspects classes to be changed
        // therefore this reflection code IS ONLY NEEDED IN THE TESTs
        Class<? extends Enum> enumClass = (Class<? extends Enum>) loader.loadClass(TestDataSingleton.class.getName());
		Enum instance = Enum.valueOf(enumClass, "INSTANCE");
		Collection<TestJoinPointData> result = (Collection<TestJoinPointData>) enumClass.getMethod("getJoinPointData").invoke(instance);
		assertEquals(0, TestDataSingleton.INSTANCE.getJoinPointData().size()); // we have two singletons now :D --> this is due to our two classloaders
        assertInjectionWorked(6, result);
	}

	private void assertInjectionWorked(int size, Collection<TestJoinPointData> result) throws ClassNotFoundException {
		assertEquals(size, result.size());
		for(Object  d : result){
			System.out.println(d); // just checking that the output makes sense
		}
	}

}
