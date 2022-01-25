import java.io.File;

import org.junit.jupiter.api.Test;

public class DurationRecordAspectIT {
   
   @Test
   public void testAspect() {
      File file = new File("target");
      for (File subfile : file.listFiles()) {
         System.out.println(subfile.getAbsolutePath());
      }
   }
}
