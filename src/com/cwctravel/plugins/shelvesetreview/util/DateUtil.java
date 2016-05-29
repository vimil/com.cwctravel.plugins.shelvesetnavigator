package com.cwctravel.plugins.shelvesetreview.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.eclipse.core.runtime.Status;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;

public class DateUtil {
	public static final String DATE_FORMAT_1 = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
	public static final String DATE_FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss.SSX";
	public static final String DATE_FORMAT_3 = "yyyy-MM-dd'T'HH:mm:ss.SX";

	public static final String[] DATE_FORMATS = new String[] { DATE_FORMAT_2, DATE_FORMAT_3, DATE_FORMAT_1 };

	public static Calendar toCalendar(String str) {
		Calendar result = null;
		for (String dateFormat : DATE_FORMATS) {
			SimpleDateFormat sDF = new SimpleDateFormat(dateFormat);
			sDF.setTimeZone(TimeZone.getTimeZone("GMT"));
			Date date;
			try {
				date = sDF.parse(str);
				result = new GregorianCalendar();
				result.setTime(date);
				break;
			} catch (ParseException e) {
				ShelvesetReviewPlugin.log(Status.INFO, e.getMessage(), e);
			}
		}
		return result;
	}

	public static String formatDate(Calendar date) {
		String result = null;
		if (date != null) {
			SimpleDateFormat sDF = new SimpleDateFormat("MM/dd/YYYY hh:mm:ss a");
			sDF.setTimeZone(TimeZone.getTimeZone("GMT"));
			result = sDF.format(date.getTime());
		}
		return result;
	}
}
