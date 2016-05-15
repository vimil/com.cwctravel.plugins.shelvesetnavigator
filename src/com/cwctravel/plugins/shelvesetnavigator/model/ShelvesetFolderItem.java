package com.cwctravel.plugins.shelvesetnavigator.model;

import java.util.ArrayList;
import java.util.List;

public class ShelvesetFolderItem extends ShelvesetResourceItem {

	private final String folderName;

	private List<ShelvesetResourceItem> children;

	public ShelvesetFolderItem(ShelvesetItem root, String folderName) {
		super(root);
		this.folderName = folderName;

	}

	public void addChild(ShelvesetResourceItem shelvesetResourceItem) {
		getChildren().add(shelvesetResourceItem);
		shelvesetResourceItem.setParentFolder(this);
	}

	public String getName() {
		return folderName;
	}

	public List<ShelvesetResourceItem> getChildren() {
		if (children == null) {
			children = new ArrayList<ShelvesetResourceItem>();
		}
		return children;
	}

	@Override
	public String getPath() {
		String result = folderName;
		ShelvesetFolderItem parentFolder = getParentFolder();
		if (parentFolder != null) {
			result = parentFolder.getPath() + "/" + result;
		}
		return result;
	}
}
