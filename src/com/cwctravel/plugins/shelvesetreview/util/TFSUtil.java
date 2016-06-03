package com.cwctravel.plugins.shelvesetreview.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.microsoft.tfs.client.common.repository.TFSRepository;
import com.microsoft.tfs.client.eclipse.TFSEclipseClientPlugin;
import com.microsoft.tfs.core.TFSConnection;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.webservices.IIdentityManagementService2;
import com.microsoft.tfs.core.clients.webservices.IdentityDescriptor;
import com.microsoft.tfs.core.clients.webservices.IdentitySearchFactor;
import com.microsoft.tfs.core.clients.webservices.MembershipQuery;
import com.microsoft.tfs.core.clients.webservices.ReadIdentityOptions;
import com.microsoft.tfs.core.clients.webservices.TeamFoundationIdentity;

public class TFSUtil {
	public static VersionControlClient getVersionControlClient() {
		VersionControlClient vC = null;
		TFSRepository tfsRepository = TFSEclipseClientPlugin.getDefault().getRepositoryManager().getDefaultRepository();
		if (tfsRepository != null) {
			vC = tfsRepository.getVersionControlClient();
		}
		return vC;
	}

	public static TFSConnection getTFSConnection() {
		TFSConnection result = null;

		TFSRepository tfsRepository = TFSEclipseClientPlugin.getDefault().getRepositoryManager().getDefaultRepository();
		if (tfsRepository != null) {
			result = tfsRepository.getConnection();
		}
		return result;
	}

	public static String getCurrentUserDisplayName() {
		final TeamFoundationIdentity userIdentity = getVersionControlClient().getConnection().getAuthorizedIdentity();
		return userIdentity.getDisplayName();
	}

	public static String getCurrentUserId() {
		final TeamFoundationIdentity userIdentity = getVersionControlClient().getConnection().getAuthorizedIdentity();
		return userIdentity.getUniqueName();
	}

	public static URI encodeURI(String path, String shelvesetName, String shelvesetOwnerName, String downloadURL) {
		URI result = null;
		try {
			String hash = computeMD5Hash(downloadURL);
			result = new URI("tfs://"
					+ Base64.getUrlEncoder().encodeToString(
							(path + ";" + downloadURL + ";" + hash + ";" + shelvesetName + ";" + shelvesetOwnerName).getBytes("UTF-8")));
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

	public static boolean userIdsSame(String userId1, String userId2) {
		if (userId1 == userId2) {
			return true;
		}
		if (userId1 == null) {
			return false;
		}

		if (userId2 == null) {
			return false;
		}

		if (userId1.equals(userId2)) {
			return true;
		}

		String[] userId1Parts = userId1.split("\\\\");
		String[] userId2Parts = userId2.split("\\\\");

		String currentDomain = System.getenv("userdomain");

		String domain1 = currentDomain;
		if (userId1Parts.length == 2) {
			domain1 = userId1Parts[0];
			userId1 = userId1Parts[1];
		} else {
			domain1 = currentDomain;
		}

		String domain2 = currentDomain;
		if (userId2Parts.length == 2) {
			domain2 = userId2Parts[0];
			userId2 = userId2Parts[1];
		}

		return domain1.equalsIgnoreCase(domain2) && userId1.equalsIgnoreCase(userId2);
	}

	public static String findUserId(String userId) {
		String result = null;
		if (userId != null && !userId.isEmpty()) {
			TFSConnection tfsConnection = getTFSConnection();
			if (tfsConnection != null) {
				IIdentityManagementService2 identitySvc = (IIdentityManagementService2) tfsConnection.getClient(IIdentityManagementService2.class);

				if (identitySvc != null) {
					TeamFoundationIdentity teamFoundationIdentity = identitySvc.readIdentity(IdentitySearchFactor.GENERAL, userId,
							MembershipQuery.DIRECT, ReadIdentityOptions.NONE);
					if (teamFoundationIdentity != null && teamFoundationIdentity.isActive() && !teamFoundationIdentity.isContainer()) {
						result = teamFoundationIdentity.getUniqueName();
					}
				}
			}
		}
		return result;
	}

	public static List<TeamFoundationIdentity> getReviewGroupMembers() {
		List<TeamFoundationIdentity> result = new ArrayList<TeamFoundationIdentity>();
		TFSConnection tfsConnection = getTFSConnection();
		if (tfsConnection != null) {
			IIdentityManagementService2 identitySvc = (IIdentityManagementService2) tfsConnection.getClient(IIdentityManagementService2.class);

			if (identitySvc != null) {
				TeamFoundationIdentity reviewerIdentity = identitySvc.readIdentity(IdentitySearchFactor.GENERAL, "Reviewers",
						MembershipQuery.EXPANDED, ReadIdentityOptions.NONE);
				if (reviewerIdentity != null) {
					IdentityDescriptor[] reviewGroupMemberDescriptors = reviewerIdentity.getMembers();
					if (reviewGroupMemberDescriptors != null) {
						for (IdentityDescriptor reviewGroupMemberDescriptor : reviewGroupMemberDescriptors) {

							TeamFoundationIdentity reviewGroupMemberIdentity = identitySvc.readIdentity(IdentitySearchFactor.IDENTIFIER,
									reviewGroupMemberDescriptor.getIdentifier(), MembershipQuery.DIRECT, ReadIdentityOptions.NONE);
							if (reviewGroupMemberIdentity != null) {
								result.add(reviewGroupMemberIdentity);
							}
						}
					}
				}
			}
		}
		return result;
	}

	public static String normalizeUserId(String userId) {
		String result = userId;
		if (result != null) {
			String[] userIdParts = userId.split("\\\\");
			if (userIdParts.length != 2) {
				String currentDomain = System.getenv("userdomain");
				result = currentDomain + "\\" + userId;
			}
		}
		return result;
	}
}
