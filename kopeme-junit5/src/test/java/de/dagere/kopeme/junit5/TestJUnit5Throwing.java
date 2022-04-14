package de.dagere.kopeme.junit5;

import java.io.File;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.junit5.exampletests.rules.ExampleExtensionTestThrowing;
import de.dagere.kopeme.junit5.rule.KoPeMeExtension;

public class TestJUnit5Throwing {
   
   @Test
   public void testCorrectThrowing() {
      File file = JUnit5RunUtil.runJUnit5Test(ExampleExtensionTestThrowing.class);
      
      Assert.assertTrue(KoPeMeExtension.isLastRunFailed());
   }
}
