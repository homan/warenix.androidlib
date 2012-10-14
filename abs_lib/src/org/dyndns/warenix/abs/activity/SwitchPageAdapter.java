package org.dyndns.warenix.abs.activity;

import org.dyndns.warenix.abs.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SwitchPageAdapter extends ArrayAdapter<String> {

	private String mThreadTitle = "";

	private int mSelectedPosition = 0;

	public SwitchPageAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public void setTitle(String title) {
		mThreadTitle = title;
	}

	public void setPageCount(int pageCount) {
		mPageCount = pageCount;
	}

	int mPageCount;

	@Override
	public int getCount() {
		return mPageCount;
	}

	@Override
	public String getItem(int position) {
		return super.getItem(position);
	}

	// return views of drop down items
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View v = getView(position, convertView, parent);
		// if (v.getBackground() == null) {
		// v.setBackgroundColor(Color.parseColor("#00ff00"));
		// } else {
		// v.getBackground().setColorFilter(Color.parseColor("#00ff00"),
		// PorterDuff.Mode.DARKEN);
		// }
		return v;
	}

	// return header view of drop down
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String siteLink = position == 0 ? mThreadTitle : "\t\t"
				+ (position + 1) + "\t";
		LayoutInflater inflater = (LayoutInflater) parent.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.switch_page_dropdown, null);
		TextView site = (TextView) view.findViewById(R.id.label);
		if (position == mSelectedPosition) {
			site.setTextColor(Color.YELLOW);
		} else {
			site.setTextColor(Color.WHITE);
		}
		site.setText(siteLink);

		// TODO mark visited
		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		boolean visited = position == 0;
		if (!visited) {
			icon.setImageResource(R.drawable.rectangle);
		}

		return view;
	}

	public void setSelectedPosition(int selectedItemIndex) {
		mSelectedPosition = selectedItemIndex;
	}	
}
