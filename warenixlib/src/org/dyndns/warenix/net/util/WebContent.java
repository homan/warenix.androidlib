package org.dyndns.warenix.net.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.dyndns.warenix.util.WLog;

import android.os.Bundle;
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

	/**
	 * get response from url using https
	 * 
	 * @param url
	 * @param nameValuePairs
	 * @return
	 * @throws FacebookException
	 */
	public static String callHTTPS(String url,
			List<NameValuePair> nameValuePairs) throws Exception {
		HttpPost post = new HttpPost(url);
		// support Chinese
		post.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

		HttpClient client = new DefaultHttpClient();
		HttpResponse response;
		response = client.execute(post);
		HttpEntity entity = response.getEntity();
		// return true or false
		String responseString = EntityUtils.toString(entity);
		WLog.d(TAG, "response json: " + responseString);
		return responseString;
	}

	/**
	 * Connect to an HTTP URL and return the response as a string.
	 * 
	 * Note that the HTTP method override is used on non-GET requests. (i.e.
	 * requests are made as "POST" with method specified in the body).
	 * 
	 * @param url
	 *            - the resource to open: must be a welformed URL
	 * @param method
	 *            - the HTTP method to use ("GET", "POST", etc.)
	 * @param params
	 *            - the query parameter for the URL (e.g. access_token=foo)
	 * @return the URL contents as a String
	 * @throws MalformedURLException
	 *             - if the URL format is invalid
	 * @throws IOException
	 *             - if a network problem occurs
	 */
	public static String openUrl(String url, String method, Bundle params)
			throws MalformedURLException, IOException {
		// random string as boundary for multi-part http post
		String strBoundary = "3i2ndDfv2rTHiSisAbouNdArYfORhtTPEefj3q2f";
		String endLine = "\r\n";

		OutputStream os;

		if (method.equals("GET") || method.equals("DELETE")) {
			url = url + "?" + encodeUrl(params);
		}
		Log.d("warenix", url);
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		conn.setRequestProperty("User-Agent", System.getProperties()
				.getProperty("http.agent") + " warenix");
		if (method.equals("DELETE")) {
			conn.setRequestMethod("DELETE");
		} else if (!method.equals("GET")) {
			Bundle dataparams = new Bundle();
			for (String key : params.keySet()) {
				Object parameter = params.get(key);
				if (parameter instanceof byte[]) {
					dataparams.putByteArray(key, (byte[]) parameter);
				}
			}

			// use method override
			if (!params.containsKey("method")) {
				params.putString("method", method);
			}

			if (params.containsKey("access_token")) {
				String decoded_token = URLDecoder.decode(params
						.getString("access_token"));
				params.putString("access_token", decoded_token);
			}

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + strBoundary);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.connect();
			os = new BufferedOutputStream(conn.getOutputStream());

			os.write(("--" + strBoundary + endLine).getBytes());
			os.write((encodePostBody(params, strBoundary)).getBytes());
			os.write((endLine + "--" + strBoundary + endLine).getBytes());

			if (!dataparams.isEmpty()) {

				for (String key : dataparams.keySet()) {
					os.write(("Content-Disposition: form-data; filename=\""
							+ key + "\"" + endLine).getBytes());
					os.write(("Content-Type: content/unknown" + endLine + endLine)
							.getBytes());
					os.write(dataparams.getByteArray(key));
					os.write((endLine + "--" + strBoundary + endLine)
							.getBytes());

				}
			}
			os.flush();
		}

		String response = "";
		try {
			response = read(conn.getInputStream());
		} catch (FileNotFoundException e) {
			// Error Stream contains JSON that we can parse to a FB error
			response = read(conn.getErrorStream());
		}
		return response;
	}

	public static String encodeUrl(Bundle parameters) {
		if (parameters == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			Object parameter = parameters.get(key);
			if (!(parameter instanceof String)) {
				continue;
			}

			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(URLEncoder.encode(key) + "="
					+ URLEncoder.encode(parameters.getString(key)));
		}
		return sb.toString();
	}

	/**
	 * Generate the multi-part post body providing the parameters and boundary
	 * string
	 * 
	 * @param parameters
	 *            the parameters need to be posted
	 * @param boundary
	 *            the random string as boundary
	 * @return a string of the post body
	 */
	public static String encodePostBody(Bundle parameters, String boundary) {
		if (parameters == null)
			return "";
		StringBuilder sb = new StringBuilder();

		for (String key : parameters.keySet()) {
			Object parameter = parameters.get(key);
			if (!(parameter instanceof String)) {
				continue;
			}

			sb.append("Content-Disposition: form-data; name=\"" + key
					+ "\"\r\n\r\n" + (String) parameter);
			sb.append("\r\n" + "--" + boundary + "\r\n");
		}

		return sb.toString();
	}

	private static String read(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = r.readLine(); line != null; line = r.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}
}
