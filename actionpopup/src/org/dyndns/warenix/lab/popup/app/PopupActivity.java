package org.dyndns.warenix.lab.popup.app;

import org.dyndns.warenix.lab.popup.R;
import org.dyndns.warenix.widget.actionpopup.ActionPopup;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class PopupActivity extends Activity {
	private Button btnShowPopUp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		int[] buttons = new int[] { R.id.btnBottomLeft, R.id.btnBottomRight,
				R.id.btnTopLeft, R.id.btnTopRight, R.id.btnCenter };
		for (int buttonId : buttons) {
			btnShowPopUp = (Button) findViewById(buttonId);
			// btnShowPopUp.setOnClickListener(new ActionPopupListener());
			btnShowPopUp.setOnTouchListener(new ActionPopupListener());
		}
	}

	class ActionPopupListener implements View.OnClickListener,
			View.OnTouchListener {

		@Override
		public void onClick(View v) {
			Log.d("popup", "onClick");
			showActionPopup(v);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if (event.getAction() == MotionEvent.ACTION_UP) {
				Log.d("popup", "onTouch");
				int x = (int) event.getX();
				int y = (int) event.getY();
				Log.d("lab", String.format("touch %d, %d", x, y));
				showActionPopup(v, x, y);
			}
			return true;
		}
	}

	void showActionPopup(View anchor) {
		ActionPopup actionPopup = new ActionPopup(getLayoutInflater());
		actionPopup.showPopupInScreenCenter(anchor);
	}

	void showActionPopup(View anchor, int x, int y) {
		ActionPopup actionPopup = new ActionPopup(getLayoutInflater());
		actionPopup.addAction(this, "Reply", null);
		actionPopup.addAction(this, "Retweet", null);
		actionPopup.addAction(this, "Favourite", null);
		actionPopup.addAction(this, "Quote Tweet", null);
		actionPopup.showPopupAt(anchor, x, y);
	}

}
