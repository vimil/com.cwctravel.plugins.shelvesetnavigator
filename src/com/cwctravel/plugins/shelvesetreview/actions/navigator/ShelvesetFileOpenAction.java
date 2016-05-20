package com.cwctravel.plugins.shelvesetreview.actions.navigator;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;

public class ShelvesetFileOpenAction extends Action implements ISelectionChangedListener {
	private TreeViewer treeViewer;
	private ISelectionProvider provider;
	private ShelvesetFileItem shelvesetFileItem;

	public ShelvesetFileOpenAction(IWorkbenchPartSite workbenchPartSite, ISelectionProvider provider,
			TreeViewer treeViewer) {
		setText("Open");
		this.treeViewer = treeViewer;
		this.provider = provider;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
	}

	public boolean isEnabled() {
		boolean result = false;
		ISelection selection = provider.getSelection();
		if (!selection.isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.size() == 1) {
				Object element = structuredSelection.getFirstElement();
				if (element instanceof ShelvesetFileItem) {
					shelvesetFileItem = (ShelvesetFileItem) element;
					result = true;
				}
			}
		}
		return result;
	}

	public void run() {
		try {
			IFileStore fileStore = EFS.getStore(shelvesetFileItem.getURI());
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditorOnFileStore(page, fileStore);
		} catch (CoreException e) {
			ShelvesetReviewPlugin.log(IStatus.ERROR, e.getMessage(), e);
		}
	}

}
