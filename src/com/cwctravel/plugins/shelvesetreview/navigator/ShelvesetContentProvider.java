package com.cwctravel.plugins.shelvesetreview.navigator;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IPipelinedTreeContentProvider;
import org.eclipse.ui.navigator.PipelinedShapeModification;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetResourceItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetUserItem;

public class ShelvesetContentProvider implements IPipelinedTreeContentProvider {

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] result = null;
		if(inputElement instanceof ShelvesetGroupItemContainer) {
			ShelvesetGroupItemContainer shelvesetGroupItemContainer = (ShelvesetGroupItemContainer)inputElement;
			List<ShelvesetGroupItem> shelvesetGroupItems = shelvesetGroupItemContainer.getShelvesetGroupItems();
			result = shelvesetGroupItems.toArray(new ShelvesetGroupItem[0]);
		}
		else if(inputElement instanceof ShelvesetGroupItem) {
			ShelvesetGroupItem shelvesetGroupItem = (ShelvesetGroupItem)inputElement;
			if(shelvesetGroupItem.isUserGroup()) {
				List<ShelvesetUserItem> shelvesetUserItems = shelvesetGroupItem.getShelvesetUserItems();
				result = shelvesetUserItems.toArray(new ShelvesetUserItem[0]);
			}
			else {
				List<ShelvesetItem> shelvesetItems = shelvesetGroupItem.getShelvesetItems();
				result = shelvesetItems.toArray(new ShelvesetItem[0]);
			}
		}
		else if(inputElement instanceof ShelvesetUserItem) {
			ShelvesetUserItem shelvesetUserItem = (ShelvesetUserItem)inputElement;
			List<ShelvesetItem> shelvesetItems = shelvesetUserItem.getShelvesetItems();
			result = shelvesetItems.toArray(new ShelvesetItem[0]);
		}
		else if(inputElement instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem)inputElement;
			List<ShelvesetResourceItem> shelvesetResourceItems = shelvesetItem.getChildren();
			if(shelvesetResourceItems != null) {
				result = shelvesetResourceItems.toArray(new ShelvesetResourceItem[0]);
			}
		}
		else if(inputElement instanceof ShelvesetFolderItem) {
			ShelvesetFolderItem shelvesetFolderItem = (ShelvesetFolderItem)inputElement;
			List<ShelvesetResourceItem> shelvesetResourceItems = shelvesetFolderItem.getChildren();
			result = shelvesetResourceItems.toArray(new ShelvesetResourceItem[0]);
		}
		return result;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return getElements(parentElement);
	}

	@Override
	public Object getParent(Object element) {
		Object result = null;
		if(element instanceof ShelvesetGroupItem) {
			ShelvesetGroupItem shelvesetGroupItem = (ShelvesetGroupItem)element;
			result = shelvesetGroupItem.getParent();
		}
		else if(element instanceof ShelvesetUserItem) {
			ShelvesetUserItem shelvesetUserItem = (ShelvesetUserItem)element;
			result = shelvesetUserItem.getParentGroup();
		}
		else if(element instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem)element;
			ShelvesetUserItem shelvesetUser = shelvesetItem.getParentUser();
			if(shelvesetUser != null) {
				result = shelvesetUser;
			}
			else {
				result = shelvesetItem.getParentGroup();
			}
		}
		else if(element instanceof ShelvesetResourceItem) {
			ShelvesetResourceItem shelvesetResourceItem = (ShelvesetResourceItem)element;
			result = shelvesetResourceItem.getParentFolder();
			if(result == null) {
				result = shelvesetResourceItem.getParent();
			}
		}
		return result;
	}

	@Override
	public boolean hasChildren(Object element) {
		boolean result = false;
		if(element instanceof ShelvesetGroupItem || element instanceof ShelvesetUserItem || element instanceof ShelvesetItem) {
			result = true;
		}
		else {
			Object[] children = getChildren(element);
			result = children != null && children.length > 0;
		}

		return result;
	}

	@Override
	public void init(ICommonContentExtensionSite aConfig) {

	}

	@Override
	public void restoreState(IMemento aMemento) {

	}

	@Override
	public void saveState(IMemento aMemento) {

	}

	@Override
	public void getPipelinedChildren(Object aParent, Set theCurrentChildren) {

	}

	@Override
	public void getPipelinedElements(Object anInput, Set theCurrentElements) {

	}

	@Override
	public Object getPipelinedParent(Object anObject, Object aSuggestedParent) {
		return null;
	}

	@Override
	public PipelinedShapeModification interceptAdd(PipelinedShapeModification anAddModification) {
		return null;
	}

	@Override
	public PipelinedShapeModification interceptRemove(PipelinedShapeModification aRemoveModification) {
		return null;
	}

	@Override
	public boolean interceptRefresh(PipelinedViewerUpdate aRefreshSynchronization) {
		return false;
	}

	@Override
	public boolean interceptUpdate(PipelinedViewerUpdate anUpdateSynchronization) {
		return false;
	}

}
