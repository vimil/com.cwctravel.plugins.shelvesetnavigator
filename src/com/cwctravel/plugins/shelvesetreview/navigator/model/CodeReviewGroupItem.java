package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.rest.workitems.dto.WorkItemInfo;
import com.cwctravel.plugins.shelvesetreview.util.IconManager;
import com.cwctravel.plugins.shelvesetreview.util.IdentityUtil;
import com.cwctravel.plugins.shelvesetreview.util.ShelvesetUtil;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class CodeReviewGroupItem
		implements Comparable<CodeReviewGroupItem>, IAdaptable, IItemContainer<CodeReviewGroupItemContainer, CodeReviewItem> {

	public static final int GROUP_TYPE_CURRENT_USER_CODEREVIEWS = 0;
	public static final int GROUP_TYPE_OPEN_CODEREVIEWS = 1;
	public static final int GROUP_TYPE_ACCEPTED_CODEREVIEWS = 2;

	private final CodeReviewGroupItemContainer parent;
	private final int groupType;

	private List<CodeReviewItem> codeReviewItems;

	public CodeReviewGroupItem(CodeReviewGroupItemContainer codeReviewItemContainer, int groupType) {
		this.parent = codeReviewItemContainer;
		this.groupType = groupType;
		this.codeReviewItems = new ArrayList<CodeReviewItem>();
	}

	public void createCodeReviewItems(Map<String, List<Shelveset>> userShelvesetItemsMap, Map<Integer, WorkItemInfo> workItemInfoMap) {
		codeReviewItems.clear();
		if (userShelvesetItemsMap != null) {
			String currentUserId = IdentityUtil.getCurrentUserName();
			switch (groupType) {
				case GROUP_TYPE_CURRENT_USER_CODEREVIEWS: {
					codeReviewItems = new ArrayList<CodeReviewItem>();
					List<Shelveset> currentUserShelvesets = userShelvesetItemsMap.get(currentUserId);
					if (currentUserShelvesets != null) {
						Map<Integer, CodeReviewItem> codeReviewItemsMap = new HashMap<Integer, CodeReviewItem>();
						for (Shelveset shelveset : currentUserShelvesets) {
							if (!ShelvesetUtil.isShelvesetInactive(shelveset)) {
								int codeReviewWorkItemId = ShelvesetUtil.getCodeReviewWorkItemId(shelveset);
								WorkItemInfo workItemInfo = workItemInfoMap.get(codeReviewWorkItemId);
								if (workItemInfo != null) {
									CodeReviewItem codeReviewItem = codeReviewItemsMap.get(codeReviewWorkItemId);
									if (codeReviewItem == null) {
										codeReviewItem = new CodeReviewItem(parent, this, workItemInfo);
										codeReviewItemsMap.put(codeReviewWorkItemId, codeReviewItem);
										codeReviewItems.add(codeReviewItem);
									}
									CodeReviewShelvesetItem codeReviewShelvesetItem = new CodeReviewShelvesetItem(codeReviewItem, shelveset);
									codeReviewItem.addShelvesetItem(codeReviewShelvesetItem);
								}
							}
						}
					}
					break;
				}
			}
		}
	}

	public CodeReviewGroupItemContainer getParent() {
		return parent;
	}

	public List<CodeReviewItem> getCodeReviewItems() {
		return codeReviewItems;
	}

	public int getGroupType() {
		return groupType;
	}

	public String getName() {
		switch (groupType) {
			case GROUP_TYPE_CURRENT_USER_CODEREVIEWS:
				return "My Code Reviews";
			case GROUP_TYPE_OPEN_CODEREVIEWS:
				return "Open Code Reviews";
			case GROUP_TYPE_ACCEPTED_CODEREVIEWS:
				return "Accepted Code Reviews";
			default:
				return "";
		}
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
		if (!(obj instanceof CodeReviewGroupItem)) {
			return false;
		}
		CodeReviewGroupItem other = (CodeReviewGroupItem) obj;
		if (groupType != other.groupType) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(CodeReviewGroupItem o) {
		return groupType - o.groupType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (CodeReviewGroupItem.class.equals(adapter)) {
			return (T) this;
		} else if (CodeReviewGroupItemContainer.class.equals(adapter)) {
			return (T) getParent();
		}
		return null;
	}

	@Override
	public CodeReviewGroupItemContainer getItemParent() {
		return getParent();
	}

	@Override
	public List<CodeReviewItem> getChildren() {
		return getCodeReviewItems();
	}

	public String getText() {
		return getName();
	}

	@Override
	public Image getImage() {
		Image image = null;
		switch (groupType) {
			case GROUP_TYPE_CURRENT_USER_CODEREVIEWS: {
				image = IconManager.getIcon(IconManager.CODEREVIEW_USER_ICON_ID);
				break;
			}
			case GROUP_TYPE_OPEN_CODEREVIEWS: {
				image = IconManager.getIcon(IconManager.CODEREVIEW_OPEN_ICON_ID);
				break;
			}
			case GROUP_TYPE_ACCEPTED_CODEREVIEWS: {
				image = IconManager.getIcon(IconManager.CODEREVIEW_ACCEPTED_ICON_ID);
				break;
			}
		}
		return image;
	}

	@Override
	public int itemCompareTo(IItemContainer<?, ?> itemContainer) {
		return compareTo((CodeReviewGroupItem) itemContainer);
	}
}
