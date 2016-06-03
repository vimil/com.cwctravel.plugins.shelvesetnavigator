package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

public class ShelvesetUserItem implements IAdaptable {
	private final String shelvesetOwner;

	private final ShelvesetGroupItem parentGroup;

	private final List<ShelvesetUserCategoryItem> shelvesetUserCategoryItems;

	public ShelvesetUserItem(ShelvesetGroupItem parentGroup, String shelvesetOwner, List<ShelvesetUserCategoryItem> shelvesetUserCategoryItems) {
		this.parentGroup = parentGroup;
		this.shelvesetOwner = shelvesetOwner;
		this.shelvesetUserCategoryItems = shelvesetUserCategoryItems;
	}

	public String getShelvesetOwner() {
		return shelvesetOwner;
	}

	public ShelvesetGroupItem getParentGroup() {
		return parentGroup;
	}

	public List<ShelvesetUserCategoryItem> getShelvesetUserCategoryItems() {
		return shelvesetUserCategoryItems;
	}

	public ShelvesetItem findShelvesetItem(String shelvesetName, String shelvesetOwnerName) {
		ShelvesetItem result = null;
		for (ShelvesetUserCategoryItem shelvesetUserCategoryItem : shelvesetUserCategoryItems) {
			result = shelvesetUserCategoryItem.findShelvesetItem(shelvesetName, shelvesetOwnerName);
			if (result != null) {
				break;
			}
		}
		return result;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (ShelvesetUserItem.class.equals(adapter)) {
			return this;
		}
		if (ShelvesetGroupItem.class.equals(adapter)) {
			return getParentGroup();
		} else if (ShelvesetGroupItemContainer.class.equals(adapter)) {
			return getParentGroup().getParent();
		}
		return null;
	}
}
