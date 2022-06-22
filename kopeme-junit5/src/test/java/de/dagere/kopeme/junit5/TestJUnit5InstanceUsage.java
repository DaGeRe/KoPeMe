package de.dagere.kopeme.junit5;

import java.io.File;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import de.dagere.kopeme.TestUtils;
import de.dagere.kopeme.junit5.exampletests.ExampleJUnit5InstanceUsageTest;
import de.dagere.kopeme.junit5.exampletests.ExampleJUnit5InstanceUsageTestWithMockito;

public class TestJUnit5InstanceUsage {

   @Test
   public void testInstanceUsage() {
      final File file = TestUtils.jsonFileForKoPeMeTest(ExampleJUnit5InstanceUsageTest.class.getName(), "testNormal");
      file.delete();
      
      ExampleJUnit5InstanceUsageTest.finished = 0;

      JUnit5RunUtil.runJUnit5TestOnly(ExampleJUnit5InstanceUsageTest.class);

      Assert.assertTrue(file.exists());
      
      Assert.assertEquals(4, ExampleJUnit5InstanceUsageTest.finished);
   }
   
   @Test
   public void testInstanceUsageWithMockito() {
      final File file = TestUtils.jsonFileForKoPeMeTest(ExampleJUnit5InstanceUsageTestWithMockito.class.getName(), "testNormal");
      file.delete();
      
      ExampleJUnit5InstanceUsageTestWithMockito.finished = 0;

      JUnit5RunUtil.runJUnit5TestOnly(ExampleJUnit5InstanceUsageTestWithMockito.class);

      Assert.assertTrue(file.exists());
      
      Assert.assertEquals(4, ExampleJUnit5InstanceUsageTestWithMockito.finished);
   }

}
