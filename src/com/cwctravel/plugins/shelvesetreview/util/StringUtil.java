package com.cwctravel.plugins.shelvesetreview.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class StringUtil {
	public static String truncateTo(String str, int length) {
		String result = str;
		if (str != null) {
			result = str.replace("\n", " ");
			result = result.replace("\r", "");
			if (result.length() > length) {
				result = result.substring(0, Math.max(length - 3, 0));
				result += "...";
			}
		}

		return result;
	}

	public static boolean equals(String str1, String str2) {
		return equals(str1, str2, false);
	}

	public static boolean equals(String str1, String str2, boolean normalizeNewLines) {
		if (str1 == str2) {
			return true;
		}
		if (str1 == null || str2 == null) {
			return false;
		}
		if (normalizeNewLines) {
			str1 = normalizeNewLines(str1);
			str2 = normalizeNewLines(str2);
		}
		return str1.equals(str2);
	}

	public static <T> String joinCollection(Collection<T> collection, String sepStr) {
		StringBuilder sB = new StringBuilder();
		if (collection != null) {
			Iterator<T> iter = collection.iterator();
			while (iter.hasNext()) {
				T t = iter.next();
				if (t != null) {
					sB.append(t.toString());
				}
				if (iter.hasNext()) {
					sB.append(sepStr);
				}
			}
		}
		return sB.toString();
	}

	public static String normalizeNewLines(String str) {
		if (str != null) {
			str = str.replace("\r\n", "\n");
		}
		return str;
	}

	public static String indent(String str, int indent) {
		String result = str;
		if (str != null && indent > 0) {
			char[] tabArray = new char[indent];
			Arrays.fill(tabArray, '\t');
			String tabArrayStr = new String(tabArray);
			String replaceStr = "\n" + tabArrayStr;
			result = tabArrayStr + str.replace("\n", replaceStr);
		}
		return result;
	}
}
