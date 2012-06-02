package com.google.api.ui;

import org.dyndns.warenix.util.WLog;
import org.json.JSONException;

import android.app.IntentService;
import android.content.Intent;
import android.os.Messenger;

import com.google.api.GoogleAppInfo;
import com.google.api.GoogleOAuthAccessToken;
import com.google.api.GoogleOAuthMaster;

public class GoogleOAuthIntentService extends IntentService {
	private static final String TAG = "GoogleOAuthIntentService";

	public static class IntentAction {
		/**
		 * start oauth, let user input credential
		 */
		public static String ACTION_OAUTH = "action_oauth";
		public static String ACTION_REFRESH_ACTION_TOKEN = "action_refresh_token";
		public static String ACTION_EXCHANGE_TOKEN = "exchange_token";
		public static String ACTION_OAUTH_FAIL = "oauth_fail";
		public static String ACTION_OAUTH_DONE = "oauth_done";
	}

	public static final String BUNDLE_OAUTH_URL = "oauth_url";
	public static final String BUNDLE_APP_INFO = "app_info";
	public static final String BUNDLE_OAUTH_LISTENER = "oauth_listener";
	public static final String BUNDLE_REFRESH_ACCESS_TOKEN = "refresh_access_token";
	public static final String BUNDLE_FLAG_DO_IN_BACKGROUND = "do_in_background_flag";
	public static final String BUNDLE_MESSENGER = "messenger";
	public static final String BUNDLE_OAUTH_CODE = "oauth_code";
	public static final String BUNDLE_OAUTH_ERROR_CODE = "oauth_error_code";
	public static final String BUNDLE_DO_IN_BACKGROUND = "do_in_background";

	public GoogleOAuthIntentService() {
		this("GoogleOAuthIntentService");
	}

	public GoogleOAuthIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		WLog.d(TAG, String.format("received action[%s]", action));

		if (IntentAction.ACTION_OAUTH.equals(action)) {
			handleOAuth(intent);
		} else if (IntentAction.ACTION_EXCHANGE_TOKEN.equals(action)) {
			handleExchangeCodeForAccessToken(intent);
		} else if (IntentAction.ACTION_REFRESH_ACTION_TOKEN.equals(action)) {
			handleRefreshToken(intent);
		} else if (IntentAction.ACTION_OAUTH_FAIL.equals(action)) {
			handleOAuthFail(intent);
		}
	}

	protected void handleOAuth(Intent intent) {
		WLog.i(TAG, "start ui for google oauth 2.0");

		String appTitle = intent
				.getStringExtra(GoogleOAuthActivity.BUNDLE_APP_TITLE);
		GoogleAppInfo appInfo = new GoogleAppInfo(this);
		boolean isDoInBackground = intent.getBooleanExtra(
				BUNDLE_DO_IN_BACKGROUND, true);

		Messenger messenger = (Messenger) intent
				.getParcelableExtra(BUNDLE_MESSENGER);

		if (!isDoInBackground) {
			GoogleOAuthActivity.startOauthActivity(this, appInfo, appTitle,
					messenger);
		}
	}

	protected void handleExchangeCodeForAccessToken(Intent intent) {
		WLog.i(TAG, "exchange code for access token");

		GoogleAppInfo mAppInfo = intent.getParcelableExtra(BUNDLE_APP_INFO);
		GoogleOAuthAccessToken accessToken = GoogleOAuthAccessToken
				.load(getApplicationContext());
		String code = intent.getStringExtra(BUNDLE_OAUTH_CODE);

		String accessTokenJsonString = GoogleOAuthMaster
				.exchangeCodeForAccessToken(code, mAppInfo.clientId,
						mAppInfo.clientSecret, mAppInfo.redirectUri);

		saveAccessToken(accessToken, accessTokenJsonString);
		notifyHandler(intent);
	}

	protected void handleRefreshToken(Intent intent) {
		WLog.i(TAG, "refresh token");

		GoogleAppInfo mAppInfo = intent.getParcelableExtra(BUNDLE_APP_INFO);
		GoogleOAuthAccessToken accessToken = GoogleOAuthAccessToken
				.load(getApplicationContext());

		String accessTokenJsonString = GoogleOAuthMaster.refreshAccessToken(
				mAppInfo.clientId, mAppInfo.clientSecret,
				accessToken.refreshToken);

		saveAccessToken(accessToken, accessTokenJsonString);
		notifyHandler(intent);
	}

	protected void handleOAuthFail(Intent intent) {
		WLog.i(TAG, "oauth fail");
		notifyHandler(intent);
	}

	protected void saveAccessToken(GoogleOAuthAccessToken accessToken,
			String accessTokenJsonString) {
		WLog.i(TAG, "save access token locally");

		// save locally
		try {
			accessToken.refresh(accessTokenJsonString);
			accessToken.save(getApplicationContext());
		} catch (JSONException e) {
		}
	}

	protected void notifyHandler(Intent intent) {
		WLog.d(TAG, "notify handler");

		String errorCode = (String) intent
				.getStringExtra(BUNDLE_OAUTH_ERROR_CODE);
		//
		// Bundle bundle = intent.getExtras();
		// Messenger messenger = (Messenger) bundle.get("messenger");
		// Message msg = Message.obtain();
		// Bundle data = new Bundle();
		// data.putString("action", intent.getAction());
		// if (errorCode != null) {
		// data.putBoolean("result", false);
		// data.putString("errorCode", errorCode);
		// } else {
		// data.putBoolean("result", true);
		// }
		//
		// msg.setData(data); // put the data here
		// try {
		// messenger.send(msg);
		// } catch (RemoteException e) {
		// WLog.i(TAG, "error");
		// }

		Intent oauthIntent = new Intent(IntentAction.ACTION_OAUTH_DONE);
		oauthIntent.putExtra("action", intent.getAction());
		if (errorCode != null) {
			oauthIntent.putExtra("result", false);
			oauthIntent.putExtra("errorCode", errorCode);
		} else {
			oauthIntent.putExtra("result", true);
		}
		sendBroadcast(oauthIntent);
	}

	public static Intent prepareActionOAuthIntent(Messenger messenger,
			boolean doInBackground) {
		Intent intent = new Intent(
				GoogleOAuthIntentService.IntentAction.ACTION_OAUTH);
		intent.putExtra(GoogleOAuthIntentService.BUNDLE_MESSENGER, messenger);
		intent.putExtra(GoogleOAuthIntentService.BUNDLE_DO_IN_BACKGROUND,
				doInBackground);
		return intent;
	}

	public static Intent prepareActionExchangeTokenIntent(Messenger messenger,
			GoogleAppInfo appInfo, String code) {
		Intent intent = new Intent(
				GoogleOAuthIntentService.IntentAction.ACTION_EXCHANGE_TOKEN);
		intent.putExtra(BUNDLE_APP_INFO, appInfo);
		intent.putExtra(GoogleOAuthIntentService.BUNDLE_MESSENGER, messenger);
		intent.putExtra(GoogleOAuthIntentService.BUNDLE_OAUTH_CODE, code);
		return intent;
	}

	public static Intent prepareActionOAuthFailIntent(Messenger messenger,
			String errorCode) {
		Intent intent = new Intent(
				GoogleOAuthIntentService.IntentAction.ACTION_OAUTH_FAIL);
		intent.putExtra(GoogleOAuthIntentService.BUNDLE_MESSENGER, messenger);
		intent.putExtra(GoogleOAuthIntentService.BUNDLE_OAUTH_ERROR_CODE,
				errorCode);
		return intent;
	}

	public static Intent prepareActionRefreshTokenIntent(Messenger messenger,
			GoogleAppInfo appInfo) {
		Intent intent = new Intent(
				GoogleOAuthIntentService.IntentAction.ACTION_REFRESH_ACTION_TOKEN);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(BUNDLE_APP_INFO, appInfo);
		intent.putExtra(GoogleOAuthIntentService.BUNDLE_MESSENGER, messenger);
		return intent;
	}
}
