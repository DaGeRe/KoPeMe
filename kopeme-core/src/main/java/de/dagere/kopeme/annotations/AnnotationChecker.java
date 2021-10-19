package de.dagere.kopeme.annotations;

public class AnnotationChecker {
   public static void check(final PerformanceTest annotation) {
      if (annotation.iterations() < 1) {
         throw new RuntimeException("Iterations need to be 1 or more!");
      }
      if (annotation.repetitions() < 1) {
         throw new RuntimeException("Iterations need to be 1 or more!");
      }
      if (annotation.warmup() < 0) {
         throw new RuntimeException("Warmup needs to be 0 or more!");
      }
   }
}
