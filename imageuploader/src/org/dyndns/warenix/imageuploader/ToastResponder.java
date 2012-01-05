package org.dyndns.warenix.imageuploader;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.dyndns.warenix.http.HTTPPostClient.StringResponseHandler;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

class ToastResponder extends StringResponseHandler {
	Context context;

	public ToastResponder(Context context) {
		this.context = context;
	}

	@Override
	public Object handleResponse(HttpResponse response)
			throws ClientProtocolException, IOException {
		final String responseString = (String) super.handleResponse(response);
		((Activity) context).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(context, responseString, Toast.LENGTH_LONG)
						.show();
			}
		});

		return responseString;
	}
}