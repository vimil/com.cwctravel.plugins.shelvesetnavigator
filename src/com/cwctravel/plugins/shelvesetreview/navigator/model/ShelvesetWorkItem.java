package com.cwctravel.plugins.shelvesetreview.navigator.model;

import com.cwctravel.plugins.shelvesetreview.WorkItemCache;
import com.microsoft.tfs.core.clients.versioncontrol.workspacecache.WorkItemCheckedInfo;
import com.microsoft.tfs.core.clients.workitem.WorkItem;

public class ShelvesetWorkItem extends ShelvesetResourceItem {
	private final ShelvesetWorkItemContainer workItemContainer;
	private final WorkItemCheckedInfo workItemCheckedInfo;

	public ShelvesetWorkItem(ShelvesetWorkItemContainer workItemContainer, WorkItemCheckedInfo workItemCheckedInfo) {
		super(workItemContainer.getParent());
		this.workItemContainer = workItemContainer;
		this.workItemCheckedInfo = workItemCheckedInfo;
	}

	@Override
	public String getName() {
		return getWorkItem().getTitle();
	}

	@Override
	public String getPath() {
		return Integer.toString(getWorkItemID());
	}

	public int getWorkItemID() {
		return workItemCheckedInfo.getID();
	}

	public ShelvesetWorkItemContainer getWorkItemContainer() {
		return workItemContainer;
	}

	public WorkItem getWorkItem() {
		return WorkItemCache.getInstance().getWorkItem(getWorkItemID());
	}

}
