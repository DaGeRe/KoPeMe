package de.kopeme.caller;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.tools.ant.types.Resource;


public class KoPeMeAntTask extends Task {

	private List<FileSet> filesets = new LinkedList<FileSet>();
	private List<Path> classpath = new LinkedList<Path>();
	private String classpathstring;
	private boolean compile;
	private String compileFolder;
	
	public void setCompileFolder(String compileFolder) {
		this.compileFolder = compileFolder;
	}

	public void setCompile(boolean compile)
	{
		this.compile = compile;
	}
	
	public void addClasspath(Path fileset) {
		classpath.add(fileset);
	}
	
	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}
	
	public void execute() {
//		System.out.println("Hello World");
		classpathstring = "";
		for ( Path f : classpath )
		{
			classpathstring+=f.toString();
		}
		for (FileSet f : filesets) {
//			System.out.println("Dir: " + f.getDir());
			Resource r = null;
			for (Iterator<Resource> i = f.iterator(); i.hasNext();) {
				r = i.next();
//				System.out.println("R: " + r + " " + r.getName());
				ExternalKoPeMeRunner ekr = new ExternalKoPeMeRunner(r.getName(), f.getDir().getName(), classpathstring);
				ekr.setCompile(true);
				ekr.run();
			}
		}
	}

}
