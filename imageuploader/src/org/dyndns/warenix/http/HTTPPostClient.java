package org.dyndns.warenix.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HTTPPostClient {
	private DefaultHttpClient mHttpClient;

	public HTTPPostClient() {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		mHttpClient = new DefaultHttpClient(params);
	}

	public void httpPost(String url, CustomMultiPartEntity multipartContent,
			ResponseHandler responseHandler) {

		try {
			HttpPost httppost = new HttpPost(url);

			httppost.setEntity(multipartContent);
			mHttpClient.execute(httppost, responseHandler);

		} catch (Exception e) {
			Log.e(HTTPPostClient.class.getName(), e.getLocalizedMessage(), e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static class StringResponseHandler implements ResponseHandler {

		@Override
		public Object handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {

			HttpEntity r_entity = response.getEntity();
			String responseString = EntityUtils.toString(r_entity);
			Log.d("UPLOAD", responseString);

			return responseString;
		}

	}

}
