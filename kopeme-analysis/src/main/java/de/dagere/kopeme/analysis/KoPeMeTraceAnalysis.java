package de.dagere.kopeme.analysis;

import kieker.analysis.AnalysisController;
import kieker.analysis.exception.AnalysisConfigurationException;
import kieker.analysis.plugin.reader.filesystem.FSReader;
import kieker.common.configuration.Configuration;
import kieker.tools.traceAnalysis.filter.AbstractTraceAnalysisFilter;
import kieker.tools.traceAnalysis.filter.executionRecordTransformation.ExecutionRecordTransformationFilter;
import kieker.tools.traceAnalysis.filter.sessionReconstruction.SessionReconstructionFilter;
import kieker.tools.traceAnalysis.filter.traceReconstruction.TraceReconstructionFilter;
import kieker.tools.traceAnalysis.systemModel.repository.SystemModelRepository;

public class KoPeMeTraceAnalysis {
	public static void main(final String[] args) throws IllegalStateException, AnalysisConfigurationException {
		final AnalysisController analysisController = new AnalysisController();

		// Initialize and register the list reader
		Configuration fsReaderConfig = new Configuration();
		fsReaderConfig.setProperty(FSReader.CONFIG_PROPERTY_NAME_INPUTDIRS, args[0]);
		final FSReader reader = new FSReader(fsReaderConfig, analysisController);

		// Initialize and register the system model repository
		final SystemModelRepository systemModelRepository = new SystemModelRepository(new Configuration(), analysisController);

		// final EventRecordTraceReconstructionFilter traceReconstructer = new EventRecordTraceReconstructionFilter(new Configuration(), analysisController);
		// analysisController.connect(reader, FSReader.OUTPUT_PORT_NAME_RECORDS,
		// traceReconstructer, EventRecordTraceReconstructionFilter.INPUT_PORT_NAME_TRACE_RECORDS);
		//
		// final TraceEventRecords2ExecutionAndMessageTraceFilter transformerFilter = new TraceEventRecords2ExecutionAndMessageTraceFilter(new Configuration(),
		// analysisController);
		// analysisController.connect(transformerFilter,
		// AbstractTraceAnalysisFilter.REPOSITORY_PORT_NAME_SYSTEM_MODEL, systemModelRepository);
		// analysisController.connect(traceReconstructer, EventRecordTraceReconstructionFilter.OUTPUT_PORT_NAME_TRACE_VALID,
		// transformerFilter, TraceEventRecords2ExecutionAndMessageTraceFilter.INPUT_PORT_NAME_EVENT_TRACE);

		// Initialize, register and connect the execution record transformation filter
		final ExecutionRecordTransformationFilter executionRecordTransformationFilter = new ExecutionRecordTransformationFilter(new Configuration(),
				analysisController);
		analysisController.connect(executionRecordTransformationFilter,
				AbstractTraceAnalysisFilter.REPOSITORY_PORT_NAME_SYSTEM_MODEL, systemModelRepository);
		analysisController.connect(reader, FSReader.OUTPUT_PORT_NAME_RECORDS,
				executionRecordTransformationFilter, ExecutionRecordTransformationFilter.INPUT_PORT_NAME_RECORDS);

		// Initialize, register and connect the trace reconstruction filter
		final TraceReconstructionFilter traceReconstructionFilter = new TraceReconstructionFilter(new Configuration(), analysisController);
		analysisController.connect(traceReconstructionFilter,
				AbstractTraceAnalysisFilter.REPOSITORY_PORT_NAME_SYSTEM_MODEL, systemModelRepository);
		analysisController.connect(executionRecordTransformationFilter, ExecutionRecordTransformationFilter.OUTPUT_PORT_NAME_EXECUTIONS,
				traceReconstructionFilter, TraceReconstructionFilter.INPUT_PORT_NAME_EXECUTIONS);

		// Initialize, register and connect the session reconstruction filter
		final Configuration bareSessionReconstructionFilterConfiguration = new Configuration();
		bareSessionReconstructionFilterConfiguration.setProperty(SessionReconstructionFilter.CONFIG_PROPERTY_NAME_MAX_THINK_TIME,
				SessionReconstructionFilter.CONFIG_PROPERTY_VALUE_MAX_THINK_TIME);

		// Trace objects now provided by the traceReconstructionFilter's output ports (TraceReconstructionFilter.OUTPUT_PORT_NAME_{MESSAGE|EXECUTION}_TRACE).

		// TODO: Instead of using the TeeFilter, define your own filter that reads the traces.
		final KoPeMeFilter teeFilter = new KoPeMeFilter(new Configuration(), analysisController);
		analysisController.connect(traceReconstructionFilter, TraceReconstructionFilter.OUTPUT_PORT_NAME_EXECUTION_TRACE,
				teeFilter, KoPeMeFilter.INPUT_EXECUTION_TRACE);

		analysisController.run();
	}
}
