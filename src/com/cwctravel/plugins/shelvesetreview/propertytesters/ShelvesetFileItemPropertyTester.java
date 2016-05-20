package com.cwctravel.plugins.shelvesetreview.propertytesters;

import org.eclipse.core.expressions.PropertyTester;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;

public class ShelvesetFileItemPropertyTester extends PropertyTester {
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof ShelvesetFileItem) {
			ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) receiver;
			if ("canCompare".equals(property)) {
				boolean canCompare = shelvesetFileItem.getShelvedDownloadURL() != null
						&& shelvesetFileItem.getDownloadUrl() != null;
				return canCompare == (Boolean) expectedValue;
			}
		}

		return false;
	}
}
