package de.dagere.kopeme.junit.tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests whether Kieker data are written
 * Does not work at release, because modules are not in repo during release -> Should be transformed to integration test
 * @author reichelt
 *
 */
@Ignore
public class TestKieker {

	protected static final String KIEKER_ARG_LINE = "-javaagent:" + System.getProperty("user.home") + "/.m2/repository/net/kieker-monitoring/kieker/1.13/kieker-1.13-aspectj.jar";

	@Test
	public void testDataCreation() throws IOException, InterruptedException {
		final File tempFolder = Files.createTempDirectory("kopeme-test").toFile();
		final String arglineString = "-DargLine=" + KIEKER_ARG_LINE;
		final ProcessBuilder builder = new ProcessBuilder("mvn", "surefire:test", "-Dtest=ExampleKiekerUsageTest", arglineString);
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
			// skip metadata
			reader.readLine();
			reader.readLine();
			int count = 0;
			while ((line = reader.readLine()) != null) {
				Assert.assertTrue(line.contains("de.dagere.kopeme.junit"));
				count++;
			}
			Assert.assertEquals(1608, count);
		}
	}
}
