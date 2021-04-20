package de.dagere.kopeme.junit5.rule;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class KoPeMeExtension implements ExecutionCondition {

   @Override
   public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {
      if (context.getTestInstance().isPresent()) {
         try {
            final KoPeMeJUnit5Starter starter = new KoPeMeJUnit5Starter(context);
            starter.start();
         } catch (Exception e) {
            e.printStackTrace();
         }
         return ConditionEvaluationResult.disabled("Outside KoPeMe");
      } else {
         return ConditionEvaluationResult.enabled("Inside KoPeMe");
      }
   }
}
