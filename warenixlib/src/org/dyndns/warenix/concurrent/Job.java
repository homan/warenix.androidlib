package org.dyndns.warenix.concurrent;

public interface Job {

	public void consume();

	public void onConsumed();

}
