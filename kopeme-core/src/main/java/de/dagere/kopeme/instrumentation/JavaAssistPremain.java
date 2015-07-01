package de.dagere.kopeme.instrumentation;

import java.lang.instrument.Instrumentation;

import javassist.NotFoundException;

public class JavaAssistPremain {

	public static void premain(String agentArgs, Instrumentation inst) throws NotFoundException {
		System.out.println(agentArgs); // TODO print args
		System.out.println("Starting the javassist agent...");
		String[] args = agentArgs.split(";;");
		inst.addTransformer(new KoPeMeClassFileTransformater(args[0], args[1], args[2] , args[3], Integer.parseInt(args[4])));
	}
	
}
