package org.dyndns.warenix.concurrent;

import java.util.concurrent.BlockingQueue;

/** Inner class representing the Consumer side */
public class Consumer implements Runnable {
	public static boolean done;
	protected BlockingQueue<Job> queue;
	JobFactory factory;
	int id;

	public Consumer(JobFactory factory, BlockingQueue<Job> theQueue,
			int consumerId) {
		this.queue = theQueue;
		this.factory = factory;
		this.id = consumerId;
	}

	Job currentJob;

	public void run() {
		while (true) {
			try {

				currentJob = queue.take();
				int len = queue.size();
				System.out.println("List size now " + len);

				factory.onConsumerChangeState(Consumer.this, currentJob, true);
				currentJob.consume();
				currentJob.onConsumed();
				factory.onConsumerChangeState(Consumer.this, currentJob, false);

				if (done) {
					return;
				}

			} catch (InterruptedException ex) {
				System.out.println("#" + id + " CONSUMER INTERRUPTED");
				factory.onConsumerChangeState(Consumer.this, currentJob, false);
			}
		}
	}
}