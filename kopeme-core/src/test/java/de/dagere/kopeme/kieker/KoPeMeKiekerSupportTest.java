package de.dagere.kopeme.kieker;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;

public class KoPeMeKiekerSupportTest {
   
   @Test
   public void testStartAndEnd() throws IOException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
      KoPeMeKiekerSupport.INSTANCE.useKieker(true, "MyTest", "test");
      IMonitoringController instance = MonitoringController.getInstance();
      System.out.println(System.identityHashCode(instance));
      
      Assert.assertTrue(instance.isMonitoringEnabled());
      KoPeMeKiekerSupport.INSTANCE.waitForEnd();
      
      Assert.assertFalse(instance.isMonitoringEnabled());
   }
}
