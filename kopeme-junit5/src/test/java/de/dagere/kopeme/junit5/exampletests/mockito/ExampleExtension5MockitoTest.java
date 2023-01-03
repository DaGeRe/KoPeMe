package de.dagere.kopeme.junit5.exampletests.mockito;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import de.dagere.kopeme.annotations.PerformanceTest;
import de.dagere.kopeme.junit5.extension.KoPeMeExtension;

class MyMockClazz{
   @Mock
   private Object value1;
   
   @Mock
   private Object value2;
   
   public Object getValue1() {
      return value1;
   }
   
   public Object getValue2() {
      return value2;
   }
}

/**
 * An example test for testing whether the KoPeMe-TestRule works correct
 * 
 * @author reichelt
 *
 */
@ExtendWith(KoPeMeExtension.class)
@ExtendWith(MockitoExtension.class)
public class ExampleExtension5MockitoTest {

   @Mock
   private Runnable myMock;
   
   @Spy
   private Runnable mySpy;
   
   @InjectMocks
   private MyMockClazz mapper;
   
	@BeforeEach
	public void setUp() {
		System.out.println("Executing Setup");
		try {
			Thread.sleep(5);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@AfterEach
   public void tearDown() {
      System.out.println("Executing Teardown");
      try {
         Thread.sleep(5);
      } catch (final InterruptedException e) {
         e.printStackTrace();
      }
   }

	@Test
	@PerformanceTest(warmup = 3, iterations = 3, repetitions = 1, timeout = 5000000, dataCollectors = "ONLYTIME", useKieker = false)
	public void testNormal() {
	   Mockito.doAnswer(new Answer<Void>() {

         @Override
         public Void answer(final InvocationOnMock invocation) throws Throwable {
            System.out.println("Doing nothing");
            return null;
         }
      }).when(myMock).run();
	   
	   System.out.println("Normal Execution");
	   myMock.run();
	   mySpy.run();
	   System.out.println(mapper.getValue1() + " " + mapper.getValue2());
	   try {
         Thread.sleep(15);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
	}
}
