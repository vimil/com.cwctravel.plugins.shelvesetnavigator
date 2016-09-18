package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.WorkItemCache;
import com.cwctravel.plugins.shelvesetreview.rest.workitems.dto.WorkItemInfo;
import com.cwctravel.plugins.shelvesetreview.util.IconManager;
import com.microsoft.tfs.core.clients.workitem.WorkItem;

public class CodeReviewItem implements IAdaptable, IItemContainer<CodeReviewGroupItem, CodeReviewShelvesetItem> {
	private CodeReviewGroupItemContainer parent;
	private CodeReviewGroupItem parentGroup;
	private WorkItemInfo workItemInfo;
	private List<CodeReviewShelvesetItem> codeReviewShelvesetItems;

	public CodeReviewItem(CodeReviewGroupItemContainer parent, CodeReviewGroupItem parentGroup, WorkItemInfo workItemInfo) {
		this.parent = parent;
		this.parentGroup = parentGroup;
		this.workItemInfo = workItemInfo;
	}

	void addShelvesetItem(CodeReviewShelvesetItem codeReviewShelvesetItem) {
		if (codeReviewShelvesetItem != null) {
			getCodeReviewShelvesetItems().add(codeReviewShelvesetItem);
		}
	}

	public CodeReviewGroupItemContainer getParent() {
		return parent;
	}

	public CodeReviewGroupItem getParentGroup() {
		return parentGroup;
	}

	public String getName() {
		return workItemInfo.getTitle();
	}

	public int getWorkItemId() {
		return workItemInfo.getId();
	}

	public WorkItem getWorkItem() {
		return WorkItemCache.getInstance().getWorkItem(getWorkItemId());
	}

	public List<CodeReviewShelvesetItem> getCodeReviewShelvesetItems() {
		if (codeReviewShelvesetItems == null) {
			codeReviewShelvesetItems = new ArrayList<CodeReviewShelvesetItem>();
		}
		return codeReviewShelvesetItems;
	}

	@Override
	public CodeReviewGroupItem getItemParent() {
		return getParentGroup();
	}

	@Override
	public List<CodeReviewShelvesetItem> getChildren() {
		return getCodeReviewShelvesetItems();
	}

	public String getText() {
		return getName();
	}

	@Override
	public Image getImage() {
		return IconManager.getIcon(IconManager.WORKITEM_ICON_ID);
	}

	@Override
	public int itemCompareTo(IItemContainer<?, ?> itemContainer) {
		if (itemContainer instanceof CodeReviewItem) {
			return getWorkItemId() - ((CodeReviewItem) itemContainer).getWorkItemId();
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (CodeReviewItem.class.equals(adapter)) {
			return (T) this;
		} else if (CodeReviewGroupItem.class.equals(adapter)) {
			return (T) getParentGroup();
		} else if (CodeReviewGroupItemContainer.class.equals(adapter)) {
			return (T) getParent();
		}
		return null;
	}

	public void decorate(IDecoration decoration) {
		decoration.addPrefix("[" + getWorkItemId() + "] ");
	}
}
