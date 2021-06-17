package de.dagere.kopeme.parsing;

import java.io.File;
import java.io.FilenameFilter;

public enum GradleParseHelper {
   ;
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
         File[] gradleFiles = projectFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
               return name.endsWith(".gradle");
            }
         });
         return gradleFiles;
      } else {
         return new File[] { gradleFile };
      }

   }
}
