package org.dyndns.warenix.concurrent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JobFactory {
	BlockingQueue<Job> myQueue = new LinkedBlockingQueue<Job>();

	Thread[] consumerPool;

	boolean[] activeConsumerPool;

	public JobFactory() {

	}

	public JobFactory(int nC) {
		start(nC);
	}

	public BlockingQueue<Job> getQueue() {
		return myQueue;
	}

	public void start(int nC) {
		System.out.println("starting " + nC + " consumer threads");
		activeConsumerPool = new boolean[nC];
		consumerPool = new Thread[nC];
		for (int i = 0; i < nC; i++) {
			consumerPool[i] = new Thread(new Consumer(this, myQueue, i));
			consumerPool[i].start();

		}
	}

	public void stop() {
		for (int i = 0; i < consumerPool.length; i++) {
			consumerPool[i].interrupt();
		}

	}

	public void addProducer(Producer p) {
		Thread currentProducerThread = new Thread(p);
		currentProducerThread.start();
	}

	public void onConsumerChangeState(Consumer consumer, Job job,
			boolean isActive) {
		activeConsumerPool[consumer.id] = isActive;
		System.out.println(String.format("consumer #%d with job %s active? %s",
				consumer.id, job, isActive));
	}

	public void stopConsumer(int position) {
		int consumerId = position % consumerPool.length;
		if (activeConsumerPool[consumerId]) {
			activeConsumerPool[consumerId] = false;
			consumerPool[consumerId].interrupt();
		}
	}
}
