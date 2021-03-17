package de.dagere.kopeme.kieker.writer;

import java.util.concurrent.ExecutionException;

import kieker.monitoring.core.controller.IMonitoringController;

public class Sample {
   
   public static IMonitoringController MONITORING_CONTROLLER;
   
	public void a() throws InterruptedException, ExecutionException {
		final long tin = MONITORING_CONTROLLER.getTimeSource().getTime();
		b();
		final long tout = MONITORING_CONTROLLER.getTimeSource().getTime();
		KiekerTestHelper.createAndWriteOperationExecutionRecord(tin, tout, "public void " + Sample.class.getName() + ".b()");
		Thread.sleep(2L);
	}

	private void b() throws InterruptedException {
		final long tin = MONITORING_CONTROLLER.getTimeSource().getTime();
		c();
		final long tout = MONITORING_CONTROLLER.getTimeSource().getTime();
		KiekerTestHelper.createAndWriteOperationExecutionRecord(tin, tout, "public void " + Sample.class.getName() + ".c()");
	}

	protected void c() throws InterruptedException {
		Thread.sleep(1L);
	}
}