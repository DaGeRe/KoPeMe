package de.dagere.kopeme.junit.rule.annotations;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import de.dagere.kopeme.annotations.PerformanceTest;

public class ParameterChecker {
   public static boolean parameterIndexInvalid(PerformanceTest annotation, LinkedHashMap<String, String> params) {
      if (params != null && params.size() > 0) {
         int chosenParameterIndex = annotation.chosenParameterIndex();
         Entry<String, String> firstParameterEntry = params.entrySet().iterator().next();
         String currentIndex = firstParameterEntry.getValue();
         
         if (chosenParameterIndex != -1 && chosenParameterIndex != Integer.parseInt(currentIndex)) {
            System.out.println("Test was disabled because of chosen parameter index (parameter) " + chosenParameterIndex);
            System.out.println("Current index: " + currentIndex);
            return true;
         }
         String chosenParameterIndexEnvironment = System.getenv(KoPeMeConstants.KOPEME_CHOSEN_PARAMETER_INDEX);
         if (chosenParameterIndexEnvironment != null) {
            int environmentChosenIndex = Integer.parseInt(chosenParameterIndexEnvironment);
            if (environmentChosenIndex != -1 && environmentChosenIndex != Integer.parseInt(currentIndex)) {
               System.out.println("Test was disabled because of chosen parameter index (environment variable) " + environmentChosenIndex);
               System.out.println("Current index: " + currentIndex);
               return true;
            }
         }
      }
      return false;
   }
}
