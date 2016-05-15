package com.cwctravel.plugins.shelvesetnavigator;

import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IPipelinedTreeContentProvider;
import org.eclipse.ui.navigator.PipelinedShapeModification;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItemContainer;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetResourceItem;

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
		if (inputElement instanceof ShelvesetItemContainer) {
			ShelvesetItemContainer shelvesetItemContainer = (ShelvesetItemContainer) inputElement;
			List<ShelvesetItem> shelvesetItems = shelvesetItemContainer.getShelvesetItems();
			result = shelvesetItems.toArray(new ShelvesetItem[0]);
		} else if (inputElement instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) inputElement;
			List<ShelvesetResourceItem> shelvesetResourceItems = shelvesetItem.getChildren();
			result = shelvesetResourceItems.toArray(new ShelvesetResourceItem[0]);
		} else if (inputElement instanceof ShelvesetFolderItem) {
			ShelvesetFolderItem shelvesetFolderItem = (ShelvesetFolderItem) inputElement;
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
		if (element instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) element;
			result = shelvesetItem.getParent();
		} else if (element instanceof ShelvesetResourceItem) {
			ShelvesetResourceItem shelvesetResourceItem = (ShelvesetResourceItem) element;
			result = shelvesetResourceItem.getParentFolder();
			if (result == null) {
				result = shelvesetResourceItem.getParent();
			}
		}
		return result;
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

	@Override
	public void init(ICommonContentExtensionSite aConfig) {
		// TODO Auto-generated method stub

	}

	@Override
	public void restoreState(IMemento aMemento) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveState(IMemento aMemento) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getPipelinedChildren(Object aParent, Set theCurrentChildren) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getPipelinedElements(Object anInput, Set theCurrentElements) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getPipelinedParent(Object anObject, Object aSuggestedParent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PipelinedShapeModification interceptAdd(PipelinedShapeModification anAddModification) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PipelinedShapeModification interceptRemove(PipelinedShapeModification aRemoveModification) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean interceptRefresh(PipelinedViewerUpdate aRefreshSynchronization) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean interceptUpdate(PipelinedViewerUpdate anUpdateSynchronization) {
		// TODO Auto-generated method stub
		return false;
	}

}
