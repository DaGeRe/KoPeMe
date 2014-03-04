package de.kopeme.mojo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.commons.io.filefilter.*;
//import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
//import org.apache.maven.plugin.annotations.Parameter;
//import org.apache.maven.plugin.annotations.*;
import org.apache.maven.execution.MavenSession;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import de.kopeme.caller.ExternalKoPeMeRunner;

/**
 * A Mojo for executing standalone-performance-tests with maven
 * 
 * @goal performancetest
 * 
 * @phase test
 * @requiresDependencyResolution compile
 */
public class KoPeMeMojo extends AbstractMojo {

	private static String CPPATHSEPERATOR = ":";
	private static String PATHSEPERATOR = "/";

	/**
	 * The current Maven session.
	 * 
	 * @parameter property="session"
	 * @required
	 * @readonly
	 */
	private MavenSession mavenSession;

	/**
	 * The Maven BuildPluginManager component.
	 * 
	 * @component
	 * @required
	 */
	private BuildPluginManager pluginManager;

	/**
	 * The maven project.
	 * 
	 * @parameter property="project"
	 * @required
	 */
	protected MavenProject project;

	/**
	 * Where the standard-output of the runs should be saved
	 * 
	 * @parameter property="standardoutput"
	 */
	protected String standardoutput;
	
	/**
	 * Writer for the standardoutput
	 */
	private BufferedWriter bw;
	
	/**
	 * Saving data about the tests: how many where run, how many failures and
	 * how many errors occured
	 */
	private int tests, failure, error;

	/**
	 * Returns a list with all files in the given Directory, matching the given
	 * Filter
	 * 
	 * @param dir
	 *            Directory, where the files should be
	 * @param filter
	 *            Filter, that the Files should match
	 * @return list with all files in the given Directory, matching the given
	 *         Filter
	 */
	public List<File> getFiles(File dir, FileFilter filter) {
//		System.out.println("Standardoutput: " + standardoutput);
		List<File> files = new LinkedList<File>();
		for (File f : dir.listFiles()) {
			if (f.isFile()) {
				if (filter.accept(f))
					files.add(f);
			} else {
				files.addAll(getFiles(f, filter));
			}
		}

		return files;
	}

	/**
	 * Main-method, that is executed when the tests are executed
	 */
	public void execute() throws MojoExecutionException {
		
		
		getLog().info("Start, BS: " + System.getProperty("os.name"));
		if (System.getProperty("os.name").contains("indows")){
			CPPATHSEPERATOR = ";";
			PATHSEPERATOR="\\";
		}else{
			CPPATHSEPERATOR=":";
			PATHSEPERATOR="/";
		}
		System.out.println("Execute: " + standardoutput);
		tests = 0;
		failure = 0;
		error = 0;
		
		if (standardoutput != null){
			File output = new File(standardoutput);
			try {
				bw = new BufferedWriter( new FileWriter(output));
			} catch (IOException e1) {
				// TODO Automatisch generierter Erfassungsblock
				e1.printStackTrace();
			}
		}
		else{
			File output = new File("stdout.txt");
			try {
				bw = new BufferedWriter( new FileWriter(output));
			} catch (IOException e1) {
				// TODO Automatisch generierter Erfassungsblock
				e1.printStackTrace();
			}
		}
		
		File dir = new File("src/test/java/");
		FileFilter fileFilter = new RegexFileFilter("[A-z/]*.java$");

		project = project.getExecutionProject();
		runTestFile(dir, fileFilter);

		getLog().info("Tests: " + tests + " Fehlschläge: " + failure
				+ " Fehler: " + error);

	}

	private void runTestFile(File dir, FileFilter fileFilter) {
		synchronized (KoPeMeMojo.class) {
			List<File> files = getFiles(dir, fileFilter);
			String classpath = "";
			try {
				List<String> classpathElements = project
						.getCompileClasspathElements();
				for (String s : classpathElements) {
					classpath += s + CPPATHSEPERATOR;
				}
				classpath = classpath.substring(0, classpath.length() - 1);

			} catch (DependencyResolutionRequiredException e) {
				getLog().info(e.toString());
			}

			getLog().info("Dateien: " + files.size() + " Filter: " + fileFilter.toString() + " Verzeichnis: " + dir.getAbsolutePath());
			
			for (File f : files) {
				String fileName = f.getPath().replace("/", ".");
				fileName = fileName.substring(fileName.indexOf("java") + 5); // test/java/ entfernen
				System.out.println("Nach Substr: " + fileName);
				int returnValue;
				try{
					ExternalKoPeMeRunner ekr;// = new ExternalKoPeMeRunner(fileName, "target/test-classes/", "", classpath+":target/test-classes/");
					ekr = new ExternalKoPeMeRunner(fileName, "target"+PATHSEPERATOR + "test-classes"+PATHSEPERATOR, classpath);
					ekr.setExternalOutputFile(standardoutput);
					returnValue = ekr.run();
				}catch (Exception e){
					e.printStackTrace();
					returnValue = 1;
				}
				
				if (returnValue != 0)
				{
					failure++;
					getLog().info("Test fehlgeschlagen");
				}
//				executeTest(f, classpath);
				tests++;
			}
		}
	}

	/**
	 * Executes a performance-test for a given file with the given
	 * classpathstring; the files have to be compiled before (e.g. via goal
	 * testCompile)
	 * 
	 * @param file
	 *            The File of the performancetest, that should be executed
	 * @param classpathstring
	 *            The classpath, with which the file should be executed
	 */
//	private void executeTest(File file, String classpathstring) {
//		tests++;
//		try {
//
//			String s = file.getPath().replace("/", ".");
//			s = s.substring(0, s.length() - 5); // .java Entfernen
//			s = s.substring(s.indexOf("java") + 5); // test/java/ entfernen
//			String compileFolder = "target/test-classes/";
//			String localClasspath = classpathstring;
//			if (compileFolder != null)
//				localClasspath = localClasspath + ":" + compileFolder;
//			getLog().info("Führe Test aus: " + s);
//			String command = "java -Djava.library.path=target/lib/ -cp "
//					+ localClasspath
//					+ ":Tests/ de.kopeme.testrunner.PerformanceTestRunner " + s;
//			System.out.println("Command: " + command);
//			Process p = Runtime.getRuntime().exec(command);
//
//			if ( bw != null )
//			{
//				BufferedReader in = new BufferedReader(new InputStreamReader(
//						p.getInputStream()));
//				String line;
//				
//				while ((line = in.readLine()) != null) {
//					bw.write(line + "\n");
//				}
//				bw.flush();
//			}
//			
//
//			int returnValue = p.waitFor();
//			if (returnValue != 0)
//			{
//				failure++;
//				getLog().info("Test fehlgeschlagen");
//			}
//		} catch (IOException e) {
//			error++;
//			// TODO Automatisch generierter Erfassungsblock
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			error++;
//			// TODO Automatisch generierter Erfassungsblock
//			e.printStackTrace();
//		}
//	}
}
