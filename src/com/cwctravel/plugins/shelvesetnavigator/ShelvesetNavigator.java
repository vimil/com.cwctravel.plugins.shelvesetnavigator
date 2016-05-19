package com.cwctravel.plugins.shelvesetnavigator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.ui.navigator.CommonNavigator;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetGroupItemContainer;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItem;

public class ShelvesetNavigator extends CommonNavigator implements ITreeViewerListener {
	private ShelvesetGroupItemContainer shelvesetGroupItemContainer;

	public ShelvesetNavigator() {
		shelvesetGroupItemContainer = ShelvesetNavigatorPlugin.getDefault().getShelvesetGroupItemContainer();
	}

	protected IAdaptable getInitialInput() {
		getCommonViewer().addTreeListener(this);
		return shelvesetGroupItemContainer;
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {

	}

	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		Object element = event.getElement();
		if (element instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) element;
			if (!shelvesetItem.isChildrenRefreshed()) {
				shelvesetItem.refreshShelvesetFileItems(true);
			}
		}

	}

}
