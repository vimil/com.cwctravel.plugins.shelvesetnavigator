package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.WorkItemCache;
import com.cwctravel.plugins.shelvesetreview.rest.workitems.dto.WorkItemInfo;
import com.cwctravel.plugins.shelvesetreview.util.IconManager;
import com.microsoft.tfs.core.clients.workitem.WorkItem;

public class ShelvesetWorkItem extends ShelvesetResourceItem implements IItemContainer<ShelvesetWorkItemContainer, Object> {
	private final ShelvesetWorkItemContainer workItemContainer;
	private final WorkItemInfo workItemInfo;

	public ShelvesetWorkItem(ShelvesetWorkItemContainer workItemContainer, WorkItemInfo workItemInfo) {
		super(workItemContainer.getParent());
		this.workItemContainer = workItemContainer;
		this.workItemInfo = workItemInfo;
	}

	@Override
	public String getName() {
		return workItemInfo.getTitle();
	}

	@Override
	public String getPath() {
		return Integer.toString(getWorkItemId());
	}

	public int getWorkItemId() {
		return workItemInfo.getId();
	}

	public ShelvesetWorkItemContainer getWorkItemContainer() {
		return workItemContainer;
	}

	public WorkItem getWorkItem() {
		return WorkItemCache.getInstance().getWorkItem(getWorkItemId());
	}

	@Override
	public ShelvesetWorkItemContainer getItemParent() {
		return getWorkItemContainer();
	}

	@Override
	public List<Object> getChildren() {
		return Collections.emptyList();
	}

	public String getText() {
		return getName();
	}

	@Override
	public Image getImage() {
		Image image = IconManager.getIcon(IconManager.WORKITEM_ICON_ID);
		return image;
	}

	@Override
	public int itemCompareTo(IItemContainer<?, ?> itemContainer) {
		if (itemContainer instanceof ShelvesetWorkItem) {
			return getWorkItemId() - ((ShelvesetWorkItem) itemContainer).getWorkItemId();
		}
		return 0;
	}

	public void decorate(IDecoration decoration) {
		decoration.addPrefix("[" + getWorkItemId() + "] ");
	}
}
