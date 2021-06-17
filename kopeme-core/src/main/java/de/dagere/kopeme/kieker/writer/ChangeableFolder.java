package de.dagere.kopeme.kieker.writer;

import java.io.File;
import java.io.IOException;

public interface ChangeableFolder {
   void setFolder(final File writingFolder) throws IOException;
}