package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cwctravel.plugins.shelvesetreview.constants.ShelvesetPropertyConstants;
import com.cwctravel.plugins.shelvesetreview.util.ShelvesetUtil;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class ShelvesetGroupItem implements Comparable<ShelvesetGroupItem> {

	public static final int GROUP_TYPE_USER_SHELVESETS = 0;
	public static final int GROUP_TYPE_REVIEWER_SHELVESETS = 1;
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

	public int getGroupType() {
		return groupType;
	}

	public String getName() {
		switch (groupType) {
			case GROUP_TYPE_USER_SHELVESETS:
				return "My Shelvesets";
			case GROUP_TYPE_REVIEWER_SHELVESETS:
				return "Review Shelvesets";
			case GROUP_TYPE_INACTIVE_SHELVESETS:
				return "Discarded Shelvesets";
			default:
				return "";
		}
	}

	public boolean isUserGroup() {
		return groupType == GROUP_TYPE_REVIEWER_SHELVESETS;
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
			String currentUserId = TFSUtil.getCurrentUserId();
			switch (groupType) {
				case GROUP_TYPE_USER_SHELVESETS: {
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
				case GROUP_TYPE_REVIEWER_SHELVESETS: {
					shelvesetUserItems = new ArrayList<ShelvesetUserItem>();
					for (Map.Entry<String, List<Shelveset>> userShelvesetItemsMapEntry : userShelvesetItemsMap
							.entrySet()) {

						String shelvesetOwner = userShelvesetItemsMapEntry.getKey();
						List<Shelveset> userShelvesetList = userShelvesetItemsMap.get(shelvesetOwner);
						if (userShelvesetList != null) {
							ShelvesetUserItem shelvesetUserItem = null;
							List<ShelvesetItem> shelvesetItems = new ArrayList<ShelvesetItem>();
							for (Shelveset shelveset : userShelvesetList) {
								if (!ShelvesetUtil.isShelvesetInactive(shelveset)) {
									boolean isCurrentUserShelvesetReviewer = false;
									String[] reviewerIds = ShelvesetUtil.getPropertyAsStringArray(shelveset,
											ShelvesetPropertyConstants.SHELVESET_PROPERTY_REVIEWER_IDS);
									for (String reviewerId : reviewerIds) {
										if (TFSUtil.userIdsSame(currentUserId, reviewerId)) {
											isCurrentUserShelvesetReviewer = true;
											break;
										}
									}
									if (isCurrentUserShelvesetReviewer) {
										if (shelvesetUserItem == null) {
											shelvesetUserItem = new ShelvesetUserItem(this, shelvesetOwner,
													shelvesetItems);
											shelvesetUserItems.add(shelvesetUserItem);
										}
										shelvesetItems
												.add(new ShelvesetItem(parent, this, shelvesetUserItem, shelveset));
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
			if (shelvesetItem.getName().equals(shelvesetName)
					&& shelvesetItem.getOwnerName().equals(shelvesetOwnerName)) {
				result = shelvesetItem;
				break;
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

}
