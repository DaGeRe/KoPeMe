package de.dagere.kopeme.datastorage;

import de.dagere.kopeme.generated.Result.Params;
import de.dagere.kopeme.generated.Result.Params.Param;

public class ParamNameHelper {
   
   public static final String PARAM_VALUE_SEPARATOR = "-";
   
   public static String paramsToString(final Params params) {
      String result;
      if (params != null) {
         result = "";
         for (Param param : params.getParam()) {
            result += param.getKey() + PARAM_VALUE_SEPARATOR + param.getValue() + " ";
         }
         result = result.substring(0, result.length() - 1);
      } else {
         result = null;
      }
      return result;
   }
}
