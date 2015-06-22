package de.dagere.kopeme.kieker;

import java.io.File;

import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.writer.filesystem.ChangeableFolderSyncFsWriter;
import de.dagere.kopeme.datastorage.FolderProvider;

/**
 * Class to control kieker tracing for KoPeMe.
 * 
 * @author dhaeb
 *
 */
public enum KoPeMeKiekerSupport {
	INSTANCE;

	private final FolderProvider fp;

	private KoPeMeKiekerSupport() {
		fp = FolderProvider.getInstance();
	}

	public void useKieker(final boolean useIt, final String testClassName, final String testCaseName) throws Exception {
		// AsyncFsWriter fsWriter2 = AsyncFsWriter.
		ChangeableFolderSyncFsWriter fsWriter = ChangeableFolderSyncFsWriter.getInstance(MonitoringController.getInstance());
		if (fsWriter == null) {
			if (useIt) {
				System.err.println("Kieker is not used, although specified. The " + ChangeableFolderSyncFsWriter.class.getCanonicalName() + " has to be used!");
			}
		} else {
			IMonitoringController kiekerController = fsWriter.getController();
			if (useIt) {
				// fsWriter.getWriter().
				File folderForCurrentPerformanceResult = fp.getFolderForCurrentPerformanceresults(testClassName, testCaseName);
				folderForCurrentPerformanceResult.mkdirs();
				fsWriter.setFolder(folderForCurrentPerformanceResult);

				kiekerController.enableMonitoring();
			} else {
				kiekerController.disableMonitoring();
				fsWriter.reset();
			}

		}
	}

}
