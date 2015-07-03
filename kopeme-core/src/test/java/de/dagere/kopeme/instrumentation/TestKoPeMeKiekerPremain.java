package de.dagere.kopeme.instrumentation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;

import org.junit.Test;

public class TestKoPeMeKiekerPremain {

	
	@Test
	public void run() throws Exception {
		// TODO adjust for windows machines if needed
		String jarFileName = "kopeme-core-0.10-SNAPSHOT-agent.jar";
		String instractableMethod = "a";
		String agentInputArgs = new KoPeMeClassFileTransformaterDataForKieker(TestDataSingleton.Transformable.class.getName(), instractableMethod, 1).getCommand();
		ProcessBuilder pb = new ProcessBuilder("java", "-cp", System.getProperty("java.class.path"),  
												String.format("-javaagent:target/%s=%s", jarFileName, agentInputArgs),
												TestJavassistPremain.class.getName());
		pb.redirectError(Redirect.INHERIT);
		pb.redirectOutput(Redirect.INHERIT);
		Process start = pb.start();
		int ret = start.waitFor();
		assertEquals("the return value of the forked vm was not null", 0, ret);
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		new TestDataSingleton.Transformable().a();
	}
}
