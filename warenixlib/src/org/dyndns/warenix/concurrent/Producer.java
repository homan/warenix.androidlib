package org.dyndns.warenix.concurrent;

import java.util.concurrent.BlockingQueue;

public abstract class Producer implements Runnable {

	public static boolean done;
	protected BlockingQueue<Job> queue;

	public Producer(BlockingQueue<Job> theQueue) {
		this.queue = theQueue;
	}

	@Override
	public void run() {
		try {
			Job job = produce();
			queue.put(job);
			System.out.println("Produced 1 Job; List size now " + queue.size());

		} catch (InterruptedException ex) {
			System.out.println("Producer INTERRUPTED");
		}
	}

	public abstract Job produce();

}