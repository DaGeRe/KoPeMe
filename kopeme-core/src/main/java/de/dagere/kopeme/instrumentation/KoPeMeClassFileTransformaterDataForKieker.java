package de.dagere.kopeme.instrumentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is a special {@link KoPeMeClassFileTransformaterData} class acting as input parser 
 * for the kieker instrumentation class.
 * 
 * As the code and the input vars are already defined here, 
 * you just need to add:
 * 
 * instrumentableClass
 * instrumentableMethod
 * level
 * 
 * also separated by ;;.
 * 
 * @author dhaeb
 *
 */
public class KoPeMeClassFileTransformaterDataForKieker extends KoPeMeClassFileTransformaterData {

	private static final String ERROR_MESSAGE = "Need three arguments for this premain! First ist the class to instrument! Second the method to instrument from! Last one is the recusion depth level.";
	private static final String KIEKER_MEASURE_UTIL_CLASS = KiekerMeasureUtil.class.getName();
	
	private static final String VARNAME_FOR_KIEKER_MEASURECLASS = "$kiekerMeasurementDataXXX";
	private static final String BEFORE_CODE = VARNAME_FOR_KIEKER_MEASURECLASS + " = new " + KIEKER_MEASURE_UTIL_CLASS + "(); " + callMethod("measureBefore");
	private static final String AFTER_CODE = callMethod("measureAfter");
	
	private static String callMethod(String method) {
		return VARNAME_FOR_KIEKER_MEASURECLASS + "." + method + "();";
	}

	public KoPeMeClassFileTransformaterDataForKieker(String cmd) {
		super(parse(cmd));
	}

	public KoPeMeClassFileTransformaterDataForKieker(String name, String string, int i) {
		super(name, string, BEFORE_CODE, AFTER_CODE, i);
	}

	static List<String> parse(String cmd) {
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

	public String getCommand() {
		return getInstrumentableClass() + DEFAULT_ARG_SEPARATOR + getInstrumentableMethod() + DEFAULT_ARG_SEPARATOR + getLevel();
	}

}
