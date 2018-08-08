package de.dagere.kopeme.junit.exampletests.time;

import de.dagere.kopeme.junit3.KoPeMeTestcase;

public class KiekerTest extends KoPeMeTestcase{
	
	@Override
	protected int getExecutionTimes() {
		return 2;
	}
	
	@Override
	protected boolean useKieker() {
		return true;
	}
	
	public void testMe(){
		stage1_1();
		stage1_2();
		stage1_3();
	}
	
	public void testMeAlso(){
      stage1_1();
      stage1_2();
   }
	
	public void stage1_1(){
		System.out.println("Test 1 1");
	}
	
	public void stage1_2(){
		System.out.println("Test 1 2");
		stage2_1();
	}
	
	public void stage1_3(){
		System.out.println("Test 1 3");
	}
	
	public void stage2_1(){
		System.out.println("Test 2 1");
		
		stage3_1();
	}
	
	public void stage3_1(){
		System.out.println("Test 3 1");
	}
}
