package de.kopeme.datacollection;

import java.lang.management.ManagementFactory;
//import com.sun.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;

import javax.management.MBeanServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CPUUsageCollector extends DataCollector {
	
	private Logger log = LogManager.getLogger(CPUUsageCollector.class);
	
	private long startTimeCpu = 0, stopTimeCpu = 0;
//	private long startTimeCpu2 = 0, stopTimeCpu2 = 0;
	private long startTimeUser = 0, stopTimeUser = 0;
	private long startTime= 0, stopTime = 0;
	ThreadMXBean mxb;
//	OperatingSystemMXBean oxb;
	
	public int getPriority(){
		return 5;
	}
	
	@Override
	public void startCollection() {
		mxb = ManagementFactory.getThreadMXBean();
//		oxb = ManagementFactory.getOperatingSystemMXBean();
		
//		ManagementFactory.getMemoryManagerMXBeans().get(0).;
		startTimeCpu = mxb.getCurrentThreadCpuTime();
		startTimeUser = mxb.getCurrentThreadUserTime();
		startTime = System.nanoTime();
	}

	@Override
	public void stopCollection() {
		stopTimeCpu = mxb.getCurrentThreadCpuTime();
		stopTimeUser = mxb.getCurrentThreadUserTime();
		stopTime = System.nanoTime();
	}

	@Override
	public long getValue() {
		
		long cpuTime = stopTimeCpu - startTimeCpu;
		long time = stopTime - startTime;
		log.debug("CPUTime: " + cpuTime + " Usertime: " + (stopTimeUser - startTimeUser));
		if ( !mxb.isCurrentThreadCpuTimeSupported() ) return -1;
		return (1000 * cpuTime) / time;
		
	}

}
