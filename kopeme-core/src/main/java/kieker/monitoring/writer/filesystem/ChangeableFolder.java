package kieker.monitoring.writer.filesystem;

import java.io.File;
import java.io.IOException;

public interface ChangeableFolder{
   void setFolder(final File writingFolder) throws IOException;
}