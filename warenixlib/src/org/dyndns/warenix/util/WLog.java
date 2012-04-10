package org.dyndns.warenix.util;

import android.util.Log;

public class WLog {

	private static String APP_NAME = "";

	public static void d(String TAG, String message) {
		Log.d(TAG, getAppName() + ":" + message);
	}

	public static void i(String TAG, String message) {
		Log.i(TAG, getAppName() + ":" + message);
	}

	private static String getAppName() {
		if (APP_NAME == null) {
			throw new IllegalStateException(
					"Please call WLog.setAppName() before");
		}
		return APP_NAME;
	}

	public static void setAppName(String appName) {
		APP_NAME = appName;
	}

}
