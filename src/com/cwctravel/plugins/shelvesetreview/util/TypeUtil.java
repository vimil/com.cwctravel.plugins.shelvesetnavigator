package com.cwctravel.plugins.shelvesetreview.util;

public class TypeUtil {
	public static boolean optBoolean(Boolean value, boolean defaultValue) {
		boolean result = defaultValue;
		if (value != null) {
			result = value;
		}

		return result;
	}
}
