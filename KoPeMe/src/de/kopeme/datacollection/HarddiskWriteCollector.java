package de.kopeme.datacollection;

import org.hyperic.sigar.*;

public class HarddiskWriteCollector extends DataCollector {

	private long writestart, diffWrites;

	public HarddiskWriteCollector() {
		sigar = new Sigar();
	}

	// @Override
	// public String getName() {
	// return "HarddiskDataCollector";
	// }

	@Override
	public void startCollection() {
		getValue();
//		sigar.enableLogging(true);
		writestart = getWrites();
		// du.
		// Runtime.getRuntime().
	}

	@Override
	public void stopCollection() {
//		sigar.enableLogging(false);
		diffWrites = writestart - getWrites();

	}
	
	private long getWrites()
	{
		long writes = 0;
		try {
			for (FileSystem fs : sigar.getFileSystemList()) {
//				System.out.println("FS: " + fs.getDevName());
				if ( fs.getType() == FileSystem.TYPE_LOCAL_DISK )
				{
					FileSystemUsage fsu = sigar.getFileSystemUsage(fs.getDevName());
//					System.out.println("FSU: " + fsu.getDiskWriteBytes() + " " + fsu.getDiskReadBytes());
					writes += fsu.getDiskWriteBytes();
				}
				
			}
		} catch (SigarException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
		return writes;
	}
	

	@Override
	public long getValue() {
//		long writes = getWrites();
//		
//		long diffWrites = writes - writestart;
//		long diffwrites2 = getWrites2() - writes2;
//		long kbWrites = diffWrites / 1024;
//		System.out.println("Writes: " + diffWrites + "(KB: " + kbWrites + ")");
//		System.out.println("Reads: " + diffReads);
//		System.out.println("Writes2: " + diffwrites2 + "(KB: " + (diffwrites2/1024)+")");
		
		return diffWrites;
	}

}
