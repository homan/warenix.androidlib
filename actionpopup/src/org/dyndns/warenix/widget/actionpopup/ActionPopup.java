package org.dyndns.warenix.widget.actionpopup;

import org.dyndns.warenix.lab.popup.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

public class ActionPopup {

	boolean isHorizontal;

	ViewGroup actionContainer;
	PopupWindow mpopup;

	View popUpView;

	public ActionPopup(LayoutInflater inflater) {
		this(inflater, true);
	}

	public ActionPopup(LayoutInflater inflater, boolean isHorizontal) {
		popUpView = inflatePopup(inflater);
		this.isHorizontal = isHorizontal;
	}

	public void showPopup(View anchor) {
		dismiss();

		Display display = ((WindowManager) anchor.getContext()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();

		Log.d("lab", String.format("screen width:%d height:%d", screenWidth,
				screenHeight));

		// create popupView

		Rect anchorRect = getViewRect(anchor);
		logRect(anchorRect);

		int titleBarHeight = getTitleBarHeight((Activity) anchor.getContext());

		popUpView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

		int popupWidth = popUpView.getMeasuredWidth();
		int popupHeight = popUpView.getMeasuredHeight();

		// determinet place position
		int roomOnTop = anchorRect.top - titleBarHeight;
		int roomOnBottom = screenHeight - anchorRect.bottom;

		boolean placeOnTop = roomOnTop > roomOnBottom;

		int popupY = 0;
		if (placeOnTop) {
			if (popupHeight <= roomOnTop) {
				// place full popup
				popupY = anchorRect.top - popupHeight;
			} else {
				// resize popup
				popupHeight = roomOnTop;
				popupY = titleBarHeight;
			}
		} else {
			// place below anchor
			if (popupHeight <= roomOnBottom) {
				popupY = anchorRect.bottom;
			} else {
				popupHeight = roomOnBottom;
				popupY = anchorRect.bottom;
			}
		}

		if (popupWidth > screenWidth) {
			popupWidth = screenWidth;
		}

		Rect popupRect = new Rect(0, 0, popupWidth, popupHeight);
		logRect(popupRect);

		Log.d("lab", String.format("popUpView original width:%d height:%d",
				popupWidth, popupHeight));

		Log.d("lab", String.format("popUpView adjusted width:%d height:%d",
				popupWidth, popupHeight));

		// positioning popup
		mpopup = new PopupWindow(popUpView);
		mpopup.setFocusable(true);

		mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
		mpopup.setBackgroundDrawable(new ColorDrawable(0xabcdef));
		mpopup.showAsDropDown(anchor);

		mpopup.setOutsideTouchable(true);
		mpopup.setTouchInterceptor(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}
				return false;
			}
		});

		int popupX = anchorRect.centerX() - popupWidth / 2;

		Log.d("lab", String.format(
				"update popup: x:%d y:%d width:%d height:%d", popupX, popupY,
				popupWidth, popupHeight));
		mpopup.update(popupX, popupY, popupWidth, popupHeight);
		// popup
	}

	public void showPopupInScreenCenter(View anchor) {
		dismiss();

		Display display = ((WindowManager) anchor.getContext()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();

		Log.d("lab", String.format("screen width:%d height:%d", screenWidth,
				screenHeight));

		// create popupView

		Rect anchorRect = getViewRect(anchor);
		logRect(anchorRect);

		int titleBarHeight = getTitleBarHeight((Activity) anchor.getContext());

		popUpView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

		int popupWidth = popUpView.getMeasuredWidth();
		int popupHeight = popUpView.getMeasuredHeight();

		if (popupWidth > screenWidth) {
			popupWidth = screenWidth;
		}
		if (popupHeight > screenHeight) {
			popupHeight = screenHeight - titleBarHeight;
		}

		Rect popupRect = new Rect(0, 0, popupWidth, popupHeight);
		logRect(popupRect);

		Log.d("lab", String.format("popUpView original width:%d height:%d",
				popupWidth, popupHeight));

		Log.d("lab", String.format("popUpView adjusted width:%d height:%d",
				popupWidth, popupHeight));

		// positioning popup
		mpopup = new PopupWindow(popUpView);
		mpopup.setFocusable(true);

		mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
		mpopup.setBackgroundDrawable(new ColorDrawable(0xabcdef));
		mpopup.showAsDropDown(anchor);

		mpopup.setOutsideTouchable(true);
		mpopup.setTouchInterceptor(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}
				return false;
			}
		});

		int popupX = (screenWidth - popupWidth) / 2;
		int popupY = (screenHeight - popupHeight) / 2;

		Log.d("lab", String.format(
				"update popup: x:%d y:%d width:%d height:%d", popupX, popupY,
				popupWidth, popupHeight));
		mpopup.update(popupX, popupY, popupWidth, popupHeight);
		// popup
	}

	public void showPopupAt(View anchor, int x, int y) {
		dismiss();

		Display display = ((WindowManager) anchor.getContext()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int screenWidth = display.getWidth();
		int screenHeight = display.getHeight();

		Log.d("lab", String.format("screen width:%d height:%d", screenWidth,
				screenHeight));

		// create popupView

		Rect anchorRect = getViewRect(anchor);
		logRect(anchorRect);

		int titleBarHeight = getTitleBarHeight((Activity) anchor.getContext());

		popUpView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

		int popupWidth = popUpView.getMeasuredWidth();
		int popupHeight = popUpView.getMeasuredHeight();

		int[] location = new int[2];

		anchor.getLocationOnScreen(location);
		Log.d("lab", String.format(String.format("getLocationOnScreen %d, %d",
				location[0], location[1])));
		anchorRect.top = location[1];
		anchorRect.bottom = location[1];

		// determinet place position
		int roomOnTop = anchorRect.top - titleBarHeight;
		int roomOnBottom = screenHeight - anchorRect.bottom;

		boolean placeOnTop = roomOnTop > roomOnBottom;

		int popupY = 0;
		if (placeOnTop) {
			if (popupHeight <= roomOnTop) {
				// place full popup
				popupY = anchorRect.top - popupHeight;
			} else {
				// resize popup
				popupHeight = roomOnTop;
				popupY = titleBarHeight;
			}
		} else {
			// place below anchor
			if (popupHeight <= roomOnBottom) {
				popupY = anchorRect.bottom;
			} else {
				popupHeight = roomOnBottom;
				popupY = anchorRect.bottom;
			}
		}

		if (popupWidth > screenWidth) {
			popupWidth = screenWidth;
		}

		Rect popupRect = new Rect(0, 0, popupWidth, popupHeight);
		logRect(popupRect);

		Log.d("lab", String.format("popUpView original width:%d height:%d",
				popupWidth, popupHeight));

		Log.d("lab", String.format("popUpView adjusted width:%d height:%d",
				popupWidth, popupHeight));

		// positioning popup
		mpopup = new PopupWindow(popUpView);
		mpopup.setFocusable(true);

		mpopup.setAnimationStyle(android.R.style.Animation_Dialog);
		mpopup.setBackgroundDrawable(new ColorDrawable(0xabcdef));
		mpopup.showAsDropDown(anchor);

		mpopup.setOutsideTouchable(true);
		mpopup.setTouchInterceptor(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}
				return false;
			}
		});

		int popupX = anchorRect.centerX() - popupWidth / 2;

		Log.d("lab", String.format(
				"update popup: x:%d y:%d width:%d height:%d", popupX, popupY,
				popupWidth, popupHeight));
		mpopup.update(popupX, popupY, popupWidth, popupHeight);

	}

	Rect getViewRect(View v) {
		int[] loc = new int[2];
		v.getLocationOnScreen(loc);
		int anchorX = loc[0];
		int anchorY = loc[1];

		int w = anchorX + v.getWidth();
		int h = anchorY + v.getHeight();

		return new Rect(anchorX, anchorY, w, h);
	}

	void logRect(Rect anchorRect) {
		Log.d("lab", String.format(
				"anchorRect left:%d right:%d top:%d bottom:%d",
				anchorRect.left, anchorRect.right, anchorRect.top,
				anchorRect.bottom));
	}

	View inflatePopup(LayoutInflater inflater) {
		View popUpView = inflater.inflate(
				isHorizontal ? R.layout.action_popup_horizontal
						: R.layout.action_popup_vertical, null);

		actionContainer = (ViewGroup) popUpView
				.findViewById(R.id.actionContainer);

		return popUpView;
	}

	public int getTitleBarHeight(Activity context) {
		Rect rectgle = new Rect();
		Window window = context.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
		int StatusBarHeight = rectgle.top;
		int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT)
				.getTop();
		int TitleBarHeight = contentViewTop - StatusBarHeight;
		return TitleBarHeight;

	}

	public void dismiss() {
		if (mpopup != null && mpopup.isShowing()) {
			mpopup.dismiss();
		}
	}

	public void addAction(Context context, String title,
			final View.OnClickListener listener) {
		Button button = new Button(context);
		button.setText(title);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

				if (listener != null) {
					listener.onClick(v);
				}
			}
		});

		actionContainer.addView(button);
	}
}
