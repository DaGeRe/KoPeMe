package de.dagere.kopeme.instrumentation;

import java.lang.instrument.Instrumentation;

import javassist.NotFoundException;

/**
 * Java agent to inject the Kieker instrumentation code into arbitrary methods recursively. The arguments needed for this agent are described in
 * {@link KoPeMeClassFileTransformaterDataForKieker}.
 * 
 * @author dhaeb
 *
 */
public class KoPeMeKiekerPremain {

	public static void premain(final String agentArgs, final Instrumentation inst) throws NotFoundException {
		System.out.println("Starting the javassist agent...");
		final KoPeMeClassFileTransformater transformer = new KoPeMeClassFileTransformater(new KoPeMeClassFileTransformaterDataForKieker(agentArgs));
		inst.addTransformer(transformer);
	}
}
