package de.dagere.kopeme.analysis;

import java.util.LinkedList;
import java.util.List;

import kieker.analysis.IProjectContext;
import kieker.analysis.plugin.annotation.InputPort;
import kieker.analysis.plugin.annotation.Plugin;
import kieker.analysis.plugin.filter.AbstractFilterPlugin;
import kieker.common.configuration.Configuration;
import kieker.tools.traceAnalysis.systemModel.Execution;
import kieker.tools.traceAnalysis.systemModel.ExecutionTrace;

@Plugin(description = "A filter to transform KoPeMe-Traces")
public class KoPeMeFilter extends AbstractFilterPlugin {
	public static final String INPUT_EXECUTION_TRACE = "INPUT_EXECUTION_TRACE";

	public KoPeMeFilter(final Configuration configuration, final IProjectContext projectContext) {
		super(configuration, projectContext);
	}

	@Override
	public Configuration getCurrentConfiguration() {
		return super.configuration;
	}

	@InputPort(name = INPUT_EXECUTION_TRACE, eventTypes = { ExecutionTrace.class })
	public void handleInputs(final ExecutionTrace trace) {
		System.out.println("Trace: " + trace.getTraceId());

		final List<Execution> unifiedTrace = new LinkedList<>();
		boolean found = false;
		boolean foundTwice = false;

		for (Execution ex : trace.getTraceAsSortedExecutionSet()) {
			if ("evaluate".equals(ex.getOperation().getSignature().getName())
					&& "de.dagere.kopeme.junit.testrunner.PerformanceJUnitStatement".equals(ex.getOperation().getComponentType().getFullQualifiedName())) {
				System.out.println(ex.getOperation().getSignature().getName() + " " + ex.getOperation().getComponentType().getFullQualifiedName());
				System.out.println("Zeit: " + (ex.getTout() - ex.getTin()) / 10E6);
				if (!found) {
					found = true;
				} else {
					foundTwice = true;
				}
			}
			if (found) {
				if (!foundTwice) {
					unifiedTrace.add(ex);
				} else {

				}
			}
		}

	}

}
