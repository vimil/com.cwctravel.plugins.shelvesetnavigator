package com.cwctravel.plugins.shelvesetreview.handlers.navigator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.cwctravel.plugins.shelvesetreview.jobs.ShelvesetItemsDeleteJob;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class DeleteShelvesetHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			if (treeSelection.size() >= 1) {
				List<ShelvesetItem> shelvesetItems = new ArrayList<ShelvesetItem>();
				Iterator<?> selectionIterator = treeSelection.iterator();
				while (selectionIterator.hasNext()) {
					ShelvesetItem shelvesetItem = (ShelvesetItem) selectionIterator.next();
					shelvesetItems.add(shelvesetItem);
				}
				new ShelvesetItemsDeleteJob(shelvesetItems).schedule();
			}
		}

		return null;
	}

}
