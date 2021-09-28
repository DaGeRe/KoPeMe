package de.dagere.kopeme.kieker.record;

import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.factory.IRecordFactory;
import kieker.common.record.io.IValueDeserializer;

public class ReducedOperationExecutionRecordFactory implements IRecordFactory<ReducedOperationExecutionRecord>{

   @Override
   public ReducedOperationExecutionRecord create(final IValueDeserializer deserializer) throws RecordInstantiationException {
      return new ReducedOperationExecutionRecord(deserializer);
   }

   @Override
   public String[] getValueNames() {
      return ReducedOperationExecutionRecord.VALUE_NAMES; // NOPMD
   }

   @Override
   public Class<?>[] getValueTypes() {
      return ReducedOperationExecutionRecord.TYPES; // NOPMD
   }

   @Override
   public int getRecordSizeInBytes() {
      return ReducedOperationExecutionRecord.SIZE;
   }

}
