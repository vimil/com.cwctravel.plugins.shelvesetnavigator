package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.net.URI;

import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.ChangeType;
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

	public String getSourcePath() {
		String sourcePath = pendingChange.getSourceServerItem();
		if (sourcePath == null) {
			sourcePath = pendingChange.getServerItem();
		}
		return sourcePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShelvedDownloadURL() {
		return pendingChange.getShelvedDownloadURL();
	}

	public String getDownloadUrl() {
		return pendingChange.getDownloadURL();
	}

	public URI getURI() {
		String path = getPath();
		String shelvedDownloadURL = pendingChange.getShelvedDownloadURL();
		URI encodedDownloadURL = TFSUtil.encodeURI(path, getShelvesetName(), getShelvesetOwnerName(),
				shelvedDownloadURL);
		return encodedDownloadURL;
	}

	public ChangeType getChangeType() {
		return pendingChange.getChangeType();
	}

}
