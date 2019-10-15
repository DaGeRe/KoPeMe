package kieker.monitoring.writer.filesystem;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({TestChangeableFolderSyncFsWriter.class,
   TestChangeableFolderSyncFsWriter.class,
   TestAggregatedTreeWriter.class,
   TestAggregatedTreeWriterSplitted.class,
   TestAggregatedMultifileWriting.class
})
public class WriterTestSuite {

}
