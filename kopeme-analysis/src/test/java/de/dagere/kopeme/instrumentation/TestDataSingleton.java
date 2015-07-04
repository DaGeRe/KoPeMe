package de.dagere.kopeme.instrumentation;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static de.dagere.kopeme.instrumentation.KiekerMeasureUtil.*;

public enum TestDataSingleton implements Serializable {
	INSTANCE;
	
	public static abstract class TestJoinPointData implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String containingClass;
		private String containingMethod;
		private int lineNumber;
		
		public TestJoinPointData() {
			StackTraceElement currentStack = lineOut(5);
		    this.containingClass = currentStack.getClassName();
		    containingMethod = currentStack.getMethodName();
		    lineNumber = currentStack.getLineNumber();
		}

		public String getContainingClass() {
			return containingClass;
		}

		public String getContainingMethod() {
			return containingMethod;
		}

		public int getLineNumber() {
			return lineNumber;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((containingClass == null) ? 0 : containingClass
							.hashCode());
			result = prime
					* result
					+ ((containingMethod == null) ? 0 : containingMethod
							.hashCode());
			result = prime * result + lineNumber;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestJoinPointData other = (TestJoinPointData) obj;
			if (containingClass == null) {
				if (other.containingClass != null)
					return false;
			} else if (!containingClass.equals(other.containingClass))
				return false;
			if (containingMethod == null) {
				if (other.containingMethod != null)
					return false;
			} else if (!containingMethod.equals(other.containingMethod))
				return false;
			if (lineNumber != other.lineNumber)
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return String.format("TestJoinPointData [containingClass=%s, containingMethod=%s, lineNumber=%s]",
								 containingClass, containingMethod, lineNumber);
		}
	
		
	}
	
	public static class BeginTestJoinPointData extends TestJoinPointData {}
	
	public static class AfterTestJoinPointData extends TestJoinPointData {}
	
	public static class Transformable implements Runnable {
		
		@Override
		public void run() {
			System.out.println("run");
			a();
		}

		public void a() {
			System.out.println("a");
			int result = (int) Math.pow(b(), 2);
			System.out.println(result);
		}
		
		private int b(){
			System.out.println("b");
			c();
			return 5;
		}
		
		protected void c(){
			System.out.println("c");
		}
		
	}
	
	private Set<TestJoinPointData> joinPointData = new HashSet<>();
	
	public void add(TestJoinPointData addable){
		joinPointData.add(addable);
	}

	public Collection<TestJoinPointData> getJoinPointData() {
		return joinPointData;
	}
	
	public void clear() {
		joinPointData.clear();
	}
}