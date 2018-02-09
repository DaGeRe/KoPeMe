package de.dagere.kopeme.kieker;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.dagere.kopeme.datastorage.FolderProvider;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
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
//		System.out.println("Initialisiere Kieker-Support");
		// AsyncFsWriter fsWriter2 = AsyncFsWriter.
		ChangeableFolderWriter fsWriter = ChangeableFolderWriter.getInstance(MonitoringController.getInstance());
		if (fsWriter == null) {
			if (useIt) {
				System.err.println("Kieker is not used, although specified. The " + ChangeableFolderWriter.class.getCanonicalName() + " has to be used!");
			}
		} else {
			LOG.info("Initializing KoPeMe-Kieker-Support");
			IMonitoringController kiekerController = fsWriter.getController();
			if (useIt) {
				// fsWriter.getWriter().
				File folderForCurrentPerformanceResult = fp.getFolderForCurrentPerformanceresults(testClassName, testCaseName);
				folderForCurrentPerformanceResult.mkdirs();
				fsWriter.setFolder(folderForCurrentPerformanceResult);
				
				kiekerController.enableMonitoring();
			} else {
				if (kiekerController.isMonitoringEnabled()) {
					kiekerController.disableMonitoring();
				}
			}
		}
	}

}
