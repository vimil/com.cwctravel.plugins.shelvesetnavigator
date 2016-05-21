package com.cwctravel.plugins.shelvesetreview.handlers.navigator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.cwctravel.plugins.shelvesetreview.jobs.ShelvesetItemDeleteJob;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class DeleteShelvesetHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			if (treeSelection.size() == 1) {
				ShelvesetItem shelvesetItem = (ShelvesetItem) treeSelection.getFirstElement();
				new ShelvesetItemDeleteJob(shelvesetItem).schedule();
			}
		}

		return null;
	}

}
