package org.dyndns.warenix.pattern.baseListView;

import android.content.Context;
import android.widget.ListView;

public class AsyncListAdapter extends ListViewAdapter {
	private AsyncRefreshTask mAsyncRefreshTask;
	private AsyncRefreshListener mAsyncRefreshListener;
	private BackgroundLogic mBackgroundLogic;

	public AsyncListAdapter(Context context, ListView listView) {
		super(context, listView);
	}

	public void setAsyncRefreshListener(AsyncRefreshListener listener) {
		mAsyncRefreshListener = listener;
	}

	public void setBackgroundLogic(BackgroundLogic logic) {
		mBackgroundLogic = logic;
	}

	public void asyncRefresh() {
		listView.post(new Runnable() {

			@Override
			public void run() {
				itemList.clear();
				notifyDataSetChanged();
			}

		});

		cancelAsyncRefresh();
		mAsyncRefreshTask = new AsyncRefreshTask(mAsyncRefreshListener,
				mBackgroundLogic);
		mAsyncRefreshTask.execute();

	}

	public void cancelAsyncRefresh() {
		if (mAsyncRefreshTask != null && !mAsyncRefreshTask.isCancelled()) {
			mAsyncRefreshTask.cancel(true);
		}
	}

	private static class AsyncRefreshTask extends
			org.dyndns.warenix.util.AsyncTask<Void, Void, Void> {
		AsyncRefreshListener mAsyncRefreshListener;
		BackgroundLogic mBackgroundLogic;
		Object result;

		public AsyncRefreshTask(AsyncRefreshListener listener,
				BackgroundLogic logic) {
			mAsyncRefreshListener = listener;
			mBackgroundLogic = logic;
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (mAsyncRefreshListener != null) {
				mAsyncRefreshListener.onAysncRefreshStarted();
			}
			if (mBackgroundLogic != null) {
				result = mBackgroundLogic.doInBackground();
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			if (mAsyncRefreshListener != null) {
				mAsyncRefreshListener.onAysncRefreshEnded();
			}
			if (mBackgroundLogic != null) {
				mBackgroundLogic.onPostExecut(result);
			}
		}
	}

	public interface AsyncRefreshListener {
		/**
		 * notify listener when async refresh about to begin
		 */
		public void onAysncRefreshStarted();

		/**
		 * notify listener when async refresh ended
		 */
		public void onAysncRefreshEnded();

	}

	public interface BackgroundLogic {
		/**
		 * the real background logic
		 * 
		 * @return result from background logic
		 */
		public Object doInBackground();

		/**
		 * background job has done
		 * 
		 * @param result
		 *            result from doInBackground
		 */
		public void onPostExecut(Object result);
	}

}
