package com.cwctravel.plugins.shelvesetreview.navigator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.events.ShelvesetContainerRefreshEvent;
import com.cwctravel.plugins.shelvesetreview.events.ShelvesetItemRefreshEvent;
import com.cwctravel.plugins.shelvesetreview.filesystem.TFSFileStore;
import com.cwctravel.plugins.shelvesetreview.jobs.ShelvesetItemsRefreshJob;
import com.cwctravel.plugins.shelvesetreview.listeners.IShelvesetContainerRefreshListener;
import com.cwctravel.plugins.shelvesetreview.listeners.IShelvesetItemRefreshListener;
import com.cwctravel.plugins.shelvesetreview.navigator.model.CodeReviewGroupItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.EditorUtil;

public class ShelvesetNavigator extends CommonNavigator
		implements ITreeViewerListener, IOpenListener, IShelvesetItemRefreshListener, IShelvesetContainerRefreshListener {
	private ShelvesetGroupItemContainer shelvesetGroupItemContainer;
	private CodeReviewGroupItemContainer codeReviewItemContainer;
	private ShelvesetItem shelvesetItemToExpand = null;

	private boolean initialRefreshComplete;

	public ShelvesetNavigator() {
		shelvesetGroupItemContainer = ShelvesetReviewPlugin.getDefault().getShelvesetGroupItemContainer();
		codeReviewItemContainer = ShelvesetReviewPlugin.getDefault().getCodeReviewItemContainer();

		ShelvesetReviewPlugin.getDefault().addShelvesetItemRefreshListener(this);
		ShelvesetReviewPlugin.getDefault().addShelvesetContainerRefreshListener(this);
		if (shelvesetGroupItemContainer.isInitialRefreshComplete()) {
			refreshShelvesetItemsOfOpenEditors();
			initialRefreshComplete = true;
		}
	}

	public void dispose() {
		ShelvesetReviewPlugin.getDefault().removeShelvesetItemRefreshListener(this);
		ShelvesetReviewPlugin.getDefault().removeShelvesetContainerRefreshListener(this);
	}

	protected IAdaptable getInitialInput() {
		CommonViewer commonViewer = getCommonViewer();
		commonViewer.addTreeListener(this);
		commonViewer.addOpenListener(this);
		return ShelvesetReviewPlugin.getDefault().getBaseItemContainer();
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
		if (!initialRefreshComplete) {
			refreshShelvesetItemsOfOpenEditors();
			initialRefreshComplete = true;
		}
		getCommonViewer().refresh(true);
	}

	private void refreshShelvesetItemsOfOpenEditors() {
		List<IEditorPart> editorParts = EditorUtil.getTFSFileStoreEditors();
		Set<ShelvesetItem> shelvesetItemsToRefresh = new HashSet<ShelvesetItem>();
		ShelvesetGroupItemContainer shelvesetGroupItemContainer = ShelvesetReviewPlugin.getDefault().getShelvesetGroupItemContainer();
		for (IEditorPart editor : editorParts) {
			FileStoreEditorInput editorInput = (FileStoreEditorInput) editor.getEditorInput();
			try {
				TFSFileStore tfsFileStore = (TFSFileStore) EFS.getStore(editorInput.getURI());
				ShelvesetItem shelvesetItem = shelvesetGroupItemContainer.findShelvesetItem(tfsFileStore.getShelvesetName(),
						tfsFileStore.getShelvesetOwnerName());
				if (shelvesetItem != null) {
					shelvesetItemsToRefresh.add(shelvesetItem);
				}

			} catch (CoreException e) {
				ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
			}
		}
		if (!shelvesetItemsToRefresh.isEmpty()) {
			new ShelvesetItemsRefreshJob(shelvesetItemsToRefresh).schedule();
		}
	}

}
