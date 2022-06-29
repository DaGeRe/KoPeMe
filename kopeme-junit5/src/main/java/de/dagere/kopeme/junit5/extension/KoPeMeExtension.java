package de.dagere.kopeme.junit5.extension;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * This extension enables execution of JUnit 5 tests with KoPeMe. Since the execution scheme of JUnit 5 is strict, KoPeMe cannot directly decide when to execute
 * BeforeAll, BeforeEach, Mockito initializations etc. Therefore, the KoPeMe extension is a ExecutionCondition and fully disabled the regular execution of a
 * statement in JUnit 5, and starts the test execution by itself. If then the KoPeMe-started test execution reaches the ExecutionCondition-phase again, since we are
 * inside KoPeMe, we need to set the test to enabled.
 * @author reichelt
 *
 */
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
