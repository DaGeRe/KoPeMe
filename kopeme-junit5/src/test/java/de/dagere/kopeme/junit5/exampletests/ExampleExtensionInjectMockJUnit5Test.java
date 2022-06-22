package de.dagere.kopeme.junit5.exampletests;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

interface IMock {
}

class MockProcessor {
   public List<String> processMock(final IMock mock) {
      return null;
   }
}

class MockMapper {
   private final MockProcessor processor;

   public MockMapper(final MockProcessor processor) {
      this.processor = processor;
   }

   public List<String> createResponse(final IMock mock) {
      List<String> stringList = processor.processMock(mock);
      System.out.println("Returned " + System.identityHashCode(mock) + " from " + System.identityHashCode(processor));
      return stringList;
   }

}

@ExtendWith(KoPeMeExtension.class)
@ExtendWith(MockitoExtension.class)
public class ExampleExtensionInjectMockJUnit5Test {
   
   public static int finishCount = 0;

   @Mock
   private IMock routeMock;

   @Mock
   private MockProcessor postProcessor;

   @InjectMocks
   private MockMapper mapper;

   @Test
   @PerformanceTest(warmup = 0, iterations = 3, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME", useKieker = false, executeBeforeClassInMeasurement = true, showStart = true, redirectToNull = false)
   public void testNormal() {
      List<String> singletonList = Collections.singletonList("This is a test");

      System.out.println("Should return " + System.identityHashCode(singletonList) + " from " + System.identityHashCode(postProcessor) + " " + singletonList.size());
      Mockito.when(postProcessor.processMock(routeMock)).thenReturn(singletonList);

      List<String> response = mapper.createResponse(routeMock);

      Assert.assertNotNull(response);
      Assert.assertNotNull(response.get(0));
      
      finishCount++;
   }
}
