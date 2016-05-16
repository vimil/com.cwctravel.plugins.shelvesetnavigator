package com.cwctravel.plugins.shelvesetnavigator.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetFileItem;

public class ShelvesetFileCompareWithLatestAction extends Action implements ISelectionChangedListener {
	private TreeViewer treeViewer;
	private ISelectionProvider provider;
	private ShelvesetFileItem shelvesetFileItem;

	public ShelvesetFileCompareWithLatestAction(IWorkbenchPartSite workbenchPartSite, ISelectionProvider provider,
			TreeViewer treeViewer) {
		setText("With Latest");
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
		System.out.println("Reached Here");
	}

}
