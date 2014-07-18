package de.dagere.kopeme.visualizer.data;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Saves a testcase, e.g. the name of a file, that should be visualized
 * @author dagere
 *
 */
public class Testcase {
	
	public String name;
	
	public Testcase()
	{
		name = "";
	}
	
	@DataBoundConstructor
	public Testcase(String name)
	{
		this.name = name;
	}
	
	public void setName( String name )
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
}
