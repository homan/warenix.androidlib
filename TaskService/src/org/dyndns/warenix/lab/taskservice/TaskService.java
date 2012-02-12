package org.dyndns.warenix.lab.taskservice;

import java.util.concurrent.LinkedBlockingQueue;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class TaskService extends IntentService {

	static final String LOG_TAG = "taskservice";

	static LinkedBlockingQueue<BackgroundTask> mQueue = new LinkedBlockingQueue<BackgroundTask>();

	static Thread mWorkerThread;

	static boolean mRunning;

	static TaskServiceStateListener mStateListener;

	public TaskService() {
		this("taskservice");
	}

	public TaskService(String name) {
		super(name);

		setRunning(true);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(LOG_TAG, "onHandleIntent");
		Bundle bundle = intent.getExtras();
		BackgroundTask task = (BackgroundTask) bundle.get("task");
		if (task != null) {
			try {
				mQueue.put(task);
				Log.d(LOG_TAG, getQueuSize() + " background task queued");

				if (mStateListener != null) {
					mStateListener.onQueueSizeChanged(getQueuSize());
					mStateListener.onBackgroundTaskAdded(task);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static int getQueuSize() {
		return mQueue.size();
	}

	public static void setRunning(boolean newRunning) {
		if (newRunning) {
			if (mWorkerThread == null || !mWorkerThread.isAlive()) {
				newWorkerThread();
				mWorkerThread.start();
			}
		}
		mRunning = newRunning;
	}

	static void newWorkerThread() {
		mWorkerThread = new Thread() {
			public void run() {

				BackgroundTask task = null;

				while (mRunning) {
					Log.d(LOG_TAG, "running");
					try {
						task = mQueue.take();
						Log.d("workerthread",
								String.format(
										"take one task [%s] from queue remaining size %d",
										task.toString(), getQueuSize()));

						if (mStateListener != null) {
							mStateListener.onQueueSizeChanged(getQueuSize());
							mStateListener.onBackgroundTaskExecuted(task);
						}

						Object result = task.onExecute();
						mStateListener.onBackgroundTaskRemoved(task);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				Log.d("workerthread", "workerThread stop");
			}
		};
	}

	public static void setStateListener(TaskServiceStateListener stateListener) {
		mStateListener = stateListener;
	}

	public static boolean isRunning() {
		return mRunning;
	}

	public static void addBackgroundTask(Context context, BackgroundTask task) {
		Intent intent = new Intent(context, TaskService.class);
		intent.putExtra("task", task);
		context.startService(intent);
	}

}
