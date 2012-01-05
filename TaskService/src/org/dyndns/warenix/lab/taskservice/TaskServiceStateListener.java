package org.dyndns.warenix.lab.taskservice;

public interface TaskServiceStateListener {
	public void onBackgroundTaskAdded(BackgroundTask task);

	public void onBackgroundTaskRemoved(BackgroundTask task);

	public void onBackgroundTaskExecuted(BackgroundTask task);

	public void onQueueSizeChanged(int newQueueSize);
}
