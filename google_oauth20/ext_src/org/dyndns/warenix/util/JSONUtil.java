package org.dyndns.warenix.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {

	public static String getString(JSONObject json, String name,
			String defaultValue) {
		try {
			return json.getString(name);
		} catch (JSONException e) {
			return defaultValue;
		}
	}
}
