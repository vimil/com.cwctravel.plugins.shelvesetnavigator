package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.util.IconManager;
import com.cwctravel.plugins.shelvesetreview.util.IdentityUtil;
import com.cwctravel.plugins.shelvesetreview.util.ShelvesetUtil;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class ShelvesetGroupItem implements Comparable<ShelvesetGroupItem>, IAdaptable, IItemContainer<ShelvesetGroupItemContainer, Object> {

	public static final int GROUP_TYPE_CURRENT_USER_SHELVESETS = 0;
	public static final int GROUP_TYPE_OTHER_USER_SHELVESETS = 1;
	public static final int GROUP_TYPE_INACTIVE_SHELVESETS = 2;

	private final ShelvesetGroupItemContainer parent;

	private final int groupType;

	private boolean isChildrenRefreshed;

	private List<ShelvesetItem> shelvesetItems;

	private List<ShelvesetUserItem> shelvesetUserItems;

	public ShelvesetGroupItem(ShelvesetGroupItemContainer parent, int groupType) {
		this.parent = parent;
		this.groupType = groupType;
		this.shelvesetItems = new ArrayList<ShelvesetItem>();
		this.shelvesetUserItems = new ArrayList<ShelvesetUserItem>();
	}

	public ShelvesetGroupItemContainer getParent() {
		return parent;
	}

	public ShelvesetGroupItemContainer getItemParent() {
		return getParent();
	}

	public int getGroupType() {
		return groupType;
	}

	public String getName() {
		switch (groupType) {
			case GROUP_TYPE_CURRENT_USER_SHELVESETS:
				return "My Shelvesets";
			case GROUP_TYPE_OTHER_USER_SHELVESETS:
				return "Other Shelvesets";
			case GROUP_TYPE_INACTIVE_SHELVESETS:
				return "Discarded Shelvesets";
			default:
				return "";
		}
	}

	public boolean isUserGroup() {
		return groupType == GROUP_TYPE_OTHER_USER_SHELVESETS;
	}

	public List<ShelvesetItem> getShelvesetItems() {
		return shelvesetItems;
	}

	public List<ShelvesetUserItem> getShelvesetUserItems() {
		return shelvesetUserItems;
	}

	public boolean isChildrenRefreshed() {
		return isChildrenRefreshed;
	}

	public void createShelvesetItems(Map<String, List<Shelveset>> userShelvesetItemsMap) {
		shelvesetItems.clear();
		if (userShelvesetItemsMap != null) {
			String currentUserId = IdentityUtil.getCurrentUserName();
			switch (groupType) {
				case GROUP_TYPE_CURRENT_USER_SHELVESETS: {
					shelvesetItems = new ArrayList<ShelvesetItem>();
					List<Shelveset> currentUserShelvesets = userShelvesetItemsMap.get(currentUserId);
					if (currentUserShelvesets != null) {
						for (Shelveset shelveset : currentUserShelvesets) {
							if (!ShelvesetUtil.isShelvesetInactive(shelveset)) {
								shelvesetItems.add(new ShelvesetItem(parent, this, shelveset));
							}
						}
					}
					break;
				}
				case GROUP_TYPE_OTHER_USER_SHELVESETS: {
					shelvesetUserItems = new ArrayList<ShelvesetUserItem>();
					for (Map.Entry<String, List<Shelveset>> userShelvesetItemsMapEntry : userShelvesetItemsMap.entrySet()) {

						String shelvesetOwner = userShelvesetItemsMapEntry.getKey();
						List<Shelveset> userShelvesetList = userShelvesetItemsMap.get(shelvesetOwner);
						if (userShelvesetList != null) {
							ShelvesetUserItem shelvesetUserItem = null;
							List<ShelvesetUserCategoryItem> shelvesetUserCategoryItems = null;
							ShelvesetUserCategoryItem unassignedShelvesetUserCategoryItem = null;
							ShelvesetUserCategoryItem pendingReviewShelvesetUserCategoryItem = null;
							List<ShelvesetItem> unassignedShelvesetItems = null;
							List<ShelvesetItem> pendingReviewShelvesetItems = null;

							for (Shelveset shelveset : userShelvesetList) {
								if (!ShelvesetUtil.isShelvesetInactive(shelveset)
										&& !IdentityUtil.userNamesSame(currentUserId, shelveset.getOwnerName())) {
									if (shelvesetUserItem == null) {
										shelvesetUserCategoryItems = new ArrayList<ShelvesetUserCategoryItem>();
										shelvesetUserItem = new ShelvesetUserItem(this, shelvesetOwner, shelvesetUserCategoryItems);

										shelvesetUserItems.add(shelvesetUserItem);
									}

									if (ShelvesetUtil.isCurrentUserReviewer(shelveset, null)) {
										if (pendingReviewShelvesetUserCategoryItem == null) {
											pendingReviewShelvesetItems = new ArrayList<ShelvesetItem>();
											pendingReviewShelvesetUserCategoryItem = new ShelvesetUserCategoryItem("Pending Review",
													IconManager.PENDING_REVIEW_USER_CATEGORY_ICON_ID, shelvesetUserItem, pendingReviewShelvesetItems);
											shelvesetUserCategoryItems.add(pendingReviewShelvesetUserCategoryItem);
										}
										pendingReviewShelvesetItems.add(new ShelvesetItem(parent, this, shelvesetUserItem,
												pendingReviewShelvesetUserCategoryItem, shelveset));
									} else {
										if (unassignedShelvesetUserCategoryItem == null) {
											unassignedShelvesetItems = new ArrayList<ShelvesetItem>();
											unassignedShelvesetUserCategoryItem = new ShelvesetUserCategoryItem("Unassigned",
													IconManager.UNASSIGNED_SHELVESET_USER_CATEGORY_ICON_ID, shelvesetUserItem,
													unassignedShelvesetItems);
											shelvesetUserCategoryItems.add(unassignedShelvesetUserCategoryItem);
										}
										unassignedShelvesetItems.add(
												new ShelvesetItem(parent, this, shelvesetUserItem, unassignedShelvesetUserCategoryItem, shelveset));
									}
								}
							}
						}
					}
					break;
				}
				case GROUP_TYPE_INACTIVE_SHELVESETS: {
					shelvesetItems = new ArrayList<ShelvesetItem>();
					List<Shelveset> currentUserShelvesets = userShelvesetItemsMap.get(currentUserId);
					if (currentUserShelvesets != null) {
						for (Shelveset shelveset : currentUserShelvesets) {
							if (ShelvesetUtil.isShelvesetInactive(shelveset)) {
								shelvesetItems.add(new ShelvesetItem(parent, this, shelveset));
							}
						}
					}
					break;
				}
			}
		}
	}

	public ShelvesetItem findShelvesetItem(String shelvesetName, String shelvesetOwnerName) {
		ShelvesetItem result = null;

		for (ShelvesetItem shelvesetItem : shelvesetItems) {
			if (shelvesetItem.getName().equals(shelvesetName) && shelvesetItem.getOwnerName().equals(shelvesetOwnerName)) {
				result = shelvesetItem;
				break;
			}
		}

		if (shelvesetUserItems != null) {
			for (ShelvesetUserItem shelvesetUserItem : shelvesetUserItems) {
				result = shelvesetUserItem.findShelvesetItem(shelvesetName, shelvesetOwnerName);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + groupType;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ShelvesetGroupItem)) {
			return false;
		}
		ShelvesetGroupItem other = (ShelvesetGroupItem) obj;
		if (groupType != other.groupType) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(ShelvesetGroupItem o) {
		return groupType - o.groupType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (ShelvesetGroupItem.class.equals(adapter)) {
			return (T) this;
		} else if (ShelvesetGroupItemContainer.class.equals(adapter)) {
			return (T) getParent();
		}
		return null;
	}

	@Override
	public List<Object> getChildren() {
		List<Object> result = new ArrayList<Object>();
		if (isUserGroup()) {
			result.addAll(getShelvesetUserItems());
		} else {
			result.addAll(getShelvesetItems());
		}

		return result;
	}

	public boolean hasChildren() {
		return true;
	}

	public String getText() {
		return getName();
	}

	@Override
	public Image getImage() {
		Image image = null;
		switch (getGroupType()) {
			case ShelvesetGroupItem.GROUP_TYPE_CURRENT_USER_SHELVESETS:
				image = IconManager.getIcon(IconManager.USER_GROUP_ICON_ID);
				break;
			case ShelvesetGroupItem.GROUP_TYPE_OTHER_USER_SHELVESETS:
				image = IconManager.getIcon(IconManager.REVIEW_GROUP_ICON_ID);
				break;
			case ShelvesetGroupItem.GROUP_TYPE_INACTIVE_SHELVESETS:
				image = IconManager.getIcon(IconManager.INACTIVE_GROUP_ICON_ID);
				break;
		}

		return image;
	}

	@Override
	public int itemCompareTo(IItemContainer<?, ?> itemContainer) {
		if (itemContainer instanceof ShelvesetGroupItem) {
			return compareTo((ShelvesetGroupItem) itemContainer);
		} else if (itemContainer instanceof CodeReviewGroupItemContainer) {
			return -1;
		}

		return 0;
	}
}
