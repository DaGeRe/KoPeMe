package de.dagere.kopeme.junit5;

import de.dagere.kopeme.junit5.exampletests.rules.ExampleExtensionTestThrowingOOM;
import de.dagere.kopeme.junit5.rule.KoPeMeExtension;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;

public class TestJUnit5ThrowingOOM {

   @Test
   @Disabled
   public void testCorrectThrowingOOM() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtensionTestThrowingOOM.class);

      Assert.assertTrue(KoPeMeExtension.isLastRunFailed());
   }
}
