KoPeMe
======

KoPeMe is a framework for enabling performance tests in Java. This makes it possible to live a software development process where performance measures are taken continously and therefore react continously to changes in performance. With continous performance testing, one avoids refactorings after performance problems occured in a big testing phase before releasing the software.

In it's core, KoPeMe got three possibilities to enable performance tests in Java: Performance Tests with JUnit 4 style by kopeme-junit (which is the preferred way), Performance Tests without JUnit by kopeme-core and Performance Tests with JUnit 3 style by kopeme-junit3. 

== Getting Started ==

KoPeMe is in maven central, so to get started, just add

```xml
<dependency>
    <groupId>de.dagere.kopeme</groupId>
    <artifactId>kopeme-junit</artifactId>
    <version>0.7</version>
</dependency>
```

To your maven depencies. After that, add a test class with a simple performance test:

```java
        @Test
	@PerformanceTest(executionTimes = 1000, warmupExecutions = 500,
			assertions =
			{ @Assertion(collectorname = "de.dagere.kopeme.datacollection.TimeDataCollector", maxvalue = 15000) })
	public void testSomething() {
	   ...
        }
```

Furthermore the class needs to be annotated with the PerformanceTestRunnerJUnit, i.e.

```
@RunWith(PerformanceTestRunnerJUnit.class)
```

Now just run this test with JUnit from your IDE or maven shurefire, and whatever you've written in the method will by executed 500 times for warmup and 1000 times for real measurement. In your KOPEME\_HOME-folder, a result-file will be placed. It will be extended by new results in every new run. If you don't define KOPEME\_HOME-folder, the file will be placed in your HOME-Folder in .KoPeMe. 

If your execution takes more than 15 seconds, the test will fail. If you now execute this test every time in your continous build environment, you can be shure that in every new commit, your testcase will not take more than 15 seconds.

To explore other abilities of KopeMe like pure or JUnit-3-Style tests, Jenkins plugin and more, please visit kopeme.dagere.de or explore the javadocs of KoPeMe.
