package de.kopeme.testrunner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.kopeme.PerformanceTest;
import de.kopeme.TestExecution;
import de.kopeme.datacollection.TimeDataCollector;
import de.kopeme.paralleltests.ParallelPerformanceTest;
import de.kopeme.paralleltests.ParallelTestExecution;

/**
 * Runs a performance test via the pure test Runner, which does not need any additional librarys.
 * @author dagere
 *
 */
public class PerformanceTestRunner {
	
	private static Logger log = LogManager.getFormatterLogger(PerformanceTestRunner.class);
	
	public static void main( String args[] )
	{
		if ( args.length == 0 )
		{
			log.error("Der PerformanceTestRunner muss mit einem Klassennamen als Parameter ausgef�hrt werden.");
			System.exit(1);
		}
		String klassenName = args[0];
		
		try {
			Class c = Class.forName(klassenName);
			runTestsWithClass(c);
		} catch (ClassNotFoundException e) {
			log.error("Die gewünschte Klasse " + klassenName + " wurde unglücklicherweise nicht gefunden.");
//			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void runTestsWithClass( Class c )
	{
		Object instance = null;
		try {
			instance = c.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if ( instance == null )
		{
			log.error("Klasseninstanziierung nicht möglich");
			return;
		}
		boolean failed = false;
		List<AssertionError> errors = new LinkedList<AssertionError>();
		for ( Method method : c.getMethods() )
		{
			try{
				if ( method.isAnnotationPresent(PerformanceTest.class) && !method.isAnnotationPresent(ParallelPerformanceTest.class) )
				{
					TestExecution te = new TestExecution(c, instance, method);
					te.runTest();
				}
				if ( method.isAnnotationPresent(PerformanceTest.class) && method.isAnnotationPresent(ParallelPerformanceTest.class))
				{
					ParallelTestExecution te = new ParallelTestExecution(c, instance, method);
					te.runTest();
				}
			}
			catch (AssertionError ae){
				failed = true;
				errors.add(ae);
			}
		}
		if (failed){
			for (AssertionError ae : errors){
				log.error("Exception: " + ae.getLocalizedMessage());
				ae.printStackTrace();
			}
		}
	}
}
