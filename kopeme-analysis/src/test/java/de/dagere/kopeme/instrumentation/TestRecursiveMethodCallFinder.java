package de.dagere.kopeme.instrumentation;

import static org.junit.Assert.*;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import org.junit.Before;
import org.junit.Test;

public class TestRecursiveMethodCallFinder {

	public class RecursiveTestClass {
		public void a(){
			b();
			b(); // multiple calls should be ignored
			System.out.println("test other statement");
		}
		
		void b(){
			c();
		}
		
		private void c(){
			fac(5);
		}
		
		protected int fac(int n){
			if(n == 0)
				return 1;
			else 
				return fac(n - 1) * n;
		}
	}
	
	private ClassPool fixturePool;
	private CtClass fixtureClass;
	private CtMethod fixtureMethod;
	private RecursiveMethodCallFinder testable;

	@Before
	public void setup() throws NotFoundException{
		fixturePool = ClassPool.getDefault();
		fixtureClass = fixturePool.get(RecursiveTestClass.class.getName());
		fixtureMethod = fixtureClass.getDeclaredMethod("a");
		testable = new RecursiveMethodCallFinder();
	}
	
	@Test
	public void testFindLevel0() throws Exception {
		Map<CtClass, Set<CtMethod>> result = testable.find(fixtureMethod, 0);
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(fixtureMethod,result.get(fixtureClass).iterator().next());
	}
	
	@Test
	public void testFindLevel1() throws Exception {
		Map<CtClass, Set<CtMethod>> result = testable.find(fixtureMethod, 1);
		assertNotNull(result);
		assertEquals(2, result.size());
		Set<CtMethod> set = result.get(fixtureClass);
		assertEquals(2, set.size());
		Iterator<CtMethod> iterator = set.iterator();
		assertEquals(fixtureMethod, iterator.next());
		assertEquals(fixtureClass.getDeclaredMethod("b"),iterator.next());
		CtClass ps = fixturePool.get(PrintStream.class.getName());
		assertEquals(ps.getDeclaredMethod("println", new CtClass[]{fixturePool.get(String.class.getName())}),result.get(ps).iterator().next());
	}

	
	@Test
	public void testFindLevel2() throws Exception {
		Map<CtClass, Set<CtMethod>> result = testable.find(fixtureMethod, 2);
		assertNotNull(result);
		assertEquals(2, result.size());
		Set<CtMethod> set = result.get(fixtureClass);
		assertEquals(3, set.size());
		Iterator<CtMethod> iterator = set.iterator();
		assertEquals(fixtureMethod, iterator.next());
		assertEquals(fixtureClass.getDeclaredMethod("b"),iterator.next());
		assertEquals(fixtureClass.getDeclaredMethod("c"),iterator.next());
		CtClass ps = fixturePool.get(PrintStream.class.getName());
		Set<CtMethod> printStreamCalledMethods = result.get(ps);
		assertEquals(3, printStreamCalledMethods.size());
		Iterator<CtMethod> psIt = printStreamCalledMethods.iterator();
		assertEquals(ps.getDeclaredMethod("println", new CtClass[]{fixturePool.get(String.class.getName())}),psIt.next());
		assertEquals(ps.getDeclaredMethod("print", new CtClass[]{fixturePool.get(String.class.getName())}),psIt.next());
		assertEquals(ps.getDeclaredMethod("newLine", new CtClass[]{}), psIt.next());
	}
	
	@Test
	public void testCanHandleNativeMethods() throws Exception {
		CtClass fixture = fixturePool.get(StrictMath.class.getName());
		CtMethod declaredMethod = fixture.getDeclaredMethod("sqrt");
		Map<CtClass, Set<CtMethod>> result = testable.find(declaredMethod, 2);
		assertEquals(1, result.size());
		Set<CtMethod> value = result.get(fixture);
		assertEquals(1, value.size());
		assertEquals(declaredMethod, value.iterator().next());
		printResult(result);
	}
	
	private void printResult(Map<CtClass, Set<CtMethod>> result) {
		for(Entry<CtClass, Set<CtMethod>> s : result.entrySet()){
			System.out.println(s.getKey().getSimpleName());
			for(CtMethod m : s.getValue()){
				System.out.println("---" + m.getLongName());
			}
		}
	}
	
	@Test
	public void testHandleBigLevelsWhenAstIsNotSoDeep() throws Exception {
		CtMethod b = fixtureClass.getDeclaredMethod("b");
		Map<CtClass, Set<CtMethod>> result = testable.find(b, 100);
		assertNotNull(result);
		assertEquals(1, result.size());
		Set<CtMethod> set = result.get(fixtureClass);
		assertEquals(3, set.size());
		Iterator<CtMethod> iterator = set.iterator();
		assertEquals(b, iterator.next());
		assertEquals(fixtureClass.getDeclaredMethod("c"),iterator.next());
		assertEquals(fixtureClass.getDeclaredMethod("fac"),iterator.next());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLevelLowerZero() {
		testable.find(fixtureMethod, Integer.MIN_VALUE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLevelLowerZero2() {
		testable.find(fixtureMethod, -1);
	}
	
}
