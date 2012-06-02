package com.google.api.ui;

import org.dyndns.warenix.alarm.R;
import org.dyndns.warenix.util.WLog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.api.GoogleAppInfo;
import com.google.api.GoogleOAuthAccessToken;
import com.google.api.GoogleOAuthListener;
import com.google.api.GoogleOAuthMaster;

public class GoogleOAuthActivity extends SherlockFragmentActivity {
	static final String TAG = "GoogleOAuthActivity";

	public static final int REQUEST_CODE_OAUTH = 1;
	public static final String BUNDLE_OAUTH_URL = "oauth_url";
	public static final String BUNDLE_APP_INFO = "app_info";
	public static final String BUNDLE_APP_TITLE = "app_title";
	public static final String BUNDLE_OAUTH_LISTENER = "oauth_listener";
	public static final String BUNDLE_REFRESH_ACCESS_TOKEN = "refresh_access_token";
	public static final String BUNDLE_FLAG_DO_IN_BACKGROUND = "do_in_background_flag";
	public static final String RESULT_CODE = "code";

	private GoogleAppInfo mAppInfo;
	private GoogleOAuthListener mListener;

	boolean mIsDoInBackground;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAppInfo = getIntent().getParcelableExtra(BUNDLE_APP_INFO);
		mListener = (GoogleOAuthListener) getIntent().getSerializableExtra(
				BUNDLE_OAUTH_LISTENER);

		// String appTitle = getIntent().getStringExtra(BUNDLE_APP_TITLE);
		setTitle(getResources().getString(R.string.app_name));

		// boolean isRefresh = getIntent().getBooleanExtra(
		// BUNDLE_REFRESH_ACCESS_TOKEN, false);
		// mIsDoInBackground = getIntent().getBooleanExtra(
		// BUNDLE_FLAG_DO_IN_BACKGROUND, true);
		// final GoogleOAuthAccessToken accessToken = GoogleOAuthAccessToken
		// .load(this);

		setupUI();

		// if (isRefresh && accessToken != null) {
		// final AlertDialog diag = new AlertDialog.Builder(
		// GoogleOAuthActivity.this)
		// .setTitle("Google OAuth")
		// // .setView(view)
		// .setMessage(
		// "Refreshing your token... Please wait a moment.")
		// .setPositiveButton("OK",
		// new DialogInterface.OnClickListener() {
		// public void onClick(
		// DialogInterface dialoginterface, int i) {
		// }
		// })
		// .setNegativeButton("Cancel",
		// new DialogInterface.OnClickListener() {
		// public void onClick(
		// DialogInterface dialoginterface, int i) {
		// }
		// }).show();
		//
		// new Thread() {
		// public void run() {
		// String accessTokenJsonString = refreshAccessToken(
		// mAppInfo.clientId, mAppInfo.clientSecret,
		// accessToken.refreshToken);
		// try {
		// accessToken.refresh(accessTokenJsonString);
		// accessToken.save(getApplicationContext());
		// onOAuthAccessTokenExchanged(accessToken);
		// setResult(RESULT_OK);
		// } catch (JSONException e) {
		// onOAuthFail(e.toString());
		// setResult(RESULT_CANCELED);
		// }
		//
		// if (mIsDoInBackground) {
		// diag.dismiss();
		// }
		//
		// }
		// }.start();
		//
		// } else {
		// if (!mIsDoInBackground) {
		// setupUI();
		// } else {
		// }
		// }
	}

	void setupUI() {
		String oauthUrl = GoogleOAuthMaster.getOAuthUrl(mAppInfo);

		// Let's display the progress in the activity title bar, like the
		// browser app does.
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		WebView webview = new WebView(this);
		setContentView(webview);
		webview.getSettings().setJavaScriptEnabled(true);

		final Activity activity = this;
		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Activities and WebViews measure progress with different
				// scales.
				// The progress meter will automatically disappear when we reach
				// 100%
				activity.setProgress(progress * 1000);
				Log.d(TAG, "progress:" + progress);
			}

			public void onReceivedTitle(WebView view, String title) {
				Log.d(TAG, "title:" + title);
				String pattern = "code=";
				if (title.contains(pattern)) {
					int start = title.indexOf(pattern) + pattern.length();
					final String code = title.substring(start);
					Log.d(TAG, pattern + title.substring(start));

					new Thread() {
						public void run() {
							onUserLoginSuccess(code);
						}
					}.start();

					// Intent data = new Intent();
					// data.putExtra(RESULT_CODE, code);
					// setResult(RESULT_OK, data);
					finish();
				} else {
					pattern = "error=";
					if (title.contains(pattern)) {
						int start = title.indexOf(pattern) + pattern.length();
						String code = title.substring(start);
						// Intent data = new Intent();
						// data.putExtra(RESULT_CODE, code);
						// setResult(RESULT_CANCELED, data);
						onOAuthFail(code);
					}
				}
			}
		});
		webview.setWebViewClient(new WebViewClient() {

		});

		webview.loadUrl(oauthUrl);
	}

	public static void startOauthActivity(Context context,
			GoogleAppInfo appInfo, String appTitle, Messenger messenger) {
		Intent intent = new Intent(context, GoogleOAuthActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(BUNDLE_APP_INFO, appInfo);
		intent.putExtra(BUNDLE_APP_TITLE, appTitle);
		intent.putExtra(GoogleOAuthIntentService.BUNDLE_MESSENGER, messenger);
		context.startActivity(intent);
	}

	//
	// /**
	// * get Google OAuth 2.0 url
	// *
	// * @param clientId
	// * @param redirectUri
	// * @param scope
	// * @return
	// */
	// static String getOAuthUrl(GoogleAppInfo appInfo) {
	// return String
	// .format("https://accounts.google.com/o/oauth2/auth?client_id=%s&redirect_uri=%s&scope=%s&response_type=code",
	// appInfo.clientId, appInfo.redirectUri, appInfo.scope);
	// }
	//
	// /**
	// * After user authorization of the app access, use the displayed code to
	// * exchange for a access token
	// *
	// * @param code
	// * @param clientId
	// * @param clientSecret
	// * @param redirectUri
	// * @param grantType
	// * @return access token in JSON
	// */
	// public static String exchangeCodeForAccessToken(String code,
	// String clientId, String clientSecret, String redirectUri) {
	// // Create a new HttpClient and Post Header
	// HttpClient httpclient = new DefaultHttpClient();
	// HttpPost httppost = new HttpPost(
	// "https://accounts.google.com/o/oauth2/token");
	//
	// try {
	// // Add your data
	// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	// nameValuePairs.add(new BasicNameValuePair("code", code));
	//
	// nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
	// nameValuePairs.add(new BasicNameValuePair("client_secret",
	// clientSecret));
	// nameValuePairs.add(new BasicNameValuePair("redirect_uri",
	// redirectUri));
	// nameValuePairs.add(new BasicNameValuePair("grant_type",
	// "authorization_code"));
	// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	//
	// // Execute HTTP Post Request
	// HttpResponse response = httpclient.execute(httppost);
	//
	// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	// String accessToken = EntityUtils.toString(response.getEntity());
	// return accessToken;
	// }
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return null;
	// }

	// /**
	// * when access token expire, use refresh token to get a new one
	// *
	// * @param code
	// * @param clientId
	// * @param clientSecret
	// * @param refreshToken
	// * @return
	// */
	// public static String refreshAccessToken(String clientId,
	// String clientSecret, String refreshToken) {
	// // Create a new HttpClient and Post Header
	// HttpClient httpclient = new DefaultHttpClient();
	// HttpPost httppost = new HttpPost(
	// "https://accounts.google.com/o/oauth2/token");
	//
	// try {
	// // Add your data
	// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	//
	// nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
	// nameValuePairs.add(new BasicNameValuePair("client_secret",
	// clientSecret));
	// nameValuePairs.add(new BasicNameValuePair("refresh_token",
	// refreshToken));
	// nameValuePairs.add(new BasicNameValuePair("grant_type",
	// "refresh_token"));
	// httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	//
	// // Execute HTTP Post Request
	// HttpResponse response = httpclient.execute(httppost);
	//
	// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	// String accessToken = EntityUtils.toString(response.getEntity());
	// return accessToken;
	// } else {
	// Log.d(TAG, EntityUtils.toString(response.getEntity()));
	// }
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return null;
	// }

	protected void onUserLoginSuccess(final String code) {
		Log.i(TAG, "user logined with code:" + code);
		//
		// Intent intent = new Intent(
		// GoogleOAuthIntentService.IntentAction.ACTION_EXCHANGE_TOKEN);
		// intent.putExtra(BUNDLE_APP_INFO, mAppInfo);
		// intent.putExtra("messenger", getIntent()
		// .getParcelableExtra("messenger"));
		// intent.putExtra("doInBackground", false);
		// intent.putExtra(GoogleOAuthIntentService.BUNDLE_OAUTH_CODE, code);
		// intent.putExtra(GoogleOAuthActivity.BUNDLE_APP_TITLE, "Demo");
		// startService(intent);

		// // continue for the final step of oauth
		// String accessTokenJsonString = exchangeCodeForAccessToken(code,
		// mAppInfo.clientId, mAppInfo.clientSecret, mAppInfo.redirectUri);
		//
		// try {
		// GoogleOAuthAccessToken accessToken = new GoogleOAuthAccessToken(
		// accessTokenJsonString);
		// Log.d(TAG, "received access token:" + accessToken.accessToken);
		// accessToken.save(getApplicationContext());
		//
		// onOAuthAccessTokenExchanged(accessToken);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// onOAuthFail(e.toString());
		// }
		Intent intent = GoogleOAuthIntentService
				.prepareActionExchangeTokenIntent((Messenger) getIntent()
						.getParcelableExtra("messenger"), mAppInfo, code);
		startService(intent);
	}

	protected void onOAuthFail(final String errorCode) {
		WLog.d(TAG, String.format("", errorCode));

		// Intent intent = new Intent(
		// GoogleOAuthIntentService.IntentAction.ACTION_OAUTH_FAIL);
		// intent.putExtra(BUNDLE_APP_INFO, mAppInfo);
		// intent.putExtra("messenger", getIntent()
		// .getParcelableExtra("messenger"));
		// intent.putExtra("doInBackground", false);
		// intent.putExtra(GoogleOAuthIntentService.BUNDLE_OAUTH_ERROR_CODE,
		// errorCode);
		// intent.putExtra(GoogleOAuthActivity.BUNDLE_APP_TITLE, "Demo");

		Intent intent = GoogleOAuthIntentService.prepareActionOAuthFailIntent(
				(Messenger) getIntent().getParcelableExtra("messenger"),
				errorCode);
		startService(intent);
	}

	/**
	 * on UI thread
	 * 
	 * @param accessToken
	 */
	protected void onOAuthAccessTokenExchanged(
			final GoogleOAuthAccessToken accessToken) {
		if (mListener != null) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mListener.onOAuthAccessTokenExchanged(accessToken);
				}
			});

		}
	}

}
