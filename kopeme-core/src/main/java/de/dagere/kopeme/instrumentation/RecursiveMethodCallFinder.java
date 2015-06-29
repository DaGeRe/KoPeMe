package de.dagere.kopeme.instrumentation;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class RecursiveMethodCallFinder {

	private Logger log = Logger.getLogger(getClass().getName());
	
	public Map<CtClass, Set<CtMethod>> find(final CtMethod declaredMethod, final int level) {
		final Map<CtClass, Set<CtMethod>> returnable = new HashMap<CtClass, Set<CtMethod>>();
		addToMap(declaredMethod.getDeclaringClass(), declaredMethod, returnable);
		if (level > 0) {
			try {
				declaredMethod.instrument(new ExprEditor() {
					@Override
					public void edit(MethodCall m)
							throws CannotCompileException {
						try {
							CtMethod method = m.getMethod();
							log.info(method.getLongName() + " / " + level);
							Map<CtClass, Set<CtMethod>> other = find(method, level - 1);
							joinMaps(returnable, other);
						} catch (NotFoundException e) {
							e.printStackTrace();
						}
						super.edit(m);
					}

				});
			} catch (CannotCompileException e) {
				// cannot possibly be thrown due to the fact that we don't change anything here
				e.printStackTrace();
			}
		}
		return returnable;
	}

	private void joinMaps(final Map<CtClass, Set<CtMethod>> first, Map<CtClass, Set<CtMethod>> second) {
		for (Entry<CtClass, Set<CtMethod>> entry : second.entrySet()) {
			CtClass ctClass = entry.getKey();
			if (first.containsKey(ctClass)) {
				Set<CtMethod> otherMethods = first.get(ctClass);
				otherMethods.addAll(entry.getValue());
				first.put(entry.getKey(), otherMethods);
			} else {
				first.put(ctClass, entry.getValue());
			}
		}
	}

	private void addToMap(final CtClass declaredClass, final CtMethod declaredMethod, final Map<CtClass, Set<CtMethod>> returnable) {
		if (returnable.containsKey(declaredClass)) {
			returnable.get(declaredClass).add(declaredMethod);
		} else {
			Set<CtMethod> value = new LinkedHashSet<CtMethod>();
			value.add(declaredMethod);
			returnable.put(declaredClass, value);
		}
	}

}
