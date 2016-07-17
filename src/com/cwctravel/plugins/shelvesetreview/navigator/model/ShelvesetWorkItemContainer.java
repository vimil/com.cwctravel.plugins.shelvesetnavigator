package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.List;

public class ShelvesetWorkItemContainer extends ShelvesetResourceItem {
	private List<ShelvesetWorkItem> workItems;

	public ShelvesetWorkItemContainer(ShelvesetItem parent) {
		super(parent);
	}

	@Override
	public String getName() {
		return "Work Items";
	}

	@Override
	public String getPath() {
		return null;
	}

	public List<ShelvesetWorkItem> getWorkItems() {
		if (workItems == null) {
			workItems = new ArrayList<ShelvesetWorkItem>();
		}
		return workItems;
	}

	public void setWorkItems(List<ShelvesetWorkItem> workItems) {
		this.workItems = workItems;
	}

}
