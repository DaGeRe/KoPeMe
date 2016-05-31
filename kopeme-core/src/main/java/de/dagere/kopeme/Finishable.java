package de.dagere.kopeme;

/**
 * Represents an runnable, that also has a finished state which indicates that the thread should finish. This is an extension which should be used in cases where the interrupt state may be cleared in
 * between, but the thread should still finish.
 * 
 * @author reichelt
 *
 */
public interface Finishable extends Runnable {
	/**
	 * Gets the finish-state.
	 * @return
	 */
	public boolean isFinished();

	/**
	 * Sets the finish-state.
	 * @param isFinished
	 */
	public void setFinished(final boolean isFinished);
}
