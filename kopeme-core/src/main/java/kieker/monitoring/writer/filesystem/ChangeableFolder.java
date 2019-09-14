package kieker.monitoring.writer.filesystem;

import java.io.File;

public interface ChangeableFolder{
   void setFolder(final File writingFolder);
}