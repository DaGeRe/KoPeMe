package de.dagere.kopeme.kieker;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datastorage.FolderProvider;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.controller.WriterController;
import kieker.monitoring.writer.MonitoringWriterThread;
import kieker.monitoring.writer.filesystem.AggregatedTreeWriter;
import kieker.monitoring.writer.filesystem.ChangeableFolder;
import kieker.monitoring.writer.filesystem.ChangeableFolderWriter;

/**
 * Class to control kieker tracing for KoPeMe.
 * 
 * @author dhaeb
 *
 */
public class KoPeMeKiekerSupport {
   public static final KoPeMeKiekerSupport INSTANCE = new KoPeMeKiekerSupport();
   private static final Logger LOG = LogManager.getLogger(KoPeMeKiekerSupport.class);

   private final FolderProvider fp;

   private KoPeMeKiekerSupport() {
      fp = FolderProvider.getInstance();
   }

   public void useKieker(final boolean useIt, int warmup, final String testClassName, final String testCaseName) throws IOException {
      if (useIt) {
         final IMonitoringController kiekerController = MonitoringController.getInstance();
         final ChangeableFolder fsWriter = getWriter();
         final File folderForCurrentPerformanceResult = fp.getFolderForCurrentPerformanceresults(testClassName, testCaseName);
         folderForCurrentPerformanceResult.mkdirs();
         fsWriter.setFolder(folderForCurrentPerformanceResult);
         if (fsWriter instanceof AggregatedTreeWriter) {
            AggregatedTreeWriter writer = (AggregatedTreeWriter) fsWriter;
            writer.getStatisticConfig().setWarmup(warmup);
         }
         kiekerController.enableMonitoring();
         LOG.debug("Kieker-Monitoring successfully enabled");
      }
   }

   private ChangeableFolder getWriter() {
      ChangeableFolder fsWriter = ChangeableFolderWriter.getInstance();
      if (fsWriter == null) {
         fsWriter = AggregatedTreeWriter.getInstance();
         if (fsWriter == null) {
            System.err.println("Kieker is not used, although specified. The " +
                  ChangeableFolderWriter.class.getCanonicalName() + " or " + AggregatedTreeWriter.class.getCanonicalName() + " have to be used!");
            final String tempdir = System.getProperty("java.io.tmpdir");
            final File tempDirFile = new File(tempdir);
            if (!tempDirFile.exists()) {
               System.err.println("Warning: Given java.io.tmpdir was " + tempdir + ", but this directory is not existing!");
            }
            throw new RuntimeException("Kieker Error: Monitoring not possible, but specified!");
         }
      }
      return fsWriter;
   }

   /**
    * Waits for the old Kieker-MonitoringWriterThread to end its writing and starts a new Kieker-MonitoringWriterThread.
    * 
    * Since kieker is designed to have exactly one MonitoringController, which has one WriterControler which has a MontioringWriterThread, we need to get those by Reflections.
    */
   public void waitForEnd() {
      LOG.debug("Disabling Monitoring..");
      MonitoringController.getInstance().disableMonitoring();
      try {
         final Field field = finishMonitoring(MonitoringController.getInstance());

         final WriterController newController = new WriterController(ConfigurationFactory.createSingletonConfiguration());
         field.set(MonitoringController.getInstance(), newController);

         final Method init = WriterController.class.getDeclaredMethod("init");
         init.setAccessible(true);
         init.invoke(newController);

      } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e1) {
         e1.printStackTrace();
      }
   }

   public static Field finishMonitoring(final IMonitoringController monitoringController)
         throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
      final Field field = MonitoringController.class.getDeclaredField("writerController");
      field.setAccessible(true);
      final WriterController writerController = (WriterController) field.get(monitoringController);

      final Method cleanup = WriterController.class.getDeclaredMethod("cleanup");
      cleanup.setAccessible(true);
      cleanup.invoke(writerController);

      final Field monitoringWriterThreadField = WriterController.class.getDeclaredField("monitoringWriterThread");
      monitoringWriterThreadField.setAccessible(true);
      final MonitoringWriterThread thread = (MonitoringWriterThread) monitoringWriterThreadField.get(writerController);
      try {
         LOG.debug("Waiting for Thread-End: {}", thread);
         for (int i = 0; i < 100 && thread.isAlive(); i++) {
            thread.join(6000);
            LOG.debug("Waiting for Thread-End: {}, Thread alive: {}", thread, thread.isAlive());
         }
         LOG.debug("Writing finished, Thread alive: " + thread.isAlive());
      } catch (final InterruptedException e1) {
         e1.printStackTrace();
      }
      return field;
   }

}
