package de.dagere.kopeme.kieker.writer.onecall;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OneCallReader {
   public static Set<String> getCalledMethods(final File folder) {
      Set<String> methodNames = new LinkedHashSet<>();
      for (File file : getFiles(folder)) {
         try {
            List<String> fileMethods = Files.readAllLines(file.toPath());
            methodNames.addAll(fileMethods);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
      return methodNames;
   }
   
   private static File[] getFiles(final File folder) {
      return folder.listFiles(new FileFilter() {
         @Override
         public boolean accept(final File pathname) {
            return pathname.getName().endsWith(".dat");
         }
      });
   }
}
