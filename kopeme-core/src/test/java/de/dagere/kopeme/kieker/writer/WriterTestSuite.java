package de.dagere.kopeme.kieker.writer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({TestChangeableFolderWriter.class,
   TestChangeableFolderWriter.class,
   TestAggregatedTreeWriter.class,
   TestAggregatedTreeWriterSplitted.class,
   TestAggregatedMultifileWriting.class
})
public class WriterTestSuite {

}
