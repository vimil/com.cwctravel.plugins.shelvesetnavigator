package com.cwctravel.plugins.shelvesetnavigator;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItemContainer;

public class ShelvesetContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] result = null;
		if (inputElement instanceof ShelvesetItemContainer) {
			ShelvesetItemContainer shelvesetItemContainer = (ShelvesetItemContainer) inputElement;
			List<ShelvesetItem> shelvesetItems = shelvesetItemContainer.getShelvesetItems();
			result = shelvesetItems.toArray(new ShelvesetItem[0]);
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
		}
		return result;
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

}
