package de.dagere.kopeme.datastorage;

import org.junit.Assert;
import org.junit.Test;

import de.dagere.kopeme.generated.Result;
import de.dagere.kopeme.generated.Result.Params;
import de.dagere.kopeme.generated.Result.Params.Param;

public class TestParamStoring {
   @Test
   public void testParamstoring() {
      Result result = new Result();
      result.setParams(new Params());
      Param jmhParam1 = new Param();
      jmhParam1.setKey("test");
      jmhParam1.setValue("val1");
      result.getParams().getParam().add(jmhParam1);
      
      Param jmhParam2 = new Param();
      jmhParam2.setKey("test2");
      jmhParam2.setValue("val2");
      result.getParams().getParam().add(jmhParam2);
      
      Assert.assertEquals(result.getParams().getParam().get(1).getKey(), "test2");
   }
}
