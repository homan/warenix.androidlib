package org.dyndns.warenix.pattern.baseListView;

import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ListViewAdapter extends BaseAdapter implements AsyncRefreshable {
	protected Context context;
	protected ArrayList<ListViewItem> itemList;
	protected ListView listView;

	public ListViewAdapter(Context context, ListView listView) {
		super();
		this.context = context;
		this.listView = listView;
		itemList = new ArrayList<ListViewItem>();

	}

	// ++BaseAdapter

	@Override
	public int getCount() {
		if (itemList != null) {
			return itemList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (itemList != null) {
			return itemList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListViewItem item = itemList.get(position);
		Log.d("warenix", String.format("get item %d view", position));
		int type = getItemViewType(position);
		View view = item.getView(context, position, convertView, parent, type);

		// TranslateAnimation animation = new TranslateAnimation(
		// Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
		// Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, -150.0f);
		// animation.setDuration(200);
		// view.startAnimation(animation);

		return view;
	}

	// --BaseAdapter

	protected void runNotifyDataSetInvalidated() {
		((Activity) context).runOnUiThread(new Runnable() {
			public void run() {
				notifyDataSetChanged();
			}
		});
	}

	public void clear() {
		itemList.clear();
	}

	@Override
	public void asyncRefresh() {

	}

	@Override
	public void cancelAsyncRefresh() {

	}

	public boolean isChildVisible(int position) {
		return position >= listView.getFirstVisiblePosition()
				&& position <= listView.getLastVisiblePosition();
	}

	/**
	 * subclass show override this method to dump the list view items to
	 * serializable
	 * 
	 * @return
	 */
	public Serializable getItemList() {
		return null;
	}

	/**
	 * subclass show override this method to recreate list view item from raw
	 * item list
	 * 
	 * @return
	 */
	public void setItemList(Serializable newItemList) {
	}
}
