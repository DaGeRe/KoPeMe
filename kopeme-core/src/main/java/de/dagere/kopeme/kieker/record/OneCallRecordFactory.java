package de.dagere.kopeme.kieker.record;

import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.factory.IRecordFactory;
import kieker.common.record.io.IValueDeserializer;

public class OneCallRecordFactory implements IRecordFactory<DurationRecord>{

   @Override
   public DurationRecord create(final IValueDeserializer deserializer) throws RecordInstantiationException {
      return new DurationRecord(deserializer);
   }

   @Override
   public String[] getValueNames() {
      return OneCallRecord.VALUE_NAMES; // NOPMD
   }

   @Override
   public Class<?>[] getValueTypes() {
      return OneCallRecord.TYPES; // NOPMD
   }

   @Override
   public int getRecordSizeInBytes() {
      return OneCallRecord.SIZE;
   }

}
