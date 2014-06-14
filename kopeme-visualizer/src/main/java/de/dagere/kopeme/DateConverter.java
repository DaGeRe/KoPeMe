package de.dagere.kopeme;

import java.util.Comparator;
import java.util.Date;

class StringDatePair implements Comparable<StringDatePair>{

	private String s;
	private Date d;
	
	public StringDatePair(String s, Date d)
	{
		this.s = s;
		this.d = d;
	}
	
	public String getString(){
		return s;
	}
	
	public int compareTo(StringDatePair o) {
		// TODO Auto-generated method stub
		return d.compareTo(o.d);
	}
	
}

public interface DateConverter{
	/**
	 * Calculates the revision for the given date; this may be a git-tag, therefore
	 * the returnvalue can not be only an int
	 * @param d
	 * @return
	 */
	public StringDatePair getDisplayStringOfDate(Date d);
	
}
