package org.dyndns.warenix.com.google.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.dyndns.warenix.com.google.calendar.CalendarList.CalendarListItem;
import org.dyndns.warenix.util.WebContent;

import android.util.Log;

import com.google.api.GoogleOAuthAccessToken;

public class GoogleCalendarMaster {
	static final String TAG = "GoogleCalendarMaster";

	public static ArrayList<CalendarListItem> getAllCalendar(
			GoogleOAuthAccessToken accessToken) {
		String url = String
				.format("https://www.googleapis.com/calendar/v3/users/me/calendarList?&output=json&ck=1255643091105&client=scroll&oauth_token=%s",
						accessToken.accessToken);
		Log.d(TAG, "url:" + url);
		try {
			String responseJsonString = WebContent.getContent(url);
			Log.d(TAG, responseJsonString);

			return CalendarList.factory(responseJsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<CalendarListItem> getFutureEvent(
			GoogleOAuthAccessToken accessToken) {
		// RFC 3339 timestamp format
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'h:m:ss.SZ");
		String timeMin = format.format(new Date());
		String calendarId = "en.hong_kong%23holiday%40group.v.calendar.google.com";
		String url = String
				.format("https://www.googleapis.com/calendar/v3/calendars/%s/events?&output=json&ck=1255643091105&client=scroll&oauth_token=%s&timeMin=%s",
						calendarId, accessToken.accessToken, timeMin);
		Log.d(TAG, "url:" + url);
		try {
			String responseJsonString = WebContent.getContent(url);
			Log.d(TAG, responseJsonString);

			return CalendarList.factory(responseJsonString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
