package de.dagere.kopeme.junit.testrunner;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

/**
 * A Listener for a run that always delegates the event to another RunNotifier
 * 
 * @author reichelt
 *
 */
class DelegatingRunListener extends RunListener {
	private final RunNotifier otherNotifier;

	public DelegatingRunListener(RunNotifier notifier) {
		this.otherNotifier = notifier;
	}

	@Override
	public void testRunStarted(Description description) throws Exception {
		otherNotifier.fireTestRunStarted(description);
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		otherNotifier.fireTestRunFinished(result);
	}

	@Override
	public void testStarted(Description description) throws Exception {
		otherNotifier.fireTestStarted(description);
	}

	@Override
	public void testFinished(Description description) throws Exception {
		otherNotifier.fireTestFinished(description);
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		otherNotifier.fireTestFailure(failure);
	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		otherNotifier.fireTestAssumptionFailed(failure);
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		otherNotifier.fireTestIgnored(description);
	}
}