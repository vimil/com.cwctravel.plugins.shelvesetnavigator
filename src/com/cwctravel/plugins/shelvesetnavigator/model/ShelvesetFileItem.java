package com.cwctravel.plugins.shelvesetnavigator.model;

import java.net.URI;

import com.cwctravel.plugins.shelvesetnavigator.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingChange;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingSet;

public class ShelvesetFileItem extends ShelvesetResourceItem {
	private String name;

	private PendingSet pendingSet;
	private PendingChange pendingChange;

	public ShelvesetFileItem(ShelvesetItem root, PendingSet pendingSet, PendingChange pendingChange) {
		super(root);
		this.pendingSet = pendingSet;
		this.pendingChange = pendingChange;
	}

	public String getPath() {
		return pendingChange.getServerItem();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public URI getURI() {
		String path = getPath();
		String shelvedDownloadURL = pendingChange.getShelvedDownloadURL();
		URI encodedDownloadURL = TFSUtil.getURI(path, shelvedDownloadURL);
		return encodedDownloadURL;
	}

}
