package org.dyndns.warenix.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

	public static Date parseISODate(String dateString) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
		return sdf.parse(dateString);
	}

	public String toISOString(Date date) {
		// and reading an ISO formatted date
		SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
		return sdf.format(date);
	}
}
