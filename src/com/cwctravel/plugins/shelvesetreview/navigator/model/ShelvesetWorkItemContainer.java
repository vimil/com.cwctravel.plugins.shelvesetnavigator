package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.util.IconManager;

public class ShelvesetWorkItemContainer extends ShelvesetResourceItem implements IItemContainer<ShelvesetItem, ShelvesetWorkItem> {
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

	@Override
	public List<ShelvesetWorkItem> getChildren() {
		return getWorkItems();
	}

	@Override
	public ShelvesetItem getItemParent() {
		return getParent();
	}

	public String getText() {
		return getName();
	}

	@Override
	public Image getImage() {
		Image image = IconManager.getIcon(IconManager.WORKITEMS_ICON_ID);
		return image;
	}

	@Override
	public int itemCompareTo(IItemContainer<?, ?> itemContainer) {
		return 1;
	}

}
