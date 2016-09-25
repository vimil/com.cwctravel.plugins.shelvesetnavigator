package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.util.IconManager;

public class ShelvesetUserCategoryItem implements IAdaptable, IItemContainer<ShelvesetUserItem, ShelvesetItem> {
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

	public ShelvesetUserItem getItemParent() {
		return getParentUser();
	}

	public List<ShelvesetItem> getShelvesetItems() {
		return shelvesetItems;
	}

	public ShelvesetItem findShelvesetItem(String shelvesetName, String shelvesetOwnerName) {
		return findShelvesetItem(shelvesetName, shelvesetOwnerName, null);
	}

	public ShelvesetItem findShelvesetItem(String shelvesetName, String shelvesetOwnerName, Calendar creationDate) {
		ShelvesetItem result = null;

		for (ShelvesetItem shelvesetItem : shelvesetItems) {
			if (shelvesetItem.getName().equals(shelvesetName) && shelvesetItem.getOwnerName().equals(shelvesetOwnerName)
					&& (creationDate == null || creationDate.equals(shelvesetItem.getCreationDate()))) {
				result = shelvesetItem;
				break;
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (ShelvesetUserCategoryItem.class.equals(adapter)) {
			return (T) this;
		} else if (ShelvesetUserItem.class.equals(adapter)) {
			return (T) getParentUser();
		}
		if (ShelvesetGroupItem.class.equals(adapter)) {
			return (T) getParentUser().getParentGroup();
		} else if (ShelvesetGroupItemContainer.class.equals(adapter)) {
			return (T) getParentUser().getParentGroup().getParent();
		}
		return null;
	}

	@Override
	public List<ShelvesetItem> getChildren() {
		return getShelvesetItems();
	}

	public boolean hasChildren() {
		return true;
	}

	public String getText() {
		return getCategoryName();
	}

	@Override
	public Image getImage() {
		Image image = IconManager.getIcon(getIconId());
		return image;
	}

	@Override
	public int itemCompareTo(IItemContainer<?, ?> itemContainer) {
		if (itemContainer instanceof ShelvesetUserCategoryItem) {
			return getCategoryName().compareTo(((ShelvesetUserCategoryItem) itemContainer).getCategoryName());
		}
		return 0;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getCategoryName().hashCode();
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShelvesetUserCategoryItem other = (ShelvesetUserCategoryItem) obj;
		if (other.getCategoryName().equals(getCategoryName())) {
			return true;
		}
		return false;
	}
}
