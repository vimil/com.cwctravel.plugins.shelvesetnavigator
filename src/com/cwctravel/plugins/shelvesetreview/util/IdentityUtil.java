package com.cwctravel.plugins.shelvesetreview.util;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.microsoft.tfs.core.clients.webservices.TeamFoundationIdentity;

public class IdentityUtil {
	public static String getCurrentUserId() {
		return ShelvesetReviewPlugin.getDefault().getIdentityManager().getCurrentUserId();
	}

	public static TeamFoundationIdentity getIdentity(String memberId) {
		return ShelvesetReviewPlugin.getDefault().getIdentityManager().getIdentity(memberId);
	}

	public static String getCurrentUserName() {
		return ShelvesetReviewPlugin.getDefault().getIdentityManager().getCurrentUserName();
	}

	public static boolean isMember(String groupId, String memberId) {
		return ShelvesetReviewPlugin.getDefault().getIdentityManager().isMember(groupId, memberId);
	}

	public static boolean userNamesSame(String username1, String username2) {
		return ShelvesetReviewPlugin.getDefault().getIdentityManager().userNamesSame(username1, username2);
	}

	public static TeamFoundationIdentity getDefaultReviewersGroup() {
		return ShelvesetReviewPlugin.getDefault().getIdentityManager().getDefaultReviewersGroup();
	}

	public static boolean isDefaultReviewerGroupId(String memberId) {
		boolean result = false;
		TeamFoundationIdentity defaultReviewersGroup = getDefaultReviewersGroup();
		if (defaultReviewersGroup != null) {
			TeamFoundationIdentity memberIdentity = getIdentity(memberId);
			if (memberIdentity != null && userNamesSame(memberIdentity.getUniqueName(), defaultReviewersGroup.getUniqueName())) {
				result = true;
			}
		}
		return result;
	}
}
