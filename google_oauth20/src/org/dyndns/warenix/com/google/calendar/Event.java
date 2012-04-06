package org.dyndns.warenix.com.google.calendar;

import org.dyndns.warenix.util.JSONUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class Event {
	public String kind;
	public String etag;
	public String id;
	public String status;
	public String htmlLink;
	public String created;
	public String updated;
	public String summary;
	public String description;
	public String timeZone;
	public String colorId;
	public String selected;
	public String accessRole;

	public static Event factory(String jsonString) {
		JSONObject json;
		try {
			json = new JSONObject(jsonString);
			return factory(json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Event factory(JSONObject json) {
		Event item = new Event();
		item.kind = JSONUtil.getString(json, "kind", null);
		item.etag = JSONUtil.getString(json, "etag", null);
		item.id = JSONUtil.getString(json, "id", null);
		item.summary = JSONUtil.getString(json, "summary", null);
		item.description = JSONUtil.getString(json, "description", null);
		item.colorId = JSONUtil.getString(json, "colorId", null);
		item.selected = JSONUtil.getString(json, "selected", null);
		item.accessRole = JSONUtil.getString(json, "accessRole", null);
		return item;
	}
}
