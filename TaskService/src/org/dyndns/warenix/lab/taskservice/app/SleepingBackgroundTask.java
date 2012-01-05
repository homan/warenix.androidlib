package org.dyndns.warenix.lab.taskservice.app;

import java.util.Date;

import org.dyndns.warenix.lab.taskservice.BackgroundTask;

import android.util.Log;

@SuppressWarnings("serial")
public class SleepingBackgroundTask implements BackgroundTask {
	static final String LOG_TAG = "sleepingtask";

	static int mTaskId = 0;

	int mId;

	Date mStartDate;
	Date mEndDate;

	public SleepingBackgroundTask() {
		mId = mTaskId++;
	}

	@Override
	public Object onExecute() throws Exception {
		mStartDate = new java.util.Date();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
		}

		mEndDate = new java.util.Date();
		return null;
	}

	@Override
	public Object getResult() {
		Log.d(LOG_TAG, "completed at " + mEndDate.toLocaleString());
		return null;
	}

	@Override
	public String toString() {
		return "Sleeping Task #" + mId;
	}

}
