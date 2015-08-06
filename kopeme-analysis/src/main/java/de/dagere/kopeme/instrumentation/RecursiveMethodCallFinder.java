package de.dagere.kopeme.instrumentation;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class using Javassist to find all methods called by a given method, parameterized by depth.
 * 
 * @author dhaeb
 *
 */
public class RecursiveMethodCallFinder {

	private final static Logger LOG = LogManager.getLogger(RecursiveMethodCallFinder.class);

	/**
	 * Method to find all method calls done by a given {@link CtMethod}. The function can be parameterized using a level parameter:
	 * <p>
	 * When level < 0: Throw {@link IllegalArgumentException}.
	 * </p>
	 * <p>
	 * When level == 0: return the given method as only result wrapped into the result type.
	 * </p>
	 * <p>
	 * When level > 0: search recursive for method calls in subsequent methods called by the given method. Every subsearch will reduce the level by one. Stops
	 * when all subsearches are finished.
	 * </p>
	 * <br/>
	 * 
	 * @param declaredMethod The method to search method call in
	 * @param level How deep should be the recursion level?
	 * 
	 * @return A map with the surrounding class as key and the found methods as value, wrapped into a set.
	 */
	public Map<CtClass, Set<CtMethod>> find(final CtMethod declaredMethod, final int level) {
		return find(declaredMethod, level, new HashMap<CtClass, Set<CtMethod>>());
	}

	/**
	 * Recursive helper function to find subsequent method calls of the given method. Uses "dict" as storage for method call during recursion, storing all
	 * called {@link CtMethod} objects in it.
	 */
	private Map<CtClass, Set<CtMethod>> find(final CtMethod declaredMethod, final int level, final Map<CtClass, Set<CtMethod>> dict) {
		if (level < 0) {
			throw new IllegalArgumentException("level < 0");
		}
		addToMap(declaredMethod.getDeclaringClass(), declaredMethod, dict);
		if (level > 0) {
			try {
				declaredMethod.instrument(new ExprEditor() {
					@Override
					public void edit(final MethodCall m) throws CannotCompileException {
						try {
							CtMethod method = m.getMethod();
							LOG.info(method.getLongName() + " / " + level);
							find(method, level - 1, dict);
						} catch (NotFoundException e) {
							e.printStackTrace(); // should not happen
						}
						super.edit(m);
					}

				});
			} catch (CannotCompileException e) {
				// cannot possibly be thrown due to the fact that we don't change anything here
				e.printStackTrace();
			}
		}
		return dict;
	}

	private void addToMap(final CtClass key, final CtMethod addable, final Map<CtClass, Set<CtMethod>> changeable) {
		if (changeable.containsKey(key)) {
			changeable.get(key).add(addable);
		} else {
			Set<CtMethod> value = new LinkedHashSet<CtMethod>();
			value.add(addable);
			changeable.put(key, value);
		}
	}

}
