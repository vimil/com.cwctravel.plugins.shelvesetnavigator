package com.cwctravel.plugins.shelvesetreview.navigator.model;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.util.StringUtil;

public abstract class ShelvesetResourceItem implements IAdaptable {
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
		String path = getPath();
		if (path != null) {
			result = prime * result + path.hashCode();
		}
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
		String otherPath = other.getPath();
		String path = getPath();
		if (StringUtil.equals(otherPath, path) && other.getShelvesetName().equals(getShelvesetName())
				&& other.getShelvesetOwnerName().equals(getShelvesetOwnerName())) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return (T) getParent().getAdapter(adapter);
	}

	public Image getImage() {
		Image image = null;
		if (this instanceof IItemContainer<?, ?>) {
			IItemContainer<?, ?> itemContainer = (IItemContainer<?, ?>) this;
			image = itemContainer.getImage();
		}
		return image;
	}
}
