package de.dagere.kopeme.junit.exampletests.runner;

import de.dagere.kopeme.datacollection.DataCollectorList;
import de.dagere.kopeme.junit.exampletests.time.AddRandomNumbers;
import de.dagere.kopeme.junit3.KoPeMeTestcase;

public class TimeRunnerExample extends KoPeMeTestcase {

	@Override
	protected boolean logFullData() {
		return true;
	}
	
	@Override
	protected int getWarmupExecutions() {
		return 0;
	}
	
	@Override
	protected int getExecutionTimes() {
		return 18000;
	}
	
	@Override
	protected DataCollectorList getDataCollectors() {
		return DataCollectorList.ONLYTIME;
	}
	
	@Override
	protected int getRepetitions() {
		return 200;
	}
	
	public void testMe() {
		final AddRandomNumbers rm = new AddRandomNumbers();
		for (int i = 0; i < 10; i++) {
			rm.addSomething();
		}
		System.out.println(rm.getValue());
	}
	
}
