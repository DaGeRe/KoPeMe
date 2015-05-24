package de.dagere.kopeme.kieker;

import java.io.File;

import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.writer.filesystem.ChangeableFolderSyncFsWriter;
import de.dagere.kopeme.datastorage.FolderProvider;

public enum KoPeMeKiekerSupport {
	INSTANCE;
	
	private final FolderProvider fp;
	
	private KoPeMeKiekerSupport() {
		fp = FolderProvider.getInstance();
	}
	
	
	public void useKieker(boolean useIt, String testClassName) throws Exception{
		ChangeableFolderSyncFsWriter fsWriter = ChangeableFolderSyncFsWriter.getInstance();
		if(fsWriter == null) {
			if(useIt){
				System.err.println("Kieker is not used, although specified. The " + ChangeableFolderSyncFsWriter.class.getCanonicalName() + " has to be used!");
			}
		} else {
			IMonitoringController kiekerController = fsWriter.getController();
			if(useIt){
				File folderForCurrentPerformanceResult = fp.getFolderForCurrentPerformanceresults(testClassName);
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
