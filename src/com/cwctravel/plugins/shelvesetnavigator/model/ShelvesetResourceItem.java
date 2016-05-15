package com.cwctravel.plugins.shelvesetnavigator.model;

public abstract class ShelvesetResourceItem {
	private final ShelvesetItem parent;

	private ShelvesetFolderItem parentFolder;

	public ShelvesetResourceItem(ShelvesetItem parent) {
		this.parent = parent;
	}

	public ShelvesetItem getParent() {
		return parent;
	}

	public ShelvesetFolderItem getParentFolder() {
		return parentFolder;
	}

	public void setParentFolder(ShelvesetFolderItem parentFolder) {
		this.parentFolder = parentFolder;
	}

	public abstract String getName();
}
