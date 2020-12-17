/***************************************************************************
 * Copyright 2020 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/
package kieker.common.record.controlflow;

import java.nio.BufferOverflowException;

import kieker.common.exception.RecordInstantiationException;
import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.io.IValueDeserializer;
import kieker.common.record.io.IValueSerializer;


/**
 * @author Andre van Hoorn, Jan Waller
 * API compatibility: Kieker 1.15.0
 * 
 * @since 0.91
 */
public class ReducedOperationExecutionRecord extends AbstractMonitoringRecord  {			
	/** Descriptive definition of the serialization size of the record. */
	public static final int SIZE = TYPE_SIZE_STRING // OperationExecutionRecord.operationSignature
			 + TYPE_SIZE_LONG // OperationExecutionRecord.tin
			 + TYPE_SIZE_LONG; // OperationExecutionRecord.tout
	
	public static final Class<?>[] TYPES = {
		String.class, // OperationExecutionRecord.operationSignature
		long.class, // OperationExecutionRecord.tin
		long.class // OperationExecutionRecord.tout
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
	public static final long TIN = NO_TIMESTAMP;
	public static final long TOUT = NO_TIMESTAMP;
	private static final long serialVersionUID = -7768272829642950711L;
	
	/** property declarations. */
	private final String operationSignature;
	private final long tin;
	private final long tout;
	
	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param operationSignature
	 *            operationSignature
	 * @param sessionId
	 *            sessionId
	 * @param traceId
	 *            traceId
	 * @param tin
	 *            tin
	 * @param tout
	 *            tout
	 * @param hostname
	 *            hostname
	 * @param eoi
	 *            eoi
	 * @param ess
	 *            ess
	 */
	public ReducedOperationExecutionRecord(final String operationSignature, final long tin, final long tout) {
		this.operationSignature = operationSignature == null?NO_OPERATION_SIGNATURE:operationSignature;
		this.tin = tin;
		this.tout = tout;
	}


	/**
	 * @param deserializer
	 *            The deserializer to use
	 * @throws RecordInstantiationException 
	 *            when the record could not be deserialized
	 */
	public ReducedOperationExecutionRecord(final IValueDeserializer deserializer) throws RecordInstantiationException {
		this.operationSignature = deserializer.getString();
		this.tin = deserializer.getLong();
		this.tout = deserializer.getLong();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void serialize(final IValueSerializer serializer) throws BufferOverflowException {
		serializer.putString(this.getOperationSignature());
		serializer.putLong(this.getTin());
		serializer.putLong(this.getTout());
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
		
		final ReducedOperationExecutionRecord castedRecord = (ReducedOperationExecutionRecord) obj;
		if (this.getLoggingTimestamp() != castedRecord.getLoggingTimestamp()) {
			return false;
		}
		if (!this.getOperationSignature().equals(castedRecord.getOperationSignature())) {
			return false;
		}
		if (this.getTin() != castedRecord.getTin()) {
			return false;
		}
		if (this.getTout() != castedRecord.getTout()) {
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
		code += ((int)this.getTin());
		code += ((int)this.getTout());
		
		return code;
	}
	
	public final String getOperationSignature() {
		return this.operationSignature;
	}
	
	
	public final long getTin() {
		return this.tin;
	}
	
	
	public final long getTout() {
		return this.tout;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String result = "OperationExecutionRecord: ";
		result += "operationSignature = ";
		result += this.getOperationSignature() + ", ";
		
		result += "tin = ";
		result += this.getTin() + ", ";
		
		result += "tout = ";
		result += this.getTout() + ", ";
		
		return result;
	}
}
