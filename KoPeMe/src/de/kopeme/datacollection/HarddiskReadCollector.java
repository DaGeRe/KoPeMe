package de.kopeme.datacollection;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class HarddiskReadCollector extends DataCollector{
	private long readsStart, diffReads;

	public HarddiskReadCollector() {
		sigar = new Sigar();
	}

	@Override
	public void startCollection() {
		getValue();
//		sigar.enableLogging(true);
		readsStart = getReads();
	}

	@Override
	public void stopCollection() {
//		sigar.enableLogging(false);
		diffReads = readsStart - getReads();
	}
	
	private long getReads()
	{
		long reads = 0;
		try {
			for (FileSystem fs : sigar.getFileSystemList()) {
				if ( fs.getType() == FileSystem.TYPE_LOCAL_DISK )
				{
					FileSystemUsage fsu = sigar.getFileSystemUsage(fs.getDevName());
//					System.out.println("FSU: " + fsu.getDiskWriteBytes() + " " + fsu.getDiskReadBytes());
					reads += fsu.getDiskReadBytes();
				}
				
			}
		} catch (SigarException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
		return reads;
	}

	@Override
	public long getValue() {
		return diffReads;
	}

	@Override
	public int getPriority() {
		// TODO Automatisch generierter Methodenstub
		return 0;
	}
}
