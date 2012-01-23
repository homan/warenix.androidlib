package org.dyndns.warenix.net.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class WebContent {
	private static final String TAG = "WebContent";

	public static String httpGet(String url) {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					"http.protocol.content-charset", "UTF-8");
			Log.d(TAG, "sending to " + url);
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpGet);

			String result = EntityUtils.toString(response.getEntity());
			Log.d(TAG, "httpGet result:" + result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
