package org.dyndns.warenix.google.search;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GoogleWebSearchMaster {

	public static class WebSearchResult {
		public WebSearchResult(String resultString) throws JSONException {
			// TODO Auto-generated constructor stub
			JSONObject json = new JSONObject(resultString);
			JSONObject responseDataJson = json.getJSONObject("responseData");
			JSONArray resultList = responseDataJson.getJSONArray("results");
			int l = resultList.length();
			JSONObject resultJson = null;
			Page page = null;
			for (int i = 0; i < l; ++i) {
				resultJson = resultList.getJSONObject(i);

				page = new Page();
				page.unescapedUrl = resultJson.getString("unescapedUrl");
				page.url = resultJson.getString("url");
				page.visibleUrl = resultJson.getString("visibleUrl");
				page.cacheUrl = resultJson.getString("cacheUrl");
				page.title = resultJson.getString("title");
				page.titleNoFormatting = resultJson
						.getString("titleNoFormatting");
				page.content = resultJson.getString("content");

				mPageList.add(page);
			}

			JSONObject cursorJson = responseDataJson.getJSONObject("cursor");
			resultCount = cursorJson.getString("resultCount");
			currentPageIndex = cursorJson.getInt("currentPageIndex");
			JSONArray pageList = cursorJson.getJSONArray("pages");
			l = pageList.length();
			for (int i = 0; i < l; ++i) {
				cursorPageIndex.add(pageList.getJSONObject(i).getInt("start"));
			}
		}

		public ArrayList<Page> mPageList = new ArrayList<GoogleWebSearchMaster.Page>();
		public String resultCount;
		public int currentPageIndex;
		public ArrayList<Integer> cursorPageIndex = new ArrayList<Integer>();
	}

	public static class Page {
		public String unescapedUrl;
		public String url;
		public String visibleUrl;
		public String cacheUrl;
		public String title;
		public String titleNoFormatting;
		public String content;
	}

	/**
	 * 
	 * @param query
	 * @param start
	 * @param timeFilter
	 *            w: week, d: day,
	 * @return
	 */
	public static WebSearchResult doSearch(String query, int start,
			String timeFilter) {

		try {
			String baseUrl = "http://ajax.googleapis.com/ajax/services/search/web";
			String url = String.format("%s?v=1.0&q=%s&start=%d&as_qdr=%s&rsz=8",
					baseUrl, URLEncoder.encode(query, "utf-8"), start,
					timeFilter);

			HttpClient httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(
					"http.protocol.content-charset", "UTF-8");
			Log.d("warenix", "sending to " + url);
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpGet);

			String result = EntityUtils.toString(response.getEntity());
			WebSearchResult webSearchResult = parseWebSearchResult(result);
			return webSearchResult;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static WebSearchResult parseWebSearchResult(String resultString)
			throws JSONException {
		WebSearchResult webSearchResult = new WebSearchResult(resultString);
		return webSearchResult;
	}
}
