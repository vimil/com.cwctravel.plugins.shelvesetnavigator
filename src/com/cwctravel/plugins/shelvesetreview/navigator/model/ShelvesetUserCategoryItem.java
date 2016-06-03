package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

public class ShelvesetUserCategoryItem implements IAdaptable {
	private final String categoryName;
	private final String iconId;

	private final ShelvesetUserItem parentUser;

	private final List<ShelvesetItem> shelvesetItems;

	public ShelvesetUserCategoryItem(String categoryName, String iconId, ShelvesetUserItem parentUser, List<ShelvesetItem> shelvesetItems) {
		this.categoryName = categoryName;
		this.iconId = iconId;
		this.parentUser = parentUser;
		this.shelvesetItems = shelvesetItems;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public String getIconId() {
		return iconId;
	}

	public ShelvesetUserItem getParentUser() {
		return parentUser;
	}

	public List<ShelvesetItem> getShelvesetItems() {
		return shelvesetItems;
	}

	public ShelvesetItem findShelvesetItem(String shelvesetName, String shelvesetOwnerName) {
		ShelvesetItem result = null;

		for (ShelvesetItem shelvesetItem : shelvesetItems) {
			if (shelvesetItem.getName().equals(shelvesetName) && shelvesetItem.getOwnerName().equals(shelvesetOwnerName)) {
				result = shelvesetItem;
				break;
			}
		}
		return result;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (ShelvesetUserCategoryItem.class.equals(adapter)) {
			return this;
		} else if (ShelvesetUserItem.class.equals(adapter)) {
			return getParentUser();
		}
		if (ShelvesetGroupItem.class.equals(adapter)) {
			return getParentUser().getParentGroup();
		} else if (ShelvesetGroupItemContainer.class.equals(adapter)) {
			return getParentUser().getParentGroup().getParent();
		}
		return null;
	}

}
