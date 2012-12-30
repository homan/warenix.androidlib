package org.dyndns.warenix.google.map;

public class GoogleMapMaster {

	public static String getStaticMapImageURL(double lat, double lng, int zoom,
			int width, int height) {
		return String
				.format("https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=%d&size=%dx%d&sensor=false&markers=size:mid|%f,%f",
						lat, lng, zoom, width, height, lat, lng);
	}
}
