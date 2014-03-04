package de.kopeme.testrunner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

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
	public static void main( String args[] )
	{
		if ( args.length == 0 )
		{
			System.out.println("Der PerformanceTestRunner muss mit einem Klassennamen als Parameter ausgeführt werden.");
			System.exit(1);
		}
		String klassenName = args[0];
		
		try {
			Class c = Class.forName(klassenName);
			runTestsWithClass(c);
		} catch (ClassNotFoundException e) {
			System.out.println("Die gewünschte Klasse " + klassenName + " wurde unglücklicherweise nicht gefunden.");
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
			System.out.println("Fehler");
			return;
		}
		boolean failed = false;
		List<AssertionError> errors = new LinkedList<AssertionError>();
		for ( Method method : c.getMethods() )
		{
			try{
//				System.out.println("Methode: " + method);
//				for ( Annotation a : method.getAnnotations())
//				{
//					System.out.println("Annotation: " + a);
//				}
				if ( method.isAnnotationPresent(PerformanceTest.class) && !method.isAnnotationPresent(ParallelPerformanceTest.class) )
				{
					TestExecution te = new TestExecution(c, instance, method);
					te.runTest();
				}
				if ( method.isAnnotationPresent(PerformanceTest.class) && method.isAnnotationPresent(ParallelPerformanceTest.class))
				{
					System.out.println("FÃ¼hre aus");
					ParallelTestExecution te = new ParallelTestExecution(c, instance, method);
					te.runTest();
				}
			}
			catch (AssertionError ae){
				failed = true;
				errors.add(ae);
			}
		}
//		if ( failed ) throw errors.get(0);
		if (failed){
			for (AssertionError ae : errors){
				System.out.println("Exception: " + ae.getLocalizedMessage());
				for (StackTraceElement ste: ae.getStackTrace()){
					System.out.println(ste);
				}
			}
		}
	}
}
