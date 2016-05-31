package de.dagere.kopeme.instrumentation;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.util.Collection;

import org.junit.Test;

import de.dagere.kopeme.instrumentation.TestDataSingleton.TestJoinPointData;

public class TestJavassistPremain {

	private static final String PICKLED_PATH = "target/test-classes/javassist-premain-tds.bin";

	@Test
	public void run() throws Exception {
		// TODO adjust for windows machines if needed
		String jarFileName = TestKoPeMeKiekerPremain.getFileInTarget("-javassist_agent.jar");
		String instractableMethod = "b";
		String agentInputArgs = TestKoPeMeClassFileTransformator.createTestData(instractableMethod, 3).toString();
		ProcessBuilder pb = new ProcessBuilder("java", "-cp", System.getProperty("java.class.path"),  
												String.format("-javaagent:target/%s=%s", jarFileName, agentInputArgs),
												TestJavassistPremain.class.getName());
		pb.redirectError(Redirect.INHERIT);
		pb.redirectOutput(Redirect.INHERIT);
		Process start = pb.start();
		int ret = start.waitFor();
		assertEquals("the return value of the forked vm was not null", 0, ret);
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PICKLED_PATH))){
			@SuppressWarnings("unchecked")
			Collection<TestJoinPointData> readObject = (Collection<TestJoinPointData>) ois.readObject();
			assertEquals(4, readObject.size());
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		new TestDataSingleton.Transformable().a();
		try(FileOutputStream targetStream = new FileOutputStream(PICKLED_PATH)){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			System.out.println(TestDataSingleton.INSTANCE.getJoinPointData());
			new ObjectOutputStream(baos).writeObject(TestDataSingleton.INSTANCE.getJoinPointData());
			targetStream.write(baos.toByteArray());
		};
	}
	
}
