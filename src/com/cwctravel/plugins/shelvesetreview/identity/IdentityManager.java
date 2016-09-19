package com.cwctravel.plugins.shelvesetreview.identity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.core.TFSConnection;
import com.microsoft.tfs.core.clients.webservices.IIdentityManagementService2;
import com.microsoft.tfs.core.clients.webservices.IdentitySearchFactor;
import com.microsoft.tfs.core.clients.webservices.MembershipQuery;
import com.microsoft.tfs.core.clients.webservices.ReadIdentityOptions;
import com.microsoft.tfs.core.clients.webservices.TeamFoundationIdentity;

public class IdentityManager {
	private final Map<String, TeamFoundationIdentity> MEMBER_IDENTITY_MAP = Collections
			.synchronizedMap(new HashMap<String, TeamFoundationIdentity>());

	private final Map<String, Map<String, Boolean>> GROUP_MEMBERSHIP_MAP = Collections.synchronizedMap(new HashMap<String, Map<String, Boolean>>());
	private TeamFoundationIdentity defaultReviewersGroup;

	public TeamFoundationIdentity getIdentity(String memberId) {
		TeamFoundationIdentity result = MEMBER_IDENTITY_MAP.get(memberId);
		if (result == null) {
			result = findIdentity(memberId);
			if (result != null) {
				MEMBER_IDENTITY_MAP.put(memberId, result);
			}
		}
		return result;
	}

	private TeamFoundationIdentity findIdentity(String memberId) {
		TeamFoundationIdentity result = null;
		TFSConnection tfsConnection = TFSUtil.getTFSConnection();
		if (tfsConnection != null) {
			IIdentityManagementService2 identitySvc = (IIdentityManagementService2) tfsConnection.getClient(IIdentityManagementService2.class);

			if (identitySvc != null) {
				TeamFoundationIdentity[] reviewerIdentities = identitySvc.readIdentities(IdentitySearchFactor.GENERAL, new String[] { memberId },
						MembershipQuery.NONE, ReadIdentityOptions.NONE)[0];
				if (reviewerIdentities != null && reviewerIdentities.length > 0) {
					result = reviewerIdentities[0];
				}
			}
		}
		return result;
	}

	public TeamFoundationIdentity getDefaultReviewersGroup() {
		if (defaultReviewersGroup == null) {
			defaultReviewersGroup = getIdentity("Reviewers");
		}
		return defaultReviewersGroup;
	}

	private boolean isMember(TeamFoundationIdentity groupIdentity, TeamFoundationIdentity memberIdentity) {
		boolean result = false;
		TFSConnection tfsConnection = TFSUtil.getTFSConnection();
		if (tfsConnection != null) {
			IIdentityManagementService2 identitySvc = (IIdentityManagementService2) tfsConnection.getClient(IIdentityManagementService2.class);

			if (identitySvc != null) {
				result = identitySvc.isMember(groupIdentity.getDescriptor(), memberIdentity.getDescriptor());

			}
		}
		return result;

	}

	public boolean isMember(String groupId, String memberId) {
		boolean result = false;
		if (memberId != null) {
			if (userNamesSame(groupId, memberId)) {
				result = true;
			} else {
				TeamFoundationIdentity groupIdentity = getIdentity(groupId);
				if (groupIdentity != null && groupIdentity.isContainer()) {
					Map<String, Boolean> members = GROUP_MEMBERSHIP_MAP.get(groupId);
					if (members != null && members.getOrDefault(memberId, false)) {
						result = true;
					} else {
						TeamFoundationIdentity memberIdentity = getIdentity(memberId);
						if (memberIdentity != null) {
							result = isMember(groupIdentity, memberIdentity);
							if (members == null) {
								members = new HashMap<String, Boolean>();
								GROUP_MEMBERSHIP_MAP.put(groupId, members);
							}
							members.put(memberId, result);
						}
					}
				}

			}
		}
		return result;
	}

	public boolean userNamesSame(String username1, String username2) {
		if (username1 == username2) {
			return true;
		}
		if (username1 == null) {
			return false;
		}

		if (username2 == null) {
			return false;
		}

		if (username1.equals(username2)) {
			return true;
		}

		String[] userId1Parts = username1.split("\\\\");
		String[] userId2Parts = username2.split("\\\\");

		String currentDomain = getCurrentUserDomain();

		String domain1 = currentDomain;
		if (userId1Parts.length == 2) {
			domain1 = userId1Parts[0];
			username1 = userId1Parts[1];
		} else {
			domain1 = currentDomain;
		}

		String domain2 = currentDomain;
		if (userId2Parts.length == 2) {
			domain2 = userId2Parts[0];
			username2 = userId2Parts[1];
		}

		return domain1.equalsIgnoreCase(domain2) && username1.equalsIgnoreCase(username2);
	}

	public String normalizeUserName(String userId) {
		String result = userId;
		if (result != null) {
			String[] userIdParts = userId.split("\\\\");
			if (userIdParts.length != 2) {
				String currentDomain = getCurrentUserDomain();
				result = currentDomain + "\\" + userId;
			}
		}
		return result;
	}

	public String getCurrentUserDisplayName() {
		final TeamFoundationIdentity userIdentity = TFSUtil.getVersionControlClient().getConnection().getAuthorizedIdentity();
		return userIdentity.getDisplayName();
	}

	public String getCurrentUserName() {
		final TeamFoundationIdentity userIdentity = TFSUtil.getVersionControlClient().getConnection().getAuthorizedIdentity();
		return userIdentity.getUniqueName();
	}

	public String getCurrentUserId() {
		final TeamFoundationIdentity userIdentity = TFSUtil.getVersionControlClient().getConnection().getAuthorizedIdentity();
		return userIdentity.getTeamFoundationID().toString();
	}

	public String getCurrentUserDomain() {
		final TeamFoundationIdentity userIdentity = TFSUtil.getVersionControlClient().getConnection().getAuthorizedIdentity();
		return (String) userIdentity.getProperty("Domain");
	}

}
