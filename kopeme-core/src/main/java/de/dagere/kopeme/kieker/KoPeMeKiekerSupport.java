package de.dagere.kopeme.kieker;

import java.io.File;
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
import kieker.monitoring.writer.filesystem.ChangeableFolderWriter;

/**
 * Class to control kieker tracing for KoPeMe.
 * 
 * @author dhaeb
 *
 */
public enum KoPeMeKiekerSupport {
   INSTANCE;
   private static final Logger LOG = LogManager.getLogger(KoPeMeKiekerSupport.class);

   private final FolderProvider fp;

   private KoPeMeKiekerSupport() {
      fp = FolderProvider.getInstance();
   }

   public void useKieker(final boolean useIt, final String testClassName, final String testCaseName) throws Exception {
      // MonitoringController.createInstance(configuration)

      if (useIt) {
         final IMonitoringController kiekerController = MonitoringController.getInstance();
         final ChangeableFolderWriter fsWriter = ChangeableFolderWriter.getInstance();
         if (fsWriter == null) {
            System.err.println("Kieker is not used, although specified. The " + ChangeableFolderWriter.class.getCanonicalName() + " has to be used!");
            String tempdir = System.getProperty("java.io.tmpdir");
            File tempDirFile = new File(tempdir);
            if (!tempDirFile.exists()) {
               System.err.println("Hint: Given java.io.tmpdir was " + tempdir + ", but this directory is not existing!");
            }
         } else {
            final File folderForCurrentPerformanceResult = fp.getFolderForCurrentPerformanceresults(testClassName, testCaseName);
            folderForCurrentPerformanceResult.mkdirs();
            fsWriter.setFolder(folderForCurrentPerformanceResult);
            kiekerController.enableMonitoring();
            LOG.debug("Kieker-Monitoring successfully enabled");
         }
      }
   }

   public void waitForEnd() {
      LOG.debug("Disabling Monitoring..");
      MonitoringController.getInstance().disableMonitoring();
      try {
         Field field = MonitoringController.class.getDeclaredField("writerController");
         field.setAccessible(true);
         WriterController writerController = (WriterController) field.get(MonitoringController.getInstance());
         
         Method cleanup = WriterController.class.getDeclaredMethod("cleanup");
         cleanup.setAccessible(true);
         cleanup.invoke(writerController);
         
         Field monitoringWriterThreadField = WriterController.class.getDeclaredField("monitoringWriterThread");
         monitoringWriterThreadField.setAccessible(true);
         MonitoringWriterThread thread = (MonitoringWriterThread) monitoringWriterThreadField.get(writerController);
         try {
            LOG.debug("Waiting for Thread-End");
            thread.join(5000);
            LOG.debug("Writing finished.");
         } catch (InterruptedException e1) {
            e1.printStackTrace();
         }
         
         WriterController newController = new WriterController(ConfigurationFactory.createSingletonConfiguration());
         field.set(MonitoringController.getInstance(), newController);
         
         Method init = WriterController.class.getDeclaredMethod("init");
         init.setAccessible(true);
         init.invoke(newController);
      } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchFieldException e1) {
         e1.printStackTrace();
      }
   }

}
