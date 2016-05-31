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
	private static final float MILLIS_PER_HOUR = (1000 * 60 * 60);
	private static final float MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;

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

	@SuppressWarnings("unchecked")
	public static <T extends Calendar> T removeTimePart(T calendar) {
		T result = null;
		if (calendar != null) {
			result = (T) calendar.clone();
			result.set(Calendar.HOUR_OF_DAY, 0);
			result.set(Calendar.MINUTE, 0);
			result.set(Calendar.SECOND, 0);
			result.set(Calendar.MILLISECOND, 0);
		}
		return result;
	}

	public static Calendar currentDate() {
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate = removeTimePart(currentDate);
		return currentDate;
	}

	public static float differenceInDaysBetweenDates(Calendar date1, Calendar date2) {
		return (((date1.getTimeInMillis() + date1.getTimeZone().getOffset(date1.getTimeInMillis())) - (date2.getTimeInMillis() + date2.getTimeZone()
				.getOffset(date2.getTimeInMillis()))) / MILLIS_PER_DAY);
	}
}
