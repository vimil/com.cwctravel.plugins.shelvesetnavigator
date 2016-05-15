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

	public String getShelvesetName() {
		return parent.getName();
	}

	public String getShelvesetOwnerName() {
		return parent.getOwnerName();
	}

	public abstract String getName();

	public abstract String getPath();

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getPath().hashCode();
		result = prime * result + getShelvesetName().hashCode();
		result = prime * result + getShelvesetOwnerName().hashCode();

		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShelvesetResourceItem other = (ShelvesetResourceItem) obj;
		if (other.getPath().equals(getPath()) && other.getShelvesetName().equals(getShelvesetName())
				&& other.getShelvesetOwnerName().equals(getShelvesetOwnerName())) {
			return true;
		}
		return false;
	}
}
