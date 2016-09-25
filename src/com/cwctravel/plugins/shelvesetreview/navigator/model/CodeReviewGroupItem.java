package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.Calendar;
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

	public static final int GROUP_TYPE_CURRENT_USER_CODEREVIEW_REQUESTS = 0;
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
		if (userShelvesetItemsMap != null) {

			switch (groupType) {
				case GROUP_TYPE_CURRENT_USER_CODEREVIEW_REQUESTS: {
					populateCurrentUserCodeReviewItems(userShelvesetItemsMap, workItemInfoMap);
					break;
				}
				case GROUP_TYPE_OPEN_CODEREVIEWS: {
					populateOpenCodeReviewItems(userShelvesetItemsMap, workItemInfoMap);
					break;
				}
			}
		}
	}

	private void populateCurrentUserCodeReviewItems(Map<String, List<Shelveset>> userShelvesetItemsMap, Map<Integer, WorkItemInfo> workItemInfoMap) {
		List<CodeReviewItem> newCodeReviewItems = new ArrayList<CodeReviewItem>();
		String currentUserId = IdentityUtil.getCurrentUserName();
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
							newCodeReviewItems.add(codeReviewItem);
						}
						CodeReviewShelvesetItem currentCodeReviewShelvesetItem = (CodeReviewShelvesetItem) findShelvesetItem(shelveset.getName(),
								shelveset.getOwnerName(), shelveset.getCreationDate());
						if (currentCodeReviewShelvesetItem != null) {
							currentCodeReviewShelvesetItem.reparent(shelveset);
							codeReviewItem.addShelvesetItem(currentCodeReviewShelvesetItem);
						} else {
							CodeReviewShelvesetItem codeReviewShelvesetItem = new CodeReviewShelvesetItem(codeReviewItem, shelveset);
							codeReviewItem.addShelvesetItem(codeReviewShelvesetItem);
						}
					}
				}
			}
		}
		codeReviewItems = newCodeReviewItems;
	}

	private void populateOpenCodeReviewItems(Map<String, List<Shelveset>> userShelvesetItemsMap, Map<Integer, WorkItemInfo> workItemInfoMap) {
		List<CodeReviewItem> newCodeReviewItems = new ArrayList<CodeReviewItem>();
		String currentUserId = IdentityUtil.getCurrentUserName();
		Map<Integer, CodeReviewItem> codeReviewItemsMap = new HashMap<Integer, CodeReviewItem>();
		for (Map.Entry<String, List<Shelveset>> userShelvesetItemsMapEntry : userShelvesetItemsMap.entrySet()) {
			String shelvesetOwnerId = userShelvesetItemsMapEntry.getKey();
			if (!IdentityUtil.userNamesSame(currentUserId, shelvesetOwnerId)) {
				List<Shelveset> shelvesets = userShelvesetItemsMapEntry.getValue();
				for (Shelveset shelveset : shelvesets) {
					if (ShelvesetUtil.isShelvesetOpenForCurrentUser(shelveset)) {
						int codeReviewWorkItemId = ShelvesetUtil.getCodeReviewWorkItemId(shelveset);
						WorkItemInfo workItemInfo = workItemInfoMap.get(codeReviewWorkItemId);
						if (workItemInfo != null) {
							CodeReviewItem codeReviewItem = codeReviewItemsMap.get(codeReviewWorkItemId);
							if (codeReviewItem == null) {
								codeReviewItem = new CodeReviewItem(parent, this, workItemInfo);
								codeReviewItemsMap.put(codeReviewWorkItemId, codeReviewItem);
								newCodeReviewItems.add(codeReviewItem);
							}

							CodeReviewShelvesetItem currentCodeReviewShelvesetItem = (CodeReviewShelvesetItem) findShelvesetItem(shelveset.getName(),
									shelveset.getOwnerName(), shelveset.getCreationDate());
							if (currentCodeReviewShelvesetItem != null) {
								currentCodeReviewShelvesetItem.reparent(shelveset);
								codeReviewItem.addShelvesetItem(currentCodeReviewShelvesetItem);
							} else {
								CodeReviewShelvesetItem codeReviewShelvesetItem = new CodeReviewShelvesetItem(codeReviewItem, shelveset);
								codeReviewItem.addShelvesetItem(codeReviewShelvesetItem);
							}
						}
					}
				}
			}
		}
		codeReviewItems = newCodeReviewItems;
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
			case GROUP_TYPE_CURRENT_USER_CODEREVIEW_REQUESTS:
				return "My Code Review Requests";
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
			case GROUP_TYPE_CURRENT_USER_CODEREVIEW_REQUESTS: {
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

	public ShelvesetItem findShelvesetItem(String shelvesetName, String shelvesetOwnerName) {
		return findShelvesetItem(shelvesetName, shelvesetOwnerName, null);
	}

	public ShelvesetItem findShelvesetItem(String shelvesetName, String shelvesetOwnerName, Calendar creationDate) {
		ShelvesetItem result = null;
		for (CodeReviewItem codeReviewItem : codeReviewItems) {
			result = codeReviewItem.findShelvesetItem(shelvesetName, shelvesetOwnerName, creationDate);
			if (result != null) {
				break;
			}
		}
		return result;
	}
}
