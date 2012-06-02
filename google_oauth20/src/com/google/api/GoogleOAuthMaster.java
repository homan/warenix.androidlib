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
import org.dyndns.warenix.util.WLog;

public class GoogleOAuthMaster {

	private static String TAG = "GoogleOAuthMaster";

	/**
	 * get Google OAuth 2.0 url
	 * 
	 * @param clientId
	 * @param redirectUri
	 * @param scope
	 * @return
	 */
	public static String getOAuthUrl(GoogleAppInfo appInfo) {
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
				WLog.d(TAG, EntityUtils.toString(response.getEntity()));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}
