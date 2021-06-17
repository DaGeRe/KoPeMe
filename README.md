# KoPeMe

KoPeMe is a framework for enabling performance tests in Java. This makes it possible to live a software development process where performance measures are taken continously and therefore react continously to changes in performance. With continous performance testing, one avoids refactorings after performance problems occured in a big testing phase before releasing the software.

## Usage

KoPeMe got three possibilities to enable performance tests in Java: 
- Using JUnit 4 with the performance test runner, by adding `@RunWith(PerformanceTestRunnerJUnit.class)` as annotation at class level
- Using JUnit 4 with the rule, by adding `@Rule public TestRule rule = new KoPeMeRule(this);` as instance variable to the class
- Using Junit 5 with the extension, by adding `@ExtendWith(KoPeMeExtension.class)` as annotation at class level
- Using JUnit 3, using `extends KoPeMeTestcase` (instead `extends TestCase`). This is mainly for compatibility with old software and is not recommended for daily use.
- Using kopeme-core, by running `PerformanceTestRunnerKoPeMe` for a class with performance tests

One of these variants should be enabled.

Additionally, the test should be annotated, e.g. like

```xml
@Test
@PerformanceTest(iterations = 500, warmup = 500, repetitions = 100)
public void measureMe() {
```

The usual JUnit test annotation should still be added if JUnit tests are measured.

The workload inside the test is repeated `repetitions*iterations` times, and `iterations` duration measurements are done (each after `repetitions` executions). Before this, `repetitions*warmup` workload executions are done without measurement (for warming up the current VM).

## Dependencies

For JUnit 4 or 5 tests, please add

```xml
<dependency>
    <groupId>de.dagere.kopeme</groupId>
    <artifactId>kopeme-junit</artifactId>
    <version>0.13</version>
</dependency>
```

to your build.

For JUnit 3 tests, please add

```xml
<dependency>
    <groupId>de.dagere.kopeme</groupId>
    <artifactId>kopeme-junit3</artifactId>
    <version>0.13</version>
</dependency>
```

to your build.

## Results

After measurement, in your KOPEME\_HOME-folder, a result-file will be placed. It will be extended by new results in every new run. If you don't define KOPEME\_HOME-folder, the file will be placed in your HOME-Folder in .KoPeMe. 
