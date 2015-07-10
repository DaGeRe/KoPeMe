package de.dagere.kopeme.instrumentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dagere.kopeme.instrumentation.generic.KoPeMeClassFileTransformaterData;

/**
 * This is a special {@link KoPeMeClassFileTransformaterData} class acting as input parser 
 * for the kieker instrumentation class {@link KoPeMeKiekerPremain}.
 * 
 * <p>As the injected code sections and the input variables are already defined (for kieker trace monitoring purposes), 
 * you just need to give the following parameters, separated by {@link KoPeMeClassFileTransformaterData#DEFAULT_ARG_SEPARATOR}:</p>
 * 
 * instrumentableClass <br/>
 * instrumentableMethod<br/>
 * level<br/>
 * <br/>
 * @author dhaeb
 *
 */
public class KoPeMeClassFileTransformaterDataForKieker extends KoPeMeClassFileTransformaterData {

	private static final String ERROR_MESSAGE = "Need three arguments for this premain! First ist the class to instrument! Second the method to instrument from! Last one is the recusion depth level.";
	private static final String KIEKER_MEASURE_UTIL_CLASS = KiekerMeasureUtil.class.getName();
	
	private static final String VARNAME_FOR_KIEKER_MEASURECLASS = "$kiekerMeasurementDataXXX";
	private static final String BEFORE_CODE = VARNAME_FOR_KIEKER_MEASURECLASS + " = new " + KIEKER_MEASURE_UTIL_CLASS + "(); " + callMethod("measureBefore");
	private static final String AFTER_CODE = callMethod("measureAfter");
	
	private static String callMethod(final String method) {
		return VARNAME_FOR_KIEKER_MEASURECLASS + "." + method + "();";
	}

	public KoPeMeClassFileTransformaterDataForKieker(final String cmd) {
		super(parse(cmd));
	}

	public KoPeMeClassFileTransformaterDataForKieker(final String name, final String string, final int i) {
		super(name, string, BEFORE_CODE, AFTER_CODE, i);
	}

	static List<String> parse(final String cmd) {
		List<String> cmdAsList = Arrays.asList(cmd.split(KoPeMeClassFileTransformaterData.DEFAULT_ARG_SEPARATOR));
		if(cmdAsList.size() != 3){
			throw new IllegalArgumentException(ERROR_MESSAGE);
		}
		List<String> returnable = new ArrayList<String>(cmdAsList.subList(0, 2));
		returnable.add(BEFORE_CODE);
		returnable.add(AFTER_CODE);
		returnable.add(cmdAsList.get(2));
		returnable.add(new VarDeclarationData(KIEKER_MEASURE_UTIL_CLASS, VARNAME_FOR_KIEKER_MEASURECLASS).toString());
		return returnable;
	}

	/**
	 * As {@link #toString()} will give the command representation of the superclass {@link KoPeMeClassFileTransformaterData},
	 * we introduced this function to get the command representation of this class.
	 * 
	 * @return the special command representation of this class with only the three parameters of this class.
	 */
	public String getCommand() {
		return getInstrumentableClass() + DEFAULT_ARG_SEPARATOR + getInstrumentableMethod() + DEFAULT_ARG_SEPARATOR + getLevel();
	}

}
