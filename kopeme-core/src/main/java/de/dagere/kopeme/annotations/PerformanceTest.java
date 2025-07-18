package de.dagere.kopeme.annotations;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.core.controller.MonitoringController;

/**
 * This annotation for <code>public void</code> methods is telling that the method is a KoPeMe-Testcase. This means that it is executed several times with performance measurements
 * to get performance measures for the testcase. It is possible to specify the count of executions and other configuration via parameters of &#064;PerformanceTest.
 * 
 * A annotated method could start like following:
 * 
 * <pre>
 * &#064;PerformanceTest(warmupExecutions = 3, 
 * 			executionTimes = 10)
 * public void simpleDeviationTest() {
 * </pre>
 * 
 * This would mean that it should be executed 3 times to be warmed up (i.e. without measurement) and 10 times for real measurement.
 * 
 * @author reichelt
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ METHOD })
public @interface PerformanceTest {
   /**
    * Optionally specificy the count of execution which should be measured.
    * 
    * @return
    */
   int iterations() default 10;

   /**
    * Specify whether to show the start and the end of a test execution.
    */
   boolean showStart() default false;

   boolean useSampling() default false;

   int samplingInterval() default 100;

   String samplingResultsFolder() default "<NULL>";

   boolean redirectToTemp() default false;

   boolean redirectToNull() default false;

   boolean executeBeforeClassInMeasurement() default false;

   /**
    * Optionally specify count of repetitions of same test.
    * 
    * @return
    */
   int repetitions() default 1;

   /**
    * Optionally specify the count of executions, that should be executed before the measuring begins.
    * 
    * @return Execution times of the test
    */
   int warmup() default 1;

   /**
    * Optionally specify the timeout after which the test is canceled. The test is canceled after the timeout occurs for all executions, not for a single. execution of the method.
    * 
    * @return Warmup executions of the test
    */
   long timeout() default 100000;

   /**
    * Optionally specify that all data should be logged, i.e. primarily all measured values instead of only average values.
    * 
    * @return Whether to log full data
    */
   boolean logFullData() default false;

   /**
    * Optionally specify which performance thresholds should be checked after the execution is completed.
    * 
    * @return Assertations that should be checked
    */
   Assertion[] assertions() default {};

   /**
    * Sets the Datacollectors - possible values are STANDARD, ONLYTIME and NONE.
    * 
    * @return Datacollectors that should be used
    */
   String dataCollectors() default "STANDARD";

   /**
    * Optionally specify for <emph>all<emph> datacollectors, for which maximal standard deviation an early stop is executed. This means that, if all relative standard deviations
    * fall below the given maximale relative deviations thresholds, the test is stoped and the measured value until the stop is the final result.
    * 
    * @return Maximale relative standard deviation for early abortion of the test
    */
   MaximalRelativeStandardDeviation[] deviations() default {};

   /**
    * Optionally specify how many executions, if <code>deviations</code> is specified, are executed before an early stop is eventually happening.
    * 
    * @return Minimal execution times for early abortion of the test
    */
   int minEarlyStopExecutions() default 10;

   /**
    * Optionally specifies that kieker should be used to collect method execution times. When set to true, the user must ensure that the code will produce
    * {@link OperationExecutionRecord} for the kieker {@link MonitoringController}. This can be achieved using manual calls to the later class or using the Aspect J weaving
    * mechanism. The last one is recommended.
    * 
    * @return true if the kieker framework should be used
    */
   boolean useKieker() default true;

   /**
    * Optionally specifies how long to wait (in seconds) until the Kieker wrinting thread is finished. By default, KoPeMe waits 10 seconds and kills the writer thread afterwards.
    * This might lead to unreadable or incomplete traces.
    * 
    * The log will indicate this problem. If you encounter this problem and measure the performance, try to monitor less methods or use DurationRecords for monitoring instead
    * simply increasing this timeout, since big writer queues will affect the performance and might lead to lost monitoring records.
    * 
    * @return seconds KoPeMe waites for Kieker writer finishing
    */
   int kiekerWaitTime() default 10;

   /**
    * Defines the duration for a timebased testcase in milliseconds
    * 
    * @return duration of the timebased testcase
    */
   int duration() default 60000;

   /**
    * Defines which parameter index should be (solely) executed - Since Gradle and Maven currently do not support this, it needs to be implemented in KoPeMe itself
    */
   int chosenParameterIndex() default -1;
}