package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.rest.workitems.dto.WorkItemInfo;
import com.cwctravel.plugins.shelvesetreview.util.IconManager;
import com.cwctravel.plugins.shelvesetreview.util.IdentityUtil;
import com.cwctravel.plugins.shelvesetreview.util.ShelvesetUtil;
import com.cwctravel.plugins.shelvesetreview.util.WorkItemUtil;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class CodeReviewGroupItemContainer extends PlatformObject implements IItemContainer<Object, CodeReviewGroupItem> {
	private final List<CodeReviewGroupItem> codeReviewGroupItems;

	public CodeReviewGroupItemContainer() {
		codeReviewGroupItems = new ArrayList<>();
		CodeReviewGroupItem userCodeReviewGroupItem = new CodeReviewGroupItem(this, CodeReviewGroupItem.GROUP_TYPE_CURRENT_USER_CODEREVIEWS);
		CodeReviewGroupItem openCodeReviewGroupItem = new CodeReviewGroupItem(this, CodeReviewGroupItem.GROUP_TYPE_OPEN_CODEREVIEWS);
		CodeReviewGroupItem acceptedCodeReviewGroupItem = new CodeReviewGroupItem(this, CodeReviewGroupItem.GROUP_TYPE_ACCEPTED_CODEREVIEWS);

		codeReviewGroupItems.add(userCodeReviewGroupItem);
		codeReviewGroupItems.add(openCodeReviewGroupItem);
		codeReviewGroupItems.add(acceptedCodeReviewGroupItem);
	}

	public List<CodeReviewGroupItem> getCodeReviewGroupItems() {
		return codeReviewGroupItems;
	}

	public void refresh(Map<String, List<Shelveset>> userShelvesetItemsMap, IProgressMonitor monitor) {
		String currentUserId = IdentityUtil.getCurrentUserName();
		Map<Integer, WorkItemInfo> workItemInfoMap = new HashMap<Integer, WorkItemInfo>();
		Set<Integer> workItemIdsSet = new HashSet<Integer>();
		for (Map.Entry<String, List<Shelveset>> userShelvesetItemsMapEntry : userShelvesetItemsMap.entrySet()) {
			String shelvesetOwnerId = userShelvesetItemsMapEntry.getKey();
			List<Shelveset> shelvesets = userShelvesetItemsMapEntry.getValue();
			if (IdentityUtil.userNamesSame(currentUserId, shelvesetOwnerId)) {
				for (Shelveset shelveset : shelvesets) {
					if (!ShelvesetUtil.isShelvesetInactive(shelveset)) {
						int workItemId = ShelvesetUtil.getCodeReviewWorkItemId(shelveset);
						if (workItemId > 0) {
							workItemIdsSet.add(workItemId);
						}
					}
				}
			} else {
				for (Shelveset shelveset : shelvesets) {
					if (ShelvesetUtil.isCurrentUserReviewer(shelveset, null)) {
						int workItemId = ShelvesetUtil.getCodeReviewWorkItemId(shelveset);
						if (workItemId > 0) {
							workItemIdsSet.add(workItemId);
						}
					}
				}
			}
		}

		try {
			List<WorkItemInfo> workItemInfos = WorkItemUtil.retrieveWorkItems(new ArrayList<Integer>(workItemIdsSet));
			for (WorkItemInfo workItemInfo : workItemInfos) {
				workItemInfoMap.put(workItemInfo.getId(), workItemInfo);
			}

			for (CodeReviewGroupItem codeReviewGroupItem : codeReviewGroupItems) {
				codeReviewGroupItem.createCodeReviewItems(userShelvesetItemsMap, workItemInfoMap);
			}

		} catch (IOException iE) {
			ShelvesetReviewPlugin.log(Status.ERROR, iE.getMessage(), iE);
		}
	}

	@Override
	public Object getItemParent() {
		return null;
	}

	@Override
	public List<CodeReviewGroupItem> getChildren() {
		return getCodeReviewGroupItems();
	}

	@Override
	public Image getImage() {
		Image image = IconManager.getIcon(IconManager.CODEREVIEW_ICON_ID);
		return image;
	}

	public String getText() {
		return "Code Reviews";
	}

	@Override
	public int itemCompareTo(IItemContainer<?, ?> itemContainer) {
		if (itemContainer instanceof ShelvesetGroupItem) {
			return 1;
		}
		return 0;
	}
}
