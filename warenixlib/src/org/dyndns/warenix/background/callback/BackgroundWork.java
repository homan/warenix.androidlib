package org.dyndns.warenix.background.callback;

import android.util.Log;

/**
 * A background work which will execute a Backgroundable to obtaine result. The
 * result will be stored
 * 
 */
public class BackgroundWork<E> {

	private static final String TAG = "BackgroundWork";

	private Backgroundable<E> mWorker;
	private Thread mWorkerThread;
	private BackgroundWorkCallback mCallback;
	private E mResult;

	public BackgroundWork(Backgroundable<E> backgroundable) {
		mWorker = backgroundable;
	}

	public void registerCallback(BackgroundWorkCallback callback) {
		if (mCallback != callback) {
			mCallback = callback;

			if (mResult != null) {
				mCallback.onResult(mResult);
				Log.i(TAG, "send stored result to callback");
			}
		}
	}

	public void unregisterCallback() {
		mCallback = null;

		Log.i(TAG, "unregisterCallback");
	}

	/**
	 * start do in background and notify registered callback when finish
	 */
	public void start() {
		if (mWorkerThread == null) {
			mWorkerThread = new Thread() {
				public void run() {
					mResult = mWorker.doInBackground();
					if (mCallback != null) {
						mCallback.onResult(mResult);
						Log.i(TAG, "worker thread send result to callback");
					} else {
						Log.i(TAG,
								"worker thread finish and doesn't find any callback");
					}
				}
			};
			mWorkerThread.start();

			Log.i(TAG, "worker thread is created and started");
		} else {
			Log.i(TAG, "worker thread has started");
		}
	}

	/**
	 * cancel and cleanup everything
	 */
	public void destroy() {
		unregisterCallback();

		if (mWorkerThread == null && !mWorkerThread.isInterrupted()) {
			mWorkerThread.interrupt();
			mWorkerThread = null;
		}

		mResult = null;

		Log.i(TAG, "worker thread is destroyed");
	}
}
