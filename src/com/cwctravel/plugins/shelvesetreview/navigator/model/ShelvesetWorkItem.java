package com.cwctravel.plugins.shelvesetreview.navigator.model;

import com.cwctravel.plugins.shelvesetreview.WorkItemCache;
import com.cwctravel.plugins.shelvesetreview.rest.workitems.dto.WorkItemInfo;
import com.microsoft.tfs.core.clients.workitem.WorkItem;

public class ShelvesetWorkItem extends ShelvesetResourceItem {
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
		return Integer.toString(getWorkItemID());
	}

	public int getWorkItemID() {
		return workItemInfo.getId();
	}

	public ShelvesetWorkItemContainer getWorkItemContainer() {
		return workItemContainer;
	}

	public WorkItem getWorkItem() {
		return WorkItemCache.getInstance().getWorkItem(getWorkItemID());
	}

}
