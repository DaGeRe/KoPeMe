package de.dagere.kopeme.junit5.exampletests.mockito.mocked;

import java.util.List;

public class MockMapper {
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