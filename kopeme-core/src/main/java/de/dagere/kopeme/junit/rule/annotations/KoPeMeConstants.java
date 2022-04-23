package de.dagere.kopeme.junit.rule.annotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class KoPeMeConstants {
   public static final String JUNIT_PARAMETERIZED = "JUNIT_PARAMETERIZED";
   public static final String KOPEME_CHOSEN_PARAMETER_INDEX = "KOPEME_CHOSEN_PARAMETER_INDEX";
   
   public final static ObjectMapper OBJECTMAPPER = new ObjectMapper();

   static {
      OBJECTMAPPER.enable(SerializationFeature.INDENT_OUTPUT);
   }
}
