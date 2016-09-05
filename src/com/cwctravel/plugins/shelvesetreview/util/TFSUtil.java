package com.cwctravel.plugins.shelvesetreview.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

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
import com.microsoft.tfs.core.clients.workitem.WorkItemClient;

public class TFSUtil {

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

}
