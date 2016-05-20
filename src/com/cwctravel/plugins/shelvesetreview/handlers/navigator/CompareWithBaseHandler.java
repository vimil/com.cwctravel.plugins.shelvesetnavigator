package com.cwctravel.plugins.shelvesetreview.handlers.navigator;

import org.eclipse.compare.CompareUI;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.cwctravel.plugins.shelvesetreview.compare.CompareInput;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;

public class CompareWithBaseHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			if (treeSelection.size() == 1) {
				ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) treeSelection.getFirstElement();

				CompareInput compare = new CompareInput(shelvesetFileItem,
						"Compare [" + shelvesetFileItem.getShelvesetName() + "] " + shelvesetFileItem.getPath());

				IWorkbench wb = PlatformUI.getWorkbench();
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				CompareUI.openCompareEditorOnPage(compare, win.getActivePage());
			}
		}
		return null;
	}
}
