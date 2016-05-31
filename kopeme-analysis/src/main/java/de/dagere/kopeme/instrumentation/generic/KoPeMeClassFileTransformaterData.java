package de.dagere.kopeme.instrumentation.generic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.dagere.kopeme.instrumentation.VarDeclarationData;

/**
 * This class handles the input to the {@link JavaAssistPremain} class. It's constructors parse the input into fields, and the {@link #toString} method can be
 * used to convert fields back to the command line representation of the arguments.
 * 
 * There are at least five arguments, given in the following order:
 * <p>
 * classname<br/>
 * method name<br/>
 * before code<br/>
 * after code<br/>
 * level<br/>
 * </p>
 * all parameters are separated by {@link #DEFAULT_ARG_SEPARATOR}.<br/>
 * <br/>
 * Optionally, you can add any number of local variables declarations given as<br/>
 * <i>Classname varname</i>, e.g.
 * <p>
 * <b>java.lang.Integer i</b>
 * </p>
 * You can also add multiple variable declarations separating them again by the same separator string.
 * 
 * @author dhaeb
 *
 */
public class KoPeMeClassFileTransformaterData {

	/**
	 * The separator used in the command line representation.
	 */
	public static final String DEFAULT_ARG_SEPARATOR = ";;";

	static final String DEFAULT_ARG_SEPARATOR_WITH_WHITESPACE_BEFORE = " " + DEFAULT_ARG_SEPARATOR;

	private final String instrumentableClass;
	private final String instrumentableMethod;
	private final String codeBefore;
	private final String codeAfter;
	private final int level;
	private final List<VarDeclarationData> declarations;

	public KoPeMeClassFileTransformaterData(final String instrumentableClass,
			final String instrumentableMethod, final String codeBefore, final String codeAfter,
			final int level) {
		this(instrumentableClass, instrumentableMethod, codeBefore, codeAfter, level, Collections.<VarDeclarationData> emptyList());
	}

	public KoPeMeClassFileTransformaterData(final String instrumentableClass,
			final String instrumentableMethod, final String codeBefore, final String codeAfter,
			final int level, final List<VarDeclarationData> declarations) {
		this.instrumentableClass = instrumentableClass;
		this.instrumentableMethod = instrumentableMethod;
		this.codeBefore = codeBefore;
		this.codeAfter = codeAfter;
		this.level = level;
		this.declarations = declarations;
	}

	/**
	 * Constructor performing the argument parse process.
	 * 
	 * @param agentArgs The arguments to parse (string separated with ;;)
	 */
	public KoPeMeClassFileTransformaterData(final String agentArgs) {
		this(Arrays.asList(agentArgs.split(DEFAULT_ARG_SEPARATOR)));
	}

	protected KoPeMeClassFileTransformaterData(final List<String> args) {
		this(args.get(0).trim(), args.get(1).trim(), args.get(2).trim(), args.get(3).trim(), Integer.parseInt(args.get(4).trim()), parseDeclarations(args.subList(5, args.size())));
	}

	private static List<VarDeclarationData> parseDeclarations(final List<String> subList) {
		ArrayList<VarDeclarationData> returnable = new ArrayList<>();
		for (String declaration : subList) {
			returnable.add(new VarDeclarationData(declaration.trim()));
		}
		return returnable;
	}

	public String getInstrumentableClass() {
		return instrumentableClass;
	}

	public String getInstrumentableMethod() {
		return instrumentableMethod;
	}

	public String getCodeBefore() {
		return codeBefore;
	}

	public String getCodeAfter() {
		return codeAfter;
	}

	public int getLevel() {
		return level;
	}

	public List<VarDeclarationData> getDeclarations() {
		return declarations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codeAfter == null) ? 0 : codeAfter.hashCode());
		result = prime * result
				+ ((codeBefore == null) ? 0 : codeBefore.hashCode());
		result = prime * result
				+ ((declarations == null) ? 0 : declarations.hashCode());
		result = prime
				* result
				+ ((instrumentableClass == null) ? 0 : instrumentableClass
						.hashCode());
		result = prime
				* result
				+ ((instrumentableMethod == null) ? 0 : instrumentableMethod
						.hashCode());
		result = prime * result + level;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KoPeMeClassFileTransformaterData other = (KoPeMeClassFileTransformaterData) obj;
		if (codeAfter == null) {
			if (other.codeAfter != null)
				return false;
		} else if (!codeAfter.equals(other.codeAfter))
			return false;
		if (codeBefore == null) {
			if (other.codeBefore != null)
				return false;
		} else if (!codeBefore.equals(other.codeBefore))
			return false;
		if (declarations == null) {
			if (other.declarations != null)
				return false;
		} else if (!declarations.equals(other.declarations))
			return false;
		if (instrumentableClass == null) {
			if (other.instrumentableClass != null)
				return false;
		} else if (!instrumentableClass.equals(other.instrumentableClass))
			return false;
		if (instrumentableMethod == null) {
			if (other.instrumentableMethod != null)
				return false;
		} else if (!instrumentableMethod.equals(other.instrumentableMethod))
			return false;
		if (level != other.level)
			return false;
		return true;
	}

	/**
	 * Creates the command line representation of this class members.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder toStringBuilder = new StringBuilder();
		toStringBuilder.append(instrumentableClass);
		toStringBuilder.append(DEFAULT_ARG_SEPARATOR);
		toStringBuilder.append(instrumentableMethod);
		toStringBuilder.append(DEFAULT_ARG_SEPARATOR);
		toStringBuilder.append(codeBefore);
		toStringBuilder.append(DEFAULT_ARG_SEPARATOR_WITH_WHITESPACE_BEFORE);
		toStringBuilder.append(codeAfter);
		toStringBuilder.append(DEFAULT_ARG_SEPARATOR_WITH_WHITESPACE_BEFORE);
		toStringBuilder.append(level);
		for (VarDeclarationData data : getDeclarations()) {
			toStringBuilder.append(DEFAULT_ARG_SEPARATOR);
			toStringBuilder.append(data.toString());
		}
		return toStringBuilder.toString();
	}

}