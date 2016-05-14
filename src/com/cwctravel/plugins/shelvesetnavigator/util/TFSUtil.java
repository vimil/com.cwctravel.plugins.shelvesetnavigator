package com.cwctravel.plugins.shelvesetnavigator.util;

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
}
