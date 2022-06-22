package de.dagere.kopeme.junit5.extension;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class KoPeMeExtension implements ExecutionCondition {

   private static boolean lastRunFailed = false;

   @Override
   public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {
      if (context.getTestInstance().isPresent()) {
         try {
            final KoPeMeJUnit5Starter starter = new KoPeMeJUnit5Starter(context);
            starter.start();
         } catch (Throwable e) {
            e.printStackTrace();
            lastRunFailed = true;
         }
         return ConditionEvaluationResult.disabled("Outside KoPeMe");
      } else {
         return ConditionEvaluationResult.enabled("Inside KoPeMe");
      }
   }

   public static boolean isLastRunFailed() {
      return lastRunFailed;
   }
}
