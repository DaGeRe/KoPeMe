package de.dagere.kopeme.instrumentation;

import java.lang.instrument.Instrumentation;

import javassist.NotFoundException;

public class KoPeMeKiekerPremain {

	public static void premain(String agentArgs, Instrumentation inst) throws NotFoundException {
		JavaAssistPremain.premain(new KoPeMeClassFileTransformaterDataForKieker(agentArgs).toString(), inst);
	}
}
