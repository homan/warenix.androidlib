package com.google.api;

import org.dyndns.warenix.alarm.R;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

public class GoogleAppInfo implements Parcelable {

	static final String TAG = "GoogleAppInfo";

	public String clientId = "";
	public String clientSecret = "";
	public String redirectUri = "urn:ietf:wg:oauth:2.0:oob";
	// public static final String scope =
	// "http://www.google.com/reader/api/0/stream/contents/feed/";
	// public static final String scope =
	// "http://www.google.com/reader/api/0/stream/contents/";
	public String scope = "https://www.google.com/calendar/feeds/";

	public GoogleAppInfo(Context context) {
		clientId = context.getString(R.string.client_id);
		clientSecret = context.getString(R.string.client_secret);
	}

	public GoogleAppInfo(Parcel in) {
		readFromParcel(in);
	}

	// Parcelable
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(clientId);
		dest.writeString(clientSecret);
		dest.writeString(redirectUri);
		dest.writeString(scope);
	}

	private void readFromParcel(Parcel in) {
		clientId = in.readString();
		clientSecret = in.readString();
		redirectUri = in.readString();
		scope = in.readString();
	}

	public static final Parcelable.Creator<GoogleAppInfo> CREATOR = new Parcelable.Creator<GoogleAppInfo>() {
		public GoogleAppInfo createFromParcel(Parcel in) {
			return new GoogleAppInfo(in);
		}

		public GoogleAppInfo[] newArray(int size) {
			return new GoogleAppInfo[size];
		}
	};
}
