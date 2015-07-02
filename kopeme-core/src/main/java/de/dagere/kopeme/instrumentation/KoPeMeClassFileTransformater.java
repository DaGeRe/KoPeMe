package de.dagere.kopeme.instrumentation;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class KoPeMeClassFileTransformater implements ClassFileTransformer {

	private ClassPool pool = ClassPool.getDefault();
	private Map<CtClass, Set<CtMethod>> instrumentable;
	private String codeBefore;
	private String codeAfter;
	private Logger logger = Logger.getLogger(getClass().getName());

	public KoPeMeClassFileTransformater(final KoPeMeClassFileTransformaterData parameterObject) throws NotFoundException {
		this.codeBefore = parameterObject.getCodeBefore();
		this.codeAfter = parameterObject.getCodeAfter();
		CtClass findable = pool.get(parameterObject.getInstrumentableClass());
		CtMethod method = findable.getDeclaredMethod(parameterObject.getInstrumentableMethod());
		instrumentable = new RecursiveMethodCallFinder().find(method, parameterObject.getLevel());
	}

	@Override
	public byte[] transform(final ClassLoader loader, final String className,
			final Class<?> classBeingRedefined,
			final ProtectionDomain protectionDomain,
			final byte[] classfileBuffer) throws IllegalClassFormatException {
		Set<CtMethod> instrumentableMethods;
		byte[] returnable = classfileBuffer;
		try {
			String currentClass = className.replace("/", ".");
			CtClass instrumentableClass = pool.get(currentClass);
			instrumentableMethods = instrumentable.get(instrumentableClass);
			if (instrumentableMethods != null) {
				logger.info("Instrumenting " + className);
				instrumentableClass.defrost();
				for (CtMethod instrumentableMethod : instrumentableMethods) {
					around(instrumentableMethod, codeBefore, codeAfter);
				}
				returnable = instrumentableClass.toBytecode();
			} 
			return returnable;
		} catch (CannotCompileException | IOException | NotFoundException e) {
			logger.warning(e.toString());
			throw new IllegalClassFormatException();
		}
	}

	/**
	 * Method introducing code before and after a given javassist method.
	 * 
	 * @param m
	 * @param before
	 * @param after
	 * @throws CannotCompileException
	 */
	private void around(final CtMethod m, final String before, final String after) throws CannotCompileException {
		m.insertBefore(before);
		m.insertAfter(after, true);
	}

}
