package com.cwctravel.plugins.shelvesetnavigator.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchPartSite;

public class ShelvesetFileOpenAction extends Action implements ISelectionChangedListener {
	private TreeViewer treeViewer;

	public ShelvesetFileOpenAction(IWorkbenchPartSite workbenchPartSite, TreeViewer treeViewer) {
		setText("Open");
		this.treeViewer = treeViewer;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
	}

	public boolean isEnabled() {
		return true;
	}

	public void run() {
		System.out.println("Reached here");
	}

}
