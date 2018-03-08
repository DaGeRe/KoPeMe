package de.dagere.kopeme.junit3.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestKieker3 extends TestCase {

	protected static final String KIEKER_ARG_LINE = "-javaagent:" + System.getProperty("user.home") + "/.m2/repository/net/kieker-monitoring/kieker/1.12/kieker-1.12-aspectj.jar";

	public void testDataCreation() throws IOException, InterruptedException {
		// TODO Integration test -> See TestKieker in kopeme-junit
		if (true) { // Since JUnit 3 does not have ignore..
			return;
		}

		final File tempFolder = Files.createTempDirectory("kopeme3-test").toFile();
		final String arglineString = "-DargLine=" + KIEKER_ARG_LINE;
		final ProcessBuilder builder = new ProcessBuilder("mvn", "surefire:test", "-Dtest=KiekerTest", arglineString);
		builder.environment().put("KOPEME_HOME", tempFolder.getAbsolutePath());

		final Process process = builder.start();
		String line;
		while ((line = new BufferedReader(new InputStreamReader(process.getInputStream())).readLine()) != null) {
			System.out.println(line);
		}

		final int result = process.waitFor();

		Assert.assertEquals(0, result);

		final File projectFolder = tempFolder.listFiles()[0].listFiles()[0].listFiles()[0];
		Assert.assertTrue(projectFolder.isDirectory());
		final File executionFolder = projectFolder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().matches("[0-9]+");
			}
		})[0];
		Assert.assertTrue(executionFolder.exists());
		final File kiekerFolder = executionFolder.listFiles()[0].listFiles()[0];
		Assert.assertTrue(kiekerFolder.isDirectory());
		final File kiekerFile = kiekerFolder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".dat");
			}
		})[0];
		Assert.assertTrue(kiekerFile.exists());

		try (BufferedReader reader = new BufferedReader(new FileReader(kiekerFile))) {
			int count = 0;
			// Skip Metadata Records
			reader.readLine();
			reader.readLine();
			while ((line = reader.readLine()) != null) {
				Assert.assertTrue(line.contains("de.dagere.kopeme.junit"));
				count++;
			}
			Assert.assertEquals(36, count);
		}
	}
}
