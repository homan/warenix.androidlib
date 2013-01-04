package org.dyndns.warenix.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;

@SuppressLint("NewApi")
public class AsyncTaskUtil {
	/**
	 * Execute async task immediately
	 * 
	 * @param asyncTask
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	public static void executeAsyncTask(AsyncTask asyncTask, Object... params) {
		if (asyncTask != null
				&& asyncTask.getStatus() == AsyncTask.Status.PENDING)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				asyncTask.execute(params);
			}
	}

}
