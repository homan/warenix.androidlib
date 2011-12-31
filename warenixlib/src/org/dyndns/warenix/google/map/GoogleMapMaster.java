package org.dyndns.warenix.google.map;

public class GoogleMapMaster {

	public static String getStaticMapImageURL(double lng, double lat, int zoom,
			int width, int height) {
		return String
				.format("https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=%d&size=%dx%d&sensor=false&markers=size:mid|%f,%f",
						lng, lat, zoom, width, height, lng, lat);
	}
}
