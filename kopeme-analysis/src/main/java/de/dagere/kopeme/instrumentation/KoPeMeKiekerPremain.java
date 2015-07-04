package de.dagere.kopeme.instrumentation;

import java.lang.instrument.Instrumentation;

import javassist.NotFoundException;

/**
 * Java agent to inject the Kieker instrumentation code into arbitrary methods recursively.
 * The arguments needed for this agent are described in {@link KoPeMeClassFileTransformaterDataForKieker}.
 * 
 * @author dhaeb
 *
 */
public class KoPeMeKiekerPremain {

	public static void premain(String agentArgs, Instrumentation inst) throws NotFoundException {
		JavaAssistPremain.premain(new KoPeMeClassFileTransformaterDataForKieker(agentArgs).toString(), inst);
	}
}
