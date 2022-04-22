package de.dagere.kopeme.datastorage;

import java.util.LinkedHashMap;
import java.util.Map;

public class ParamNameHelper {
   
   public static final String PARAM_VALUE_SEPARATOR = "-";
   
   public static String paramsToString(final LinkedHashMap<String, String> parameters) {
      String result;
      if (parameters != null) {
         result = "";
         for (Map.Entry<String, String> param : parameters.entrySet()) {
            result += param.getKey() + PARAM_VALUE_SEPARATOR + param.getValue() + " ";
         }
         result = result.substring(0, result.length() - 1);
      } else {
         result = null;
      }
      return result;
   }
}
