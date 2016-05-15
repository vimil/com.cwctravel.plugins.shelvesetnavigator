package com.cwctravel.plugins.shelvesetnavigator.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.eclipse.core.runtime.IStatus;

import com.cwctravel.plugins.shelvesetnavigator.ShelvesetNavigatorPlugin;
import com.microsoft.tfs.client.common.repository.TFSRepository;
import com.microsoft.tfs.client.eclipse.TFSEclipseClientPlugin;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
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

	public static String getCurrentUser() {
		final TeamFoundationIdentity userIdentity = getVersionControlClient().getConnection().getAuthorizedIdentity();
		return userIdentity.getDisplayName();
	}

	public static URI encodeURI(String path, String shelvesetName, String shelvesetOwnerName, String downloadURL) {
		URI result = null;
		try {
			String hash = computeMD5Hash(downloadURL);
			result = new URI("tfs://" + Base64.getUrlEncoder().encodeToString(
					(path + ";" + downloadURL + ";" + hash + ";" + shelvesetName + ";" + shelvesetOwnerName)
							.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException | URISyntaxException | NoSuchAlgorithmException uEE) {
			ShelvesetNavigatorPlugin.log(IStatus.WARNING, uEE.getMessage(), uEE);
		}

		return result;
	}

	public static String[] decodeURI(URI uri) {
		String[] result = null;
		try {
			String decodedStr = new String(Base64.getUrlDecoder().decode(uri.getAuthority()), "UTF-8");
			result = decodedStr.split(";");

		} catch (UnsupportedEncodingException e) {
			ShelvesetNavigatorPlugin.log(IStatus.WARNING, e.getMessage(), e);
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
}
