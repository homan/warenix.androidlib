package org.dyndns.warenix.alarm.app;

import java.util.ArrayList;

import org.dyndns.warenix.alarm.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.api.GoogleAppInfo;
import com.google.api.GoogleOAuthAccessToken;
import com.google.api.GoogleOAuthActivity;
import com.google.api.GoogleOAuthListener;

public class AlarmActivity extends FragmentActivity implements
		GoogleOAuthListener {
	static final String TAG = "AlarmActivity";

	GoogleOAuthAccessToken mAccessToken;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mAccessToken = GoogleOAuthAccessToken.load(this);
		final GoogleAppInfo appInfo = new GoogleAppInfo(getApplicationContext());

		if (mAccessToken == null) {
			GoogleOAuthActivity.startOauthActivity(getApplicationContext(),
					appInfo, false, this);
		} else if (mAccessToken.hasExpired()) {
			GoogleOAuthActivity.startOauthActivity(getApplicationContext(),
					appInfo, true, this);
		} else {
			onReady();
		}
	}

	protected void onReady() {
	}

	@Override
	public void onOAuthSuccess(String code) {

	}

	@Override
	public void onOAuthFail(String errorCode) {

	}

	@Override
	public void onOAuthAccessTokenExchanged(GoogleOAuthAccessToken accessToken) {
		onReady();
	}
}