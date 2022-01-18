package de.dagere.kopeme.kieker.record;

import java.nio.BufferOverflowException;

import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.io.IValueDeserializer;
import kieker.common.record.io.IValueSerializer;

public class OneCallRecord extends AbstractMonitoringRecord {
   /** Descriptive definition of the serialization size of the record. */
   public static final int SIZE = TYPE_SIZE_STRING; // OneCallRecord.operationSignature

   public static final Class<?>[] TYPES = {
         String.class // OperationExecutionRecord.operationSignature
   };

   /** user-defined constants. */
   public static final String NO_OPERATION_SIGNATURE = "noOperation";
   public static final long NO_TIMESTAMP = -1L;
   /** property name array. */
   public static final String[] VALUE_NAMES = {
         "operationSignature",
         "tin",
         "tout",
   };

   /** default constants. */
   public static final String OPERATION_SIGNATURE = NO_OPERATION_SIGNATURE;
   private static final long serialVersionUID = -7768272829642950711L;

   /** property declarations. */
   private final String operationSignature;

   /**
    * Creates a new instance of this class using the given parameters.
    * 
    * @param operationSignature operationSignature
    * @param tin tin
    * @param tout tout
    */
   public OneCallRecord(final String operationSignature) {
      this.operationSignature = operationSignature == null ? NO_OPERATION_SIGNATURE : operationSignature;
   }

   /**
    * @param deserializer The deserializer to use
    * @throws RecordInstantiationException when the record could not be deserialized
    */
   public OneCallRecord(final IValueDeserializer deserializer) throws RecordInstantiationException {
      this.operationSignature = deserializer.getString();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void serialize(final IValueSerializer serializer) throws BufferOverflowException {
      serializer.putString(this.getOperationSignature());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<?>[] getValueTypes() {
      return TYPES; // NOPMD
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String[] getValueNames() {
      return VALUE_NAMES; // NOPMD
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getSize() {
      return SIZE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(final Object obj) {
      if (obj == null) {
         return false;
      }
      if (obj == this) {
         return true;
      }
      if (obj.getClass() != this.getClass()) {
         return false;
      }

      final OneCallRecord castedRecord = (OneCallRecord) obj;
      if (this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) {
         return false;
      }
      if (!this.getOperationSignature().equals(castedRecord.getOperationSignature())) {
         return false;
      }
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      int code = 0;
      code += this.getOperationSignature().hashCode();

      return code;
   }

   public final String getOperationSignature() {
      return this.operationSignature;
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      String result = "OperationExecutionRecord: ";
      result += "operationSignature = ";
      result += this.getOperationSignature() + ", ";

      return result;
   }
}