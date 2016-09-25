package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.util.IconManager;

public class ShelvesetUserItem implements IAdaptable, IItemContainer<ShelvesetGroupItem, Object> {
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

	public ShelvesetGroupItem getItemParent() {
		return getParentGroup();
	}

	public List<ShelvesetUserCategoryItem> getShelvesetUserCategoryItems() {
		return shelvesetUserCategoryItems;
	}

	public ShelvesetItem findShelvesetItem(String shelvesetName, String shelvesetOwnerName) {
		return findShelvesetItem(shelvesetName, shelvesetOwnerName, null);
	}

	public ShelvesetItem findShelvesetItem(String shelvesetName, String shelvesetOwnerName, Calendar creationDate) {
		ShelvesetItem result = null;
		for (ShelvesetUserCategoryItem shelvesetUserCategoryItem : shelvesetUserCategoryItems) {
			result = shelvesetUserCategoryItem.findShelvesetItem(shelvesetName, shelvesetOwnerName, creationDate);
			if (result != null) {
				break;
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (ShelvesetUserItem.class.equals(adapter)) {
			return (T) this;
		}
		if (ShelvesetGroupItem.class.equals(adapter)) {
			return (T) getParentGroup();
		} else if (ShelvesetGroupItemContainer.class.equals(adapter)) {
			return (T) getParentGroup().getParent();
		}
		return null;
	}

	@Override
	public List<Object> getChildren() {
		List<Object> result = new ArrayList<Object>();
		List<ShelvesetUserCategoryItem> shelvesetUserCategoryItems = getShelvesetUserCategoryItems();
		if (shelvesetUserCategoryItems.size() == 1) {
			ShelvesetUserCategoryItem shelvesetUserCategoryItem = shelvesetUserCategoryItems.get(0);
			result.addAll(shelvesetUserCategoryItem.getShelvesetItems());
		} else {
			result.addAll(shelvesetUserCategoryItems);
		}
		return result;
	}

	public String getText() {
		return getShelvesetOwner();
	}

	@Override
	public Image getImage() {
		Image image = null;
		List<ShelvesetUserCategoryItem> userCategoryItems = getShelvesetUserCategoryItems();
		int userCategoryCount = userCategoryItems.size();
		if (userCategoryCount > 1) {
			image = IconManager.getIcon(IconManager.MIXED_USER_ICON_ID);
		} else if (userCategoryCount == 1) {
			ShelvesetUserCategoryItem shelvesetUserCategoryItem = userCategoryItems.get(0);
			image = IconManager.getIcon(shelvesetUserCategoryItem.getIconId());
		} else {
			image = IconManager.getIcon(IconManager.USER_ICON_ID);
		}

		return image;
	}

	@Override
	public int itemCompareTo(IItemContainer<?, ?> itemContainer) {
		if (itemContainer instanceof ShelvesetUserItem) {
			return getShelvesetOwner().compareTo(((ShelvesetUserItem) itemContainer).getShelvesetOwner());
		}
		return 0;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getShelvesetOwner().hashCode();
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShelvesetUserItem other = (ShelvesetUserItem) obj;
		if (other.getShelvesetOwner().equals(getShelvesetOwner())) {
			return true;
		}
		return false;
	}
}
