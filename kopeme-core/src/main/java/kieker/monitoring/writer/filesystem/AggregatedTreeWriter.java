package kieker.monitoring.writer.filesystem;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.monitoring.writer.AbstractMonitoringWriter;

/**
 * This class creates a tree with statistical summaries from the data.
 * 
 * @author reichelt
 *
 */
public class AggregatedTreeWriter extends AbstractMonitoringWriter {

   public static final String PREFIX = AggregatedTreeWriter.class.getName() + ".";
   public static final String CONFIG_PATH = PREFIX + "customStoragePath";
   public static final String CONFIG_MAXENTRIESINFILE = PREFIX + "maxEntriesInFile";
   public static final String CONFIG_MAXLOGSIZE = PREFIX + "maxLogSize";
   public static final String REAL_WRITER = PREFIX + "realwriter";

   public static final String CONFIG_MAXLOGFILES = PREFIX + "maxLogFiles";
   public static final String CONFIG_FLUSH = PREFIX + "flush";
   public static final String CONFIG_BUFFER = PREFIX + "bufferSize";

   private static AggregatedTreeWriter instance;

   public static synchronized AggregatedTreeWriter getInstance() {
      return instance;
   }

   private static final Logger LOG = Logger.getLogger(AggregatedTreeWriter.class.getName());

//   private final Configuration configuration;

   public AggregatedTreeWriter(final Configuration configuration) {
      super(configuration);
      LOG.info("Init..");
//      this.configuration = configuration;
      instance = this;
   }


   @Override
   public void onStarting() {
      System.out.println("Initializing " + getClass());
   }
   
   private final Map<CallTreeNode, SummaryStatistics> nodeMap = new HashMap<>();
   
   @Override
   public void writeMonitoringRecord(final IMonitoringRecord record) {
      if (record instanceof OperationExecutionRecord) {
         final OperationExecutionRecord operation = (OperationExecutionRecord) record;
         final CallTreeNode node = new CallTreeNode(operation.getEoi(), operation.getEss(), operation.getOperationSignature());
         SummaryStatistics statistics = nodeMap.get(node);
         if (statistics == null) {
            statistics = new SummaryStatistics();
            nodeMap.put(node, statistics);
         }
         statistics.addValue(operation.getTin() - operation.getTout());
      }
      
   }

   @Override
   public void onTerminating() {
   }

}
