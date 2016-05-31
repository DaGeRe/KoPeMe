package de.dagere.kopeme.instrumentation;

import kieker.common.logging.Log;
import kieker.common.logging.LogFactory;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.registry.ControlFlowRegistry;
import kieker.monitoring.core.registry.SessionRegistry;
import kieker.monitoring.probe.IMonitoringProbe;
import kieker.monitoring.probe.aspectj.operationExecution.AbstractOperationExecutionAspect;
import kieker.monitoring.timer.ITimeSource;

/**
 * Instances of this class will be used for instrumenting purposes, 
 * making able to save {@link OperationExecutionRecord}s in the kieker framework.
 * The code for the methods {@link #measureBefore} and {@link #measureAfter} was taken from the class {@link AbstractOperationExecutionAspect}. 
 * 
 * @author dhaeb
 *
 */
public class KiekerMeasureUtil implements IMonitoringProbe {

	/**
	 * Used to get meta information about the current stack ({@link StackTraceElement}).
	 * 
	 * @see  {@code http://stackoverflow.com/questions/115008/how-can-we-print-line-numbers-to-the-log-in-java}
	 * 
	 * @param level which element of the current stack should be returned? <br/>
	 * 		  0 => representing {@code Thread.currentThread().getStackTrace()} <br/>
	 * 		  1 => representing {@code KiekerMeasureUtil.lineOut()}<br/>
	 * 		  2 => representing The position where you called this method. If you indirecting yourself, you should take care of this by yourself and use an higher value for level.<br/>
	 * 
	 * 
	 * @return The stacktrace element denoted by level, 0 most current, higher values representing deeper stack entries
	 */
	public static StackTraceElement lineOut(final int level) { 
	    StackTraceElement[] traces = Thread.currentThread().getStackTrace();
	    return traces[level];
	}
	
	private static final Log LOG = LogFactory.getLog(AbstractOperationExecutionAspect.class);

	static final IMonitoringController CTRLINST = MonitoringController.getInstance();
	
	private static final ITimeSource TIME = CTRLINST.getTimeSource();
	private static final String VMNAME = CTRLINST.getHostname();
	private static final ControlFlowRegistry CFREGISTRY = ControlFlowRegistry.INSTANCE;
	private static final SessionRegistry SESSIONREGISTRY = SessionRegistry.INSTANCE;
	
	String signature;
	
	private boolean entrypoint;
	private String hostname;
	private String sessionId;
	private long tin;
	private long traceId;
	private int ess;
	private int eoi;

	/**
	 * Constructor which captures the stack to save the name of the surrounding called method.  
	 */
	public KiekerMeasureUtil() {
		StackTraceElement stackOfCaller = lineOut(3);
		signature = stackOfCaller.toString(); 
	}
	
	/**
	 * Will be called to register the time when the method has started.
	 */
	public void measureBefore() {
		if (!CTRLINST.isMonitoringEnabled()) {
			return;
		}
		hostname = VMNAME;
		sessionId = SESSIONREGISTRY.recallThreadLocalSessionId();
		traceId = CFREGISTRY.recallThreadLocalTraceId();
																// entry point
		if (traceId == -1) {
			entrypoint = true;
			traceId = CFREGISTRY.getAndStoreUniqueThreadLocalTraceId();
			CFREGISTRY.storeThreadLocalEOI(0);
			CFREGISTRY.storeThreadLocalESS(1); // next operation is ess + 1
			eoi = 0;
			ess = 0;
		} else {
			entrypoint = false;
			eoi = CFREGISTRY.incrementAndRecallThreadLocalEOI(); // ess > 1
			ess = CFREGISTRY.recallAndIncrementThreadLocalESS(); // ess >= 0
			if ((eoi == -1) || (ess == -1)) {
				LOG.error("eoi and/or ess have invalid values:" + " eoi == "
						+ eoi + " ess == " + ess);
				CTRLINST.terminateMonitoring();
			}
		}
		tin = TIME.getTime();
	}

	/**
	 * Denotes that the method call has finished successfully, 
	 * finishing the {@link OperationExecutionRecord} and saving it to Kieker.
	 * 
	 * @return The recoreded value, mainly for testing purposes
	 */
	public OperationExecutionRecord measureAfter() {
		// measure after
		final long tout = TIME.getTime();
		OperationExecutionRecord record = new OperationExecutionRecord(signature, sessionId, traceId, tin, tout, hostname, eoi, ess);
		CTRLINST.newMonitoringRecord(record);
		// cleanup
		if (entrypoint) {
			CFREGISTRY.unsetThreadLocalTraceId();
			CFREGISTRY.unsetThreadLocalEOI();
			CFREGISTRY.unsetThreadLocalESS();
		} else {
			CFREGISTRY.storeThreadLocalESS(ess); // next operation is ess
		}
		return record;
	}
}
