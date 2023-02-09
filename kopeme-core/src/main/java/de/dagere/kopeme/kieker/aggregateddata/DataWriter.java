package de.dagere.kopeme.kieker.aggregateddata;

import java.io.Closeable;

public interface DataWriter extends Runnable, Closeable {

   void finish();

   void run();

   void write(DataNode node, long duration);

}