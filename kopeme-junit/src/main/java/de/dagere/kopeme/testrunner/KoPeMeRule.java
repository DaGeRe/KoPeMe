package de.dagere.kopeme.testrunner;

import java.lang.reflect.Method;

import org.junit.rules.MethodRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import de.dagere.kopeme.PerformanceTestRunner;
import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.paralleltests.ParallelPerformanceTest;
import de.dagere.kopeme.paralleltests.ParallelTestExecution;

/**
 * This Rule gives the possibility to test performance with a rule and without a
 * testrunner; this makes it possible to use a different testrunner
 * @author DaGeRe
 *
 */
public class KoPeMeRule implements TestRule{
	
	class PerformanceStatement extends Statement{
		private Statement oldStatement;
		
		public PerformanceStatement(Statement stmt, Class clazz, Method method){
			this.oldStatement = stmt;
		}
		@Override
		public void evaluate() throws Throwable {
			PerformanceTestRunner te;
		}
		
	}
	
	@Override
	public Statement apply(final Statement stmt, Description descr) {
		if (descr.isTest()){
			Method m = null;
			Class<?> clazz = null;
			try {
				clazz = Class.forName(descr.getClassName());
				m = clazz.getMethod(descr.getMethodName());
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						stmt.evaluate();
					} catch (Throwable e) {
						// TODO Automatisch generierter Erfassungsblock
						e.printStackTrace();
					}
				}
			};
			return new ParameterlessTestExecution(r, m, clazz.getName()+".yaml");
		}
		else{
			return stmt;
		}
		
	}

}
