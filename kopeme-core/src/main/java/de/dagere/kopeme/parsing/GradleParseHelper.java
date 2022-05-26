package de.dagere.kopeme.parsing;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public enum GradleParseHelper {
   ;
   public static final String ALTERNATIVE_NAME = "alternative_build.gradle";
   
   public static File findGradleFile(final File projectFolder) {
      File gradleFile = searchGradleFiles(projectFolder)[0];
      if (!gradleFile.exists()) {
         throw new RuntimeException("There was no .gradle file in " + projectFolder.getAbsolutePath());
      }
      return gradleFile;
   }

   public static File[] searchGradleFiles(final File projectFolder) {
      File gradleFile = new File(projectFolder, "build.gradle");
      if (!gradleFile.exists()) {
         File moduleNameCandidate = new File(projectFolder, projectFolder.getName() + ".gradle");
         if (!moduleNameCandidate.exists()) {
            File[] gradleFiles = projectFolder.listFiles(new FileFilter() {
               
               @Override
               public boolean accept(File potentialBuildfile) {
                  return potentialBuildfile.getName().endsWith(".gradle") && !potentialBuildfile.getName().equals(ALTERNATIVE_NAME) && !potentialBuildfile.isDirectory();
               }
            });
            return gradleFiles;
         } else {
            return new File[] { moduleNameCandidate };
         }
      } else {
         return new File[] { gradleFile };
      }

   }
}
