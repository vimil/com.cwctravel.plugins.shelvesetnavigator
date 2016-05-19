package com.cwctravel.plugins.shelvesetnavigator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonNavigator;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetGroupItemContainer;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItem;

public class ShelvesetNavigator extends CommonNavigator implements ITreeViewerListener, IOpenListener {
	private ShelvesetGroupItemContainer shelvesetGroupItemContainer;

	public ShelvesetNavigator() {
		shelvesetGroupItemContainer = ShelvesetNavigatorPlugin.getDefault().getShelvesetGroupItemContainer();
	}

	protected IAdaptable getInitialInput() {
		getCommonViewer().addTreeListener(this);
		getCommonViewer().addOpenListener(this);
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

	@Override
	public void open(OpenEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			if (!selection.isEmpty()) {
				Object selectedObject = treeSelection.getFirstElement();
				if (selectedObject instanceof ShelvesetItem) {
					ShelvesetItem shelvesetItem = (ShelvesetItem) selectedObject;
					if (!shelvesetItem.isChildrenRefreshed()) {
						shelvesetItem.refreshShelvesetFileItems(true);
					}
				}
			}
		}

	}

}
