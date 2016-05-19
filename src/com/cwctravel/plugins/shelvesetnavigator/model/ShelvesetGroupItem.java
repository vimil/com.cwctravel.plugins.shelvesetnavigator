package com.cwctravel.plugins.shelvesetnavigator.model;

import java.util.ArrayList;
import java.util.List;

import com.cwctravel.plugins.shelvesetnavigator.constants.ShelvesetPropertyConstants;
import com.cwctravel.plugins.shelvesetnavigator.util.ShelvesetUtil;
import com.cwctravel.plugins.shelvesetnavigator.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class ShelvesetGroupItem implements Comparable<ShelvesetGroupItem> {

	public static final int GROUP_TYPE_USER_SHELVESETS = 0;
	public static final int GROUP_TYPE_REVIEWER_SHELVESETS = 1;
	public static final int GROUP_TYPE_INACTIVE_SHELVESETS = 2;

	private final ShelvesetGroupItemContainer parent;

	private final int groupType;

	private boolean isChildrenRefreshed;

	private List<ShelvesetItem> shelvesetItems;

	public ShelvesetGroupItem(ShelvesetGroupItemContainer parent, int groupType) {
		this.parent = parent;
		this.groupType = groupType;
		this.shelvesetItems = new ArrayList<ShelvesetItem>();
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
				return "Inactive Shelvesets";
			default:
				return "";
		}
	}

	public List<ShelvesetItem> getShelvesetItems() {
		return shelvesetItems;
	}

	public boolean isChildrenRefreshed() {
		return isChildrenRefreshed;
	}

	public void createShelvesetItems(Shelveset[] shelvesets) {
		shelvesetItems.clear();
		if (shelvesets != null) {
			shelvesetItems = new ArrayList<ShelvesetItem>();
			for (Shelveset shelveset : shelvesets) {
				String shelvesetOwnerName = shelveset.getOwnerName();

				boolean isCurrentUserShelvesetOwner = TFSUtil.getCurrentUserId().equals(shelvesetOwnerName);
				boolean isShelvesetInactive = ShelvesetUtil.getPropertyAsBoolean(shelveset,
						ShelvesetPropertyConstants.SHELVESET_INACTIVE_FLAG, false);

				boolean isCurrentUserShelvesetReviewer = false;
				String[] reviewerIds = ShelvesetUtil.getPropertyAsStringArray(shelveset,
						ShelvesetPropertyConstants.SHELVESET_PROPERTY_REVIEWER_IDS);
				for (String reviewerId : reviewerIds) {
					if (TFSUtil.getCurrentUserId().equals(reviewerId)) {
						isCurrentUserShelvesetReviewer = true;
						break;
					}
				}

				switch (groupType) {
					case GROUP_TYPE_USER_SHELVESETS: {
						if (!isShelvesetInactive && isCurrentUserShelvesetOwner) {
							ShelvesetItem shelvesetItem = new ShelvesetItem(parent, this, shelveset);
							shelvesetItems.add(shelvesetItem);
						}
					}
					case GROUP_TYPE_REVIEWER_SHELVESETS: {
						if (!isShelvesetInactive && isCurrentUserShelvesetReviewer) {
							ShelvesetItem shelvesetItem = new ShelvesetItem(parent, this, shelveset);
							shelvesetItems.add(shelvesetItem);
						}
					}
					case GROUP_TYPE_INACTIVE_SHELVESETS: {
						if (isShelvesetInactive && (isCurrentUserShelvesetOwner || isCurrentUserShelvesetReviewer)) {
							ShelvesetItem shelvesetItem = new ShelvesetItem(parent, this, shelveset);
							shelvesetItems.add(shelvesetItem);
						}
					}
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
