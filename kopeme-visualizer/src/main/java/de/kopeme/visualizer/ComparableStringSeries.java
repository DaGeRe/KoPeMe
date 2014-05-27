package de.kopeme.visualizer;

import org.jfree.data.ComparableObjectSeries;

public class ComparableStringSeries extends ComparableObjectSeries{

	public ComparableStringSeries(Comparable key) {
		super(key);
		// TODO Auto-generated constructor stub
	}
	
	public void add(Comparable x, Object o)
	{
		super.add(x, o);
	}

}
