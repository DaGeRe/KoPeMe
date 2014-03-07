package de.kopeme.caller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * Runs the KoPeMe-Testrunner on the commandline. The runs can be parameterized by
 * several options.
 * 
 * @author dagere
 * 
 */
public class ExternalKoPeMeRunner {
	
//	private static Logger log = LogMana
	
	private String fileName;
	private String sourceDirName;
	private boolean compile;
	private String compileFolder;
	private String libraryPath;
	private String classpath;
	private String externalOutputFile;
	
	/**
	 * Initiates an ExternalKoPeMeRunner with the given parameters - primarily for Maven-use
	 * @param fileName		name of the class, that should be run (e.g. de.tests.testA)
	 * @param compileFolder	where the compiled classes are
	 * @param classpath		classpath of the files
	 * @param libraryPath	path of the library (mostly where the .so files of sigar are)
	 */
	public ExternalKoPeMeRunner(String fileName, String compileFolder,
			String classpath, String libraryPath) {
		this.fileName = fileName;
		this.compileFolder = compileFolder;
		this.classpath = classpath;

		this.libraryPath = libraryPath;
		externalOutputFile = "";

		this.sourceDirName = "";
		this.compile = false;
	}

	/**
	 * Initiates an ExternalKoPeMeRunner with the given parameters - primarily for Ant-use
	 * 
	 * @param fileName
	 *            name of the class, that should be run (e.g. de.tests.testA)
	 * @param sourceDirName
	 *            name of the directory where the things should be compiled from,
	 *            if they should be compiled
	 * @param classpath
	 *            classpath of calls to java and javac
	 */
	public ExternalKoPeMeRunner(String fileName, String compileFolder, String classpath) {
		this.fileName = fileName;
		this.compileFolder = compileFolder;
		this.classpath = classpath;

		libraryPath = "";
		externalOutputFile = "";
		
		this.sourceDirName = "";
		this.compile = false;
	}

	/**
	 * Sets the file, where the standardoutput should go to
	 * 
	 * @param externalOutputFile
	 */
	public void setExternalOutputFile(String externalOutputFile) {
		this.externalOutputFile = externalOutputFile;
	}
	
	/**
	 * Sets weather the files should be compiled - for ant use
	 * @param compile
	 * @param compilePath
	 */
	public void setCompile(boolean compile)
	{
//		this.sourceDirName = compilePath;
		this.compile = compile;
	}

	/**
	 * Runs KoPeMe, and returns 0, if everything works allright
	 * 
	 * @return 0, if everything works allright
	 */
	public int run() {
		try {
			
			if ( compile )
			{
				compile();
			}
			
			
			String separator = "/";
			String cpseperator = ":";
			if (System.getProperty("os.name").contains("indows")){
				separator = "\\";
				cpseperator = ";";
			}
			String s = fileName.replace(separator, ".");
			if ( ".java".equals(s.substring(s.length()-5)))
			{
				s = s.substring(0, s.length()-5); // .java Entfernen
			}
			else
			{
				s = s.substring(0, s.length() - 6); // .class entfernen
			}
			
			
			String localClasspath = classpath;
			if (compileFolder != null)
				localClasspath = localClasspath + cpseperator + compileFolder;
			String command = "java -cp "+localClasspath;
			if (libraryPath != null && libraryPath!= "")
				command += "-Djava.library.path="+libraryPath;
			command = command + " de.kopeme.testrunner.PerformanceTestRunner " + s;
			
//			System.out.println(command);
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			
			BufferedWriter bw = null;
//			System.out.println("ExternalOutputFile: " + externalOutputFile);
			if ( externalOutputFile != null && externalOutputFile != "")
			{
				File output = new File(externalOutputFile);
				try {
					bw = new BufferedWriter( new FileWriter(output));
				} catch (IOException e1) {
					// TODO Automatisch generierter Erfassungsblock
					e1.printStackTrace();
				}
			}
			
			
			while ( (line = br.readLine()) != null )
			{
				if ( bw == null)
				{
					System.out.println(line);
				}
				else
				{
					bw.write(line+"\n");
				}
			}
			
			br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ( (line = br.readLine()) != null )
			{
				if ( bw == null)
				{
					System.out.println(line);
				}
				else
				{
					bw.write(line+"\n");
				}
			}
			if ( bw != null )
				bw.close();
			int returnValue = p.waitFor();
//			System.out.println("Returnvalue: " + returnValue);
			return returnValue;
		} catch (IOException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
		return 1;
	}

	private void compile() throws InterruptedException, IOException {
		String command = "javac -cp "+classpath+" " +
//				"-sourcepath "+sourceDirName+ " " +
				"-d " + (compileFolder != null ? compileFolder : ".") + " " +
				compileFolder +"/" +fileName;
//		System.out.println(command);
		Runtime.getRuntime().exec(command).waitFor();
	}
}
