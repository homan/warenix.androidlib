package com.google.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class GoogleOAuthActivity extends Activity {
	static final String TAG = "GoogleOAuthActivity";

	public static final int REQUEST_CODE_OAUTH = 1;
	public static final String BUNDLE_OAUTH_URL = "oauth_url";
	public static final String BUNDLE_APP_INFO = "app_info";
	public static final String BUNDLE_OAUTH_LISTENER = "oauth_listener";
	public static final String BUNDLE_REFRESH_ACCESS_TOKEN = "refresh_access_token";
	public static final String RESULT_CODE = "code";

	private GoogleAppInfo mAppInfo;
	private GoogleOAuthListener mListener;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAppInfo = getIntent().getParcelableExtra(BUNDLE_APP_INFO);
		mListener = (GoogleOAuthListener) getIntent().getSerializableExtra(
				BUNDLE_OAUTH_LISTENER);

		boolean isRefresh = getIntent().getBooleanExtra(
				BUNDLE_REFRESH_ACCESS_TOKEN, false);
		final GoogleOAuthAccessToken accessToken = GoogleOAuthAccessToken
				.load(this);
		if (isRefresh && accessToken != null) {
			Toast.makeText(this, "Refresh token", Toast.LENGTH_SHORT).show();

			new Thread() {
				public void run() {
					String accessTokenJsonString = refreshAccessToken(
							mAppInfo.clientId, mAppInfo.clientSecret,
							accessToken.refreshToken);
					try {
						accessToken.refresh(accessTokenJsonString);
						accessToken.save(getApplicationContext());
						onOAuthAccessTokenExchanged(accessToken);
					} catch (JSONException e) {
						onOAuthFail(e.toString());
					}

				}
			}.start();

		} else {
			setupUI();
		}
	}

	void setupUI() {
		String oauthUrl = getOAuthUrl(mAppInfo);

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
							onOAuthSuccess(code);
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
						finish();
					}
				}
			}
		});
		webview.setWebViewClient(new WebViewClient() {

		});

		webview.loadUrl(oauthUrl);
	}

	public static void startOauthActivity(Context context,
			GoogleAppInfo appInfo, boolean isRefresh,
			GoogleOAuthListener listener) {
		Intent intent = new Intent(context, GoogleOAuthActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(BUNDLE_APP_INFO, appInfo);
		intent.putExtra(BUNDLE_REFRESH_ACCESS_TOKEN, isRefresh);
		intent.putExtra(BUNDLE_OAUTH_LISTENER, listener);
		context.startActivity(intent);
	}

	/**
	 * get Google OAuth 2.0 url
	 * 
	 * @param clientId
	 * @param redirectUri
	 * @param scope
	 * @return
	 */
	static String getOAuthUrl(GoogleAppInfo appInfo) {
		return String
				.format("https://accounts.google.com/o/oauth2/auth?client_id=%s&redirect_uri=%s&scope=%s&response_type=code",
						appInfo.clientId, appInfo.redirectUri, appInfo.scope);
	}

	/**
	 * After user authorization of the app access, use the displayed code to
	 * exchange for a access token
	 * 
	 * @param code
	 * @param clientId
	 * @param clientSecret
	 * @param redirectUri
	 * @param grantType
	 * @return access token in JSON
	 */
	public static String exchangeCodeForAccessToken(String code,
			String clientId, String clientSecret, String redirectUri) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"https://accounts.google.com/o/oauth2/token");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("code", code));

			nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
			nameValuePairs.add(new BasicNameValuePair("client_secret",
					clientSecret));
			nameValuePairs.add(new BasicNameValuePair("redirect_uri",
					redirectUri));
			nameValuePairs.add(new BasicNameValuePair("grant_type",
					"authorization_code"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String accessToken = EntityUtils.toString(response.getEntity());
				return accessToken;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * when access token expire, use refresh token to get a new one
	 * 
	 * @param code
	 * @param clientId
	 * @param clientSecret
	 * @param refreshToken
	 * @return
	 */
	public static String refreshAccessToken(String clientId,
			String clientSecret, String refreshToken) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"https://accounts.google.com/o/oauth2/token");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
			nameValuePairs.add(new BasicNameValuePair("client_secret",
					clientSecret));
			nameValuePairs.add(new BasicNameValuePair("refresh_token",
					refreshToken));
			nameValuePairs.add(new BasicNameValuePair("grant_type",
					"refresh_token"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String accessToken = EntityUtils.toString(response.getEntity());
				return accessToken;
			} else {
				Log.d(TAG, EntityUtils.toString(response.getEntity()));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	protected void onOAuthSuccess(final String code) {
		Log.i(TAG, "oauth success with code:" + code);
		if (mListener != null) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mListener.onOAuthSuccess(code);
				}
			});
		}

		// continue for the final step of oauth
		String accessTokenJsonString = exchangeCodeForAccessToken(code,
				mAppInfo.clientId, mAppInfo.clientSecret, mAppInfo.redirectUri);

		try {
			GoogleOAuthAccessToken accessToken = new GoogleOAuthAccessToken(
					accessTokenJsonString);
			Log.d(TAG, "received access token:" + accessToken.accessToken);
			accessToken.save(getApplicationContext());

			onOAuthAccessTokenExchanged(accessToken);
		} catch (JSONException e) {
			e.printStackTrace();
			onOAuthFail(e.toString());
		}
	}

	protected void onOAuthFail(final String errorCode) {
		if (mListener != null) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mListener.onOAuthFail(errorCode);
				}
			});

		}
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
