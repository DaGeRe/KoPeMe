package de.dagere.kopeme.junit.exampletests.runner;

import java.util.LinkedList;
import java.util.List;

import de.dagere.kopeme.junit3.KoPeMeTestcase;

public class GarbageCollectorOverheadTest extends KoPeMeTestcase {

	@Override
	protected int getWarmupExecutions() {
		return 2;
	}

	@Override
	protected int getExecutionTimes() {
		// TODO Auto-generated method stub
		return 10;
	}

	@Override
	protected boolean logFullData() {
		// TODO Auto-generated method stub
		return false;
	}

	public void testOverhead() {
		List<List> list = new LinkedList<>();
		while (true) {
			List<Integer> addList = new LinkedList<>();
			for (int i = 0; i < 500; i++)
				addList.add(i);
			list.add(addList);
			if (list.size() % 10000 == 0) {
				System.out.println("Größe: " + list.size() + " Speicher: " + Runtime.getRuntime().totalMemory() / 1024);
			}
		}

	}

}
