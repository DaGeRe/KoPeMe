package de.dagere.kopeme.instrumentation;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.instrumentation.generic.KoPeMeClassFileTransformaterData;

/**
 * The class which performs the byte code transformation, injecting statements given from {@link KoPeMeClassFileTransformaterData}.
 * 
 * @author dhaeb
 *
 */
public class KoPeMeClassFileTransformater implements ClassFileTransformer {

	private final static Logger LOG = LogManager.getLogger(KoPeMeClassFileTransformater.class);

	private final ClassPool pool = ClassPool.getDefault();
	private Map<CtClass, Set<CtMethod>> instrumentable;
	private final KoPeMeClassFileTransformaterData parameterObject;

	public KoPeMeClassFileTransformater(final KoPeMeClassFileTransformaterData parameterObject) throws NotFoundException {
		this.parameterObject = parameterObject;
		findInstrumentableClasses(parameterObject);
	}

	private void findInstrumentableClasses(final KoPeMeClassFileTransformaterData parameterObject) throws NotFoundException {
		CtClass findable = pool.get(parameterObject.getInstrumentableClass());
		CtMethod method = findable.getDeclaredMethod(parameterObject.getInstrumentableMethod());
		instrumentable = new RecursiveMethodCallFinder().find(method, parameterObject.getLevel());
	}

	@Override
	public byte[] transform(final ClassLoader loader, final String className,
			final Class<?> classBeingRedefined,
			final ProtectionDomain protectionDomain,
			final byte[] classfileBuffer) throws IllegalClassFormatException {
		byte[] returnable = classfileBuffer;
		try {
			String currentClass = className.replace("/", ".");
			CtClass instrumentableClass = pool.get(currentClass);
			Set<CtMethod> instrumentableMethods = instrumentable.get(instrumentableClass);
			if (instrumentableMethods != null) {
				LOG.info("Instrumenting " + className);
				instrumentableClass.defrost();
				for (CtMethod instrumentableMethod : instrumentableMethods) {
					around(instrumentableMethod, parameterObject.getCodeBefore(), parameterObject.getCodeAfter(), parameterObject.getDeclarations());
				}
				returnable = instrumentableClass.toBytecode();
			}
			return returnable;
		} catch (CannotCompileException | IOException | NotFoundException e) {
			LOG.warn(e.toString());
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
	 * @throws NotFoundException
	 */
	private void around(final CtMethod m, final String before, final String after, final List<VarDeclarationData> declarations) throws CannotCompileException, NotFoundException {
		String signature = Modifier.toString(m.getModifiers()) + " " + m.getReturnType().getName() + " " + m.getLongName();
		LOG.info("--- Instrumenting " + signature);
		for (VarDeclarationData declaration : declarations) {
			m.addLocalVariable(declaration.getName(), pool.get(declaration.getType()));
		}
		m.insertBefore(before);
		m.insertAfter(after);
	}

}
