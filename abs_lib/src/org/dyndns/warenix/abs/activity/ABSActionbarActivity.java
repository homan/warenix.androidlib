package org.dyndns.warenix.abs.activity;

import org.dyndns.warenix.abs.BaseActionBarActivity;
import org.dyndns.warenix.abs.R;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.actionbarsherlock.view.Menu;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivityBase;
import com.slidingmenu.lib.app.SlidingActivityHelper;

public class ABSActionbarActivity extends BaseActionBarActivity implements
		SlidingActivityBase {

	SwitchPageAdapter mAdapter;
	private SlidingActivityHelper mHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHelper = new SlidingActivityHelper(this);
		mHelper.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		//
		// setTitle("");
		// setSwitchThreadPageAdapter(10);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate(savedInstanceState);
	}

	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return mHelper.findViewById(id);
	}

	@Override
	public void setContentView(int id) {
		setContentView(getLayoutInflater().inflate(id, null));
	}

	@Override
	public void setContentView(View v) {
		setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	@Override
	public void setContentView(View v, LayoutParams params) {
		super.setContentView(v, params);
		mHelper.registerAboveContentView(v, params);
	}

	public void setBehindContentView(int id) {
		setBehindContentView(getLayoutInflater().inflate(id, null));
	}

	public void setBehindContentView(View v) {
		setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	public void setBehindContentView(View v, LayoutParams params) {
		mHelper.setBehindContentView(v, params);
	}

	public SlidingMenu getSlidingMenu() {
		return mHelper.getSlidingMenu();
	}

	public void toggle() {
		mHelper.toggle();
	}

	public void showAbove() {
		mHelper.showAbove();
	}

	public void showBehind() {
		mHelper.showBehind();
	}

	public void setSlidingActionBarEnabled(boolean b) {
		mHelper.setSlidingActionBarEnabled(b);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean b = mHelper.onKeyUp(keyCode, event);
		if (b)
			return b;
		return super.onKeyUp(keyCode, event);
	}

	public void setSwitchThreadPageAdapter(String title, int pageCount,
			int selectedItemIndex) {
		Context context = getSupportActionBar().getThemedContext();

		// int selectedItemIndex = 0;
		if (mAdapter == null) {
			mAdapter = new SwitchPageAdapter(context,
					R.layout.switch_page_dropdown);
		} else {
			// selectedItemIndex = getSupportActionBar()
			// .getSelectedNavigationIndex();
			// if (selectedItemIndex == -1) {
			// selectedItemIndex = 0;
			// }
		}
		mAdapter.setTitle(title);
		mAdapter.setPageCount(pageCount);
		mAdapter.setSelectedPosition(selectedItemIndex);
		setActionBarList(mAdapter, selectedItemIndex);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Used to put dark icons on light action bar
		//
		// menu.add("留明").setIcon(android.R.drawable.ic_input_add)
		// .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		// //
		// // menu.add("Refresh")
		// // .setIcon(android.R.drawable.ic_menu_recent_history)
		// // .setShowAsAction(
		// // MenuItem.SHOW_AS_ACTION_IF_ROOM
		// // | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		//
		// SubMenu subMenu = menu.addSubMenu("More");
		//
		// subMenu.add("分享").setIcon(android.R.drawable.ic_menu_share);
		// // subMenu.add("除明").setIcon(android.R.drawable.ic_input_delete);
		//
		// MenuItem subMenu1Item = subMenu.getItem();
		// subMenu1Item.setIcon(android.R.drawable.ic_menu_more);
		// subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}
}
