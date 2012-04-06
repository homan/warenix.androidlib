package com.google.api;

import java.io.Serializable;

public interface GoogleOAuthListener extends Serializable {
	/**
	 * After oauth success, a code is returned ny Google. You need to use this
	 * code to exchagne for an access token.
	 * 
	 * @param code
	 */
	public void onOAuthSuccess(String code);

	public void onOAuthFail(String errorCode);

	public void onOAuthAccessTokenExchanged(GoogleOAuthAccessToken accessToken);

}