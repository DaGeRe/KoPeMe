package de.dagere.kopeme.parsing;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import de.dagere.kopeme.annotations.KoPeMeIgnore;

/**
 * Helps parsing JUnit classes for test execution
 * 
 * @author reichelt
 *
 */
public class JUnitParseUtil {

   private final static ThreadLocal<JavaParser> JAVA_PARSER = new ThreadLocal<JavaParser>() {
      protected JavaParser initialValue() {
         return new JavaParser();
      };
   };

   public synchronized static CompilationUnit parse(final File file) throws FileNotFoundException {
      final JavaParser parser = JAVA_PARSER.get();
      final Optional<CompilationUnit> result = parser.parse(file).getResult();
      return result.get();
   }

   public List<String> getAnnotatedMethods(File clazzFile, final String fqnAnnotationName, String annotationName) throws FileNotFoundException {
      CompilationUnit unit = parse(clazzFile);
      TypeDeclaration<?> typeDeclaration = unit.getPrimaryType().get();
      if (typeDeclaration instanceof ClassOrInterfaceDeclaration) {
         return getAnnotatedMethods((ClassOrInterfaceDeclaration) typeDeclaration, fqnAnnotationName, annotationName);
      } else {
         throw new RuntimeException("Classfile must contain class!");
      }
   }

   /**
    * Identifies all methods which are tests
    * 
    * @param clazz Class which should be analyzed
    * @param fqnAnnotationName Full qualified name of the annotation, e.g. de.dagere.kopeme.annotations.PerformanceTest
    * @param annotationName Simple name, e.g. PerformanceTest
    * @return List of annotated methods
    */
   public static List<String> getAnnotatedMethods(final ClassOrInterfaceDeclaration clazz, final String fqnAnnotationName, String annotationName) {
      List<String> methods = new LinkedList<>();

      if (isDeactivated(clazz)) {
         return methods;
      }

      for (final MethodDeclaration method : clazz.getMethods()) {
         boolean found = false;
         for (final AnnotationExpr annotation : method.getAnnotations()) {
            final String currentName = annotation.getNameAsString();

            if (currentName.equals(fqnAnnotationName) || currentName.equals(annotationName)) {
               found = true;
            }
         }

         boolean testIsDeactivated = isDeactivated(method);

         if (found && !testIsDeactivated) {
            methods.add(method.getNameAsString());
         }
      }
      return methods;
   }

   public static boolean isDeactivated(final ClassOrInterfaceDeclaration clazz) {
      return isDeactivated(clazz::isAnnotationPresent);
   }

   public static boolean isDeactivated(final MethodDeclaration method) {
      return isDeactivated(method::isAnnotationPresent);
   }

   private static boolean isDeactivated(Predicate<Class<? extends Annotation>> annotationChecker) {
      boolean testIsDeactivated = false;
      testIsDeactivated |= annotationChecker.test(Disabled.class);
      testIsDeactivated |= annotationChecker.test(Ignore.class);
      testIsDeactivated |= annotationChecker.test(KoPeMeIgnore.class);
      return testIsDeactivated;
   }
}
