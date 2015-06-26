package de.dagere.kopeme.datacollection;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A collector for the CPU usage during a test.
 * 
 * @author reichelt
 *
 */
public final class CPUUsageCollector extends DataCollector {

	private static final int MULTIPLICATOR_FOR_READABILITY = 1000;

	private static final Logger LOG = LogManager.getLogger(CPUUsageCollector.class);

	private long startTimeCpu = 0, stopTimeCpu = 0;
	private long startTimeUser = 0, stopTimeUser = 0;
	private long startTime = 0, stopTime = 0;
	private ThreadMXBean mxb;

	@Override
	public int getPriority() {
		return LOW_DATACOLLECTOR_PRIORITY;
	}

	@Override
	public void startCollection() {
		mxb = ManagementFactory.getThreadMXBean();
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
		LOG.trace("CPUTime: " + cpuTime + " Usertime: " + (stopTimeUser - startTimeUser));
		if (!mxb.isCurrentThreadCpuTimeSupported()) return -1;
		return (MULTIPLICATOR_FOR_READABILITY * cpuTime) / time;

	}
}
