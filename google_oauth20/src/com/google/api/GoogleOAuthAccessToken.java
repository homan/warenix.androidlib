package com.google.api;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class GoogleOAuthAccessToken implements Serializable {
	static final String preferenceName = "google_access_token";
	public String accessToken;
	/**
	 * duration in seconds
	 */
	public int expiresIn;
	public String refreshToken;
	public String tokenType;
	public long expireDateMillis;

	public GoogleOAuthAccessToken() {
	}

	public GoogleOAuthAccessToken(String jsonString) throws JSONException {
		JSONObject json = new JSONObject(jsonString);
		accessToken = json.getString("access_token");
		expiresIn = json.getInt("expires_in");
		refreshToken = json.getString("refresh_token");
		tokenType = json.getString("token_type");
		expireDateMillis = System.currentTimeMillis() + expiresIn * 1000;
	}

	public void save(Context context) {
		SharedPreferences settings = context.getSharedPreferences(
				preferenceName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("accessToken", accessToken);
		editor.putInt("expiresIn", expiresIn);
		editor.putString("refreshToken", refreshToken);
		editor.putString("tokenType", tokenType);
		editor.putLong("expireDateMillis", expireDateMillis);
		editor.commit();
	}

	public static GoogleOAuthAccessToken load(Context context) {
		GoogleOAuthAccessToken accessToken = new GoogleOAuthAccessToken();
		SharedPreferences settings = context.getSharedPreferences(
				preferenceName, Context.MODE_PRIVATE);
		accessToken.accessToken = settings.getString("accessToken", null);
		accessToken.expiresIn = settings.getInt("expiresIn", 0);
		accessToken.refreshToken = settings.getString("refreshToken", null);
		accessToken.tokenType = settings.getString("tokenType", null);
		accessToken.expireDateMillis = settings.getLong("expireDateMillis", 0);

		return accessToken;
	}

	public void refresh(String jsonString) throws JSONException {
		JSONObject json = new JSONObject(jsonString);
		accessToken = json.getString("access_token");
		expiresIn = json.getInt("expires_in");
		tokenType = json.getString("token_type");
		expireDateMillis = System.currentTimeMillis() + expiresIn * 1000;
	}

	public boolean hasExpired() {
		return expireDateMillis < System.currentTimeMillis();
	}

}
