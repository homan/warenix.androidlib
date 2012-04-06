package com.google.api;

import android.content.Context;

public class GoogleOAuthMaster {

	static final String TAG = "GoogleOAuthMaster";
	protected GoogleAppInfo mAppInfo;
	Context mContext;

	public GoogleOAuthMaster(Context context, GoogleAppInfo appInfo) {
		mContext = context;
		mAppInfo = appInfo;
	}

}
