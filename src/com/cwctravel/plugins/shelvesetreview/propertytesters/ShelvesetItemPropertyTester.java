package com.cwctravel.plugins.shelvesetreview.propertytesters;

import org.eclipse.core.expressions.PropertyTester;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;

public class ShelvesetItemPropertyTester extends PropertyTester {
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		boolean result = false;
		if (receiver instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) receiver;
			if ("isActive".equals(property)) {
				result = shelvesetItem.isInactive() != (Boolean) expectedValue;
			} else if ("canActivate".equals(property)) {
				result = shelvesetItem.canActivate() == (Boolean) expectedValue;
			} else if ("canAssignReviewers".equals(property)) {
				result = shelvesetItem.canAssignReviewers() == (Boolean) expectedValue;
			} else if ("belongsToCurrentUser".equals(property)) {
				result = TFSUtil.userIdsSame(TFSUtil.getCurrentUserId(), shelvesetItem.getOwnerName()) == (Boolean) expectedValue;
			} else if ("canApprove".equals(property)) {
				result = shelvesetItem.canApprove() == (Boolean) expectedValue;
			} else if ("canUnapprove".equals(property)) {
				result = shelvesetItem.canUnapprove() == (Boolean) expectedValue;
			}
		}

		return result;
	}

}
