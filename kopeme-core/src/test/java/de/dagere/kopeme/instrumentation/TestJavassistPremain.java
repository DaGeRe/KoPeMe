package de.dagere.kopeme.instrumentation;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

import org.junit.Test;

import de.dagere.kopeme.instrumentation.TestDataSingleton.TestJoinPointData;
import de.dagere.kopeme.instrumentation.TestDataSingleton.Transformable;

public class TestJavassistPremain {

	private static final String PICKLED_PATH = "target/test-classes/javassist-premain-tds.bin";

	@Test
	public void run() throws Exception {
		// TODO adjust for windows machines if needed
		String jarFileName = "kopeme-core-0.10-SNAPSHOT-agent.jar";
		String instructableClass = Transformable.class.getName();
		String instractableMethod = "b";
		String codeBefore = TestKoPeMeClassFileTransformator.FIXTURE_BEFORE;
		String codeAfter =TestKoPeMeClassFileTransformator.FIXTURE_AFTER;
		ProcessBuilder pb = new ProcessBuilder("java", "-cp", System.getProperty("java.class.path"),  
												String.format("-javaagent:target/%s=%s;;%s;;%s ;;%s ;;3", jarFileName, instructableClass, instractableMethod, codeBefore, codeAfter),
												TestJavassistPremain.class.getName());
		pb.redirectError(new File("target/out"));
		pb.redirectOutput(new File("target/err"));
		int ret = pb.start().waitFor();
		assertEquals("the return value of the forked vm was not null", 0, ret);
		try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PICKLED_PATH))){
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
