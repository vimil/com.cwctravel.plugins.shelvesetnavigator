package com.cwctravel.plugins.shelvesetreview.navigator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.navigator.CommonNavigator;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.events.ShelvesetContainerRefreshEvent;
import com.cwctravel.plugins.shelvesetreview.events.ShelvesetItemRefreshEvent;
import com.cwctravel.plugins.shelvesetreview.listeners.IShelvesetContainerRefreshListener;
import com.cwctravel.plugins.shelvesetreview.listeners.IShelvesetItemRefreshListener;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class ShelvesetNavigator extends CommonNavigator
		implements ITreeViewerListener, IOpenListener, IShelvesetItemRefreshListener, IShelvesetContainerRefreshListener {
	private ShelvesetGroupItemContainer shelvesetGroupItemContainer;
	private ShelvesetItem shelvesetItemToExpand = null;

	public ShelvesetNavigator() {
		shelvesetGroupItemContainer = ShelvesetReviewPlugin.getDefault().getShelvesetGroupItemContainer();
		ShelvesetReviewPlugin.getDefault().addShelvesetItemRefreshListener(this);
		ShelvesetReviewPlugin.getDefault().addShelvesetContainerRefreshListener(this);
	}

	public void dispose() {
		ShelvesetReviewPlugin.getDefault().removeShelvesetItemRefreshListener(this);
		ShelvesetReviewPlugin.getDefault().removeShelvesetContainerRefreshListener(this);
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
				shelvesetItemToExpand = shelvesetItem;
				shelvesetItem.scheduleRefresh();
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
						shelvesetItemToExpand = shelvesetItem;
						shelvesetItem.scheduleRefresh();
					}
				}
			}
		}

	}

	@Override
	public void onShelvesetItemRefreshed(ShelvesetItemRefreshEvent event) {
		if (shelvesetItemToExpand == event.getShelvesetItem()) {
			getCommonViewer().expandToLevel(shelvesetItemToExpand, 1);
			shelvesetItemToExpand = null;
		} else {
			getCommonViewer().refresh(event.getShelvesetItem(), true);
		}
	}

	@Override
	public void onShelvesetContainerRefreshed(ShelvesetContainerRefreshEvent event) {
		getCommonViewer().refresh(true);
	}

}
