package de.dagere.kopeme.instrumentation;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.lang.ProcessBuilder.Redirect;

import kieker.monitoring.writer.filesystem.ChangeableFolderSyncFsWriter;

import org.junit.Test;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.datastorage.FolderProvider;

public class TestKoPeMeKiekerPremain {
	
	@Test
	public void run() throws Exception {
		// TODO adjust for windows machines if needed
		String jarFileName = "kopeme-core-0.10-SNAPSHOT-agent.jar";
		String instractableMethod = "a";
		String agentInputArgs = new KoPeMeClassFileTransformaterDataForKieker(TestDataSingleton.Transformable.class.getName(), instractableMethod, 1).getCommand();
		ProcessBuilder pb = new ProcessBuilder("java", //"-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=1044",
												"-cp", System.getProperty("java.class.path"),  
												String.format("-javaagent:target/%s=%s", jarFileName, agentInputArgs),
												TestKoPeMeKiekerPremain.class.getName());
		pb.redirectError(Redirect.INHERIT);
		pb.redirectOutput(Redirect.INHERIT);
		Process start = pb.start();
		int ret = start.waitFor();
		assertEquals("the return value of the forked vm was not null", 0, ret);
	}
	
	public static void main(String[] args) throws Exception {
		TestUtils.cleanAndSetKoPeMeOutputFolder();
		File folder = new File(FolderProvider.getInstance().getFolderFor(TestKiekerMeasureUtil.class.getName()));
		System.out.println(folder);
		ChangeableFolderSyncFsWriter inst = ChangeableFolderSyncFsWriter.getInstance(KiekerMeasureUtil.CTRLINST);
		inst.setFolder(folder);
		new TestDataSingleton.Transformable().a();
	}
}
