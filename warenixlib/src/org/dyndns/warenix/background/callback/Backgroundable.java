package org.dyndns.warenix.background.callback;

/**
 * Perform something in background
 * 
 */
public interface Backgroundable<E> {

	/**
	 * perform long running job in a thread
	 * 
	 * @return result
	 */
	public E doInBackground();

}
