package org.dyndns.warenix.abs.activity;

import org.dyndns.warenix.abs.BaseActionBarActivity;
import org.dyndns.warenix.abs.R;

import android.content.Context;
import android.os.Bundle;

import com.actionbarsherlock.view.Menu;

public class ABSActionbarActivity extends BaseActionBarActivity {

	SwitchPageAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_main);
		//
		// setTitle("");
		// setSwitchThreadPageAdapter(10);
	}

	public void setSwitchThreadPageAdapter(String title, int pageCount, int selectedItemIndex) {
		Context context = getSupportActionBar().getThemedContext();

		//int selectedItemIndex = 0;
		if (mAdapter == null) {
			mAdapter = new SwitchPageAdapter(context,
					R.layout.switch_page_dropdown);
		} else {
//			selectedItemIndex = getSupportActionBar()
//					.getSelectedNavigationIndex();
//			if (selectedItemIndex == -1) {
//				selectedItemIndex = 0;
//			}
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
