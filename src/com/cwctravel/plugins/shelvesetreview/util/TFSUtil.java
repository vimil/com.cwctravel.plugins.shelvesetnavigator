package com.cwctravel.plugins.shelvesetreview.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.microsoft.tfs.client.common.repository.TFSRepository;
import com.microsoft.tfs.client.common.server.TFSServer;
import com.microsoft.tfs.client.common.ui.controls.vc.changes.ChangeItem;
import com.microsoft.tfs.client.eclipse.TFSEclipseClientPlugin;
import com.microsoft.tfs.core.TFSConnection;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.RecursionType;
import com.microsoft.tfs.core.clients.versioncontrol.specs.ItemSpec;
import com.microsoft.tfs.core.clients.webservices.IIdentityManagementService2;
import com.microsoft.tfs.core.clients.webservices.IdentitySearchFactor;
import com.microsoft.tfs.core.clients.webservices.MembershipQuery;
import com.microsoft.tfs.core.clients.webservices.ReadIdentityOptions;
import com.microsoft.tfs.core.clients.webservices.TeamFoundationIdentity;
import com.microsoft.tfs.core.clients.workitem.WorkItemClient;

public class TFSUtil {
	private static final Map<String, TeamFoundationIdentity> MEMBER_IDENTITY_MAP = Collections
			.synchronizedMap(new HashMap<String, TeamFoundationIdentity>());

	private static final Map<String, Map<String, Boolean>> GROUP_MEMBERSHIP_MAP = Collections
			.synchronizedMap(new HashMap<String, Map<String, Boolean>>());

	public static TFSRepository getRepository() {
		TFSRepository tfsRepository = TFSEclipseClientPlugin.getDefault().getRepositoryManager().getDefaultRepository();
		return tfsRepository;
	}

	public static VersionControlClient getVersionControlClient() {
		VersionControlClient vC = null;
		TFSRepository tfsRepository = TFSEclipseClientPlugin.getDefault().getRepositoryManager().getDefaultRepository();
		if (tfsRepository != null) {
			vC = tfsRepository.getVersionControlClient();
		}
		return vC;
	}

	public static WorkItemClient getWorkItemClient() {
		WorkItemClient wIC = null;
		TFSRepository tfsRepository = TFSEclipseClientPlugin.getDefault().getRepositoryManager().getDefaultRepository();
		if (tfsRepository != null) {
			wIC = (WorkItemClient) tfsRepository.getConnection().getClient(WorkItemClient.class);
		}
		return wIC;
	}

	public static TFSConnection getTFSConnection() {
		TFSConnection result = null;

		TFSRepository tfsRepository = TFSEclipseClientPlugin.getDefault().getRepositoryManager().getDefaultRepository();
		if (tfsRepository != null) {
			result = tfsRepository.getConnection();
		}
		return result;
	}

	public static TFSServer getTFSServer() {
		TFSServer result = null;
		TFSConnection tfsConnection = getTFSConnection();
		if (tfsConnection != null) {
			result = TFSEclipseClientPlugin.getDefault().getServerManager().getServer(tfsConnection.getBaseURI());
		}
		return result;
	}

	public static String getCurrentUserDisplayName() {
		final TeamFoundationIdentity userIdentity = getVersionControlClient().getConnection().getAuthorizedIdentity();
		return userIdentity.getDisplayName();
	}

	public static String getCurrentUserName() {
		final TeamFoundationIdentity userIdentity = getVersionControlClient().getConnection().getAuthorizedIdentity();
		return userIdentity.getUniqueName();
	}

	public static String getCurrentUserId() {
		final TeamFoundationIdentity userIdentity = getVersionControlClient().getConnection().getAuthorizedIdentity();
		return userIdentity.getTeamFoundationID().toString();
	}

	public static String getCurrentUserDomain() {
		final TeamFoundationIdentity userIdentity = getVersionControlClient().getConnection().getAuthorizedIdentity();
		return (String) userIdentity.getProperty("Domain");
	}

	public static URI encodeURI(String path, String shelvesetName, String shelvesetOwnerName, String downloadURL) {
		URI result = null;
		try {
			String hash = computeMD5Hash(downloadURL);
			result = new URI("tfs://" + Base64.getUrlEncoder()
					.encodeToString((path + ";" + downloadURL + ";" + hash + ";" + shelvesetName + ";" + shelvesetOwnerName).getBytes("UTF-8")));
		} catch (UnsupportedEncodingException | URISyntaxException | NoSuchAlgorithmException uEE) {
			ShelvesetReviewPlugin.log(IStatus.WARNING, uEE.getMessage(), uEE);
		}

		return result;
	}

	public static String[] decodeURI(URI uri) {
		String[] result = null;
		try {
			String decodedStr = new String(Base64.getUrlDecoder().decode(uri.getAuthority()), "UTF-8");
			result = decodedStr.split(";");

		} catch (UnsupportedEncodingException e) {
			ShelvesetReviewPlugin.log(IStatus.WARNING, e.getMessage(), e);
		}

		return result;
	}

	public static String computeMD5Hash(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String result = null;
		byte[] hash = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder(2 * hash.length);
		for (byte b : hash) {
			sb.append(String.format("%02x", b & 0xff));
		}
		result = sb.toString();
		return result;
	}

	public static boolean userNamesSame(String username1, String username2) {
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

		String currentDomain = TFSUtil.getCurrentUserDomain();

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

	/*
	 * public static List<TeamFoundationIdentity> getReviewGroupMembers() {
	 * List<TeamFoundationIdentity> result = new
	 * ArrayList<TeamFoundationIdentity>(); TFSConnection tfsConnection =
	 * getTFSConnection(); if (tfsConnection != null) {
	 * IIdentityManagementService2 identitySvc = (IIdentityManagementService2)
	 * tfsConnection.getClient(IIdentityManagementService2.class);
	 * 
	 * if (identitySvc != null) { TeamFoundationIdentity[] reviewerIdentities =
	 * identitySvc.readIdentities(IdentitySearchFactor.GENERAL, new String[] {
	 * "Reviewers" }, MembershipQuery.EXPANDED, ReadIdentityOptions.NONE)[0]; if
	 * (reviewerIdentities != null) { for (TeamFoundationIdentity
	 * reviewerIdentity : reviewerIdentities) { IdentityDescriptor[]
	 * reviewGroupMemberDescriptors = reviewerIdentity.getMembers(); if
	 * (reviewGroupMemberDescriptors != null) { for (IdentityDescriptor
	 * reviewGroupMemberDescriptor : reviewGroupMemberDescriptors) {
	 * TeamFoundationIdentity reviewGroupMemberIdentity =
	 * identitySvc.readIdentity(IdentitySearchFactor.IDENTIFIER,
	 * reviewGroupMemberDescriptor.getIdentifier(), MembershipQuery.DIRECT,
	 * ReadIdentityOptions.NONE); if (reviewGroupMemberIdentity != null) {
	 * result.add(reviewGroupMemberIdentity); } } } } } } } return result; }
	 */

	public static TeamFoundationIdentity getDefaultReviewersGroup() {
		TeamFoundationIdentity result = getIdentity("Reviewers");
		return result;
	}

	private static TeamFoundationIdentity findIdentity(String memberId) {
		TeamFoundationIdentity result = null;
		TFSConnection tfsConnection = getTFSConnection();
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

	private static boolean isMember(TeamFoundationIdentity groupIdentity, TeamFoundationIdentity memberIdentity) {
		boolean result = false;
		TFSConnection tfsConnection = getTFSConnection();
		if (tfsConnection != null) {
			IIdentityManagementService2 identitySvc = (IIdentityManagementService2) tfsConnection.getClient(IIdentityManagementService2.class);

			if (identitySvc != null) {
				result = identitySvc.isMember(groupIdentity.getDescriptor(), memberIdentity.getDescriptor());

			}
		}
		return result;

	}

	public static String normalizeUserName(String userId) {
		String result = userId;
		if (result != null) {
			String[] userIdParts = userId.split("\\\\");
			if (userIdParts.length != 2) {
				String currentDomain = TFSUtil.getCurrentUserDomain();
				result = currentDomain + "\\" + userId;
			}
		}
		return result;
	}

	public static ItemSpec[] getItemSpecs(ChangeItem[] changeItems) {
		if (changeItems == null) {
			return null;
		}

		final ItemSpec[] specs = new ItemSpec[changeItems.length];
		for (int i = 0; i < changeItems.length; i++) {
			specs[i] = new ItemSpec(changeItems[i].getServerItem(), RecursionType.NONE);
		}

		return specs;
	}

	public static TeamFoundationIdentity getIdentity(String memberId) {
		TeamFoundationIdentity result = MEMBER_IDENTITY_MAP.get(memberId);
		if (result == null) {
			result = findIdentity(memberId);
			if (result != null) {
				MEMBER_IDENTITY_MAP.put(memberId, result);
			}
		}
		return result;
	}

	public static boolean isMember(String groupId, String memberId) {
		boolean result = false;
		if (TFSUtil.userNamesSame(groupId, memberId)) {
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
		return result;
	}
}
