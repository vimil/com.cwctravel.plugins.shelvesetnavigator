package com.cwctravel.plugins.shelvesetreview.navigator;

import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IPipelinedTreeContentProvider;
import org.eclipse.ui.navigator.PipelinedShapeModification;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;

import com.cwctravel.plugins.shelvesetreview.navigator.model.IItemContainer;

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
		if (inputElement instanceof IItemContainer<?, ?>) {
			IItemContainer<?, ?> itemContainer = (IItemContainer<?, ?>) inputElement;
			result = itemContainer.getChildren().toArray(new Object[0]);
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
		if (element instanceof IItemContainer<?, ?>) {
			IItemContainer<?, ?> itemContainer = (IItemContainer<?, ?>) element;
			result = itemContainer.getItemParent();
		}
		return result;
	}

	@Override
	public boolean hasChildren(Object element) {
		boolean result = false;
		if (element instanceof IItemContainer<?, ?>) {
			IItemContainer<?, ?> itemContainer = (IItemContainer<?, ?>) element;
			result = itemContainer.hasChildren();
		} else {
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
