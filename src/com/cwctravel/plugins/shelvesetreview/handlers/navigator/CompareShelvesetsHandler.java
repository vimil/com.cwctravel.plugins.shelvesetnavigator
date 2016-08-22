package com.cwctravel.plugins.shelvesetreview.handlers.navigator;

import java.util.Iterator;

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

import com.cwctravel.plugins.shelvesetreview.compare.CompareShelvesetItemInput;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class CompareShelvesetsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			if (treeSelection.size() == 2) {
				@SuppressWarnings("unchecked")
				Iterator<ShelvesetItem> treeSelectionItr = treeSelection.iterator();
				ShelvesetItem shelvesetItem1 = (ShelvesetItem) treeSelectionItr.next();
				ShelvesetItem shelvesetItem2 = (ShelvesetItem) treeSelectionItr.next();

				shelvesetItem1.scheduleRefresh(true);
				shelvesetItem2.scheduleRefresh(true);

				CompareShelvesetItemInput compare = new CompareShelvesetItemInput(shelvesetItem1, shelvesetItem2);
				IWorkbench wb = PlatformUI.getWorkbench();
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				CompareUI.openCompareEditorOnPage(compare, win.getActivePage());
			}
		}
		return null;
	}
}