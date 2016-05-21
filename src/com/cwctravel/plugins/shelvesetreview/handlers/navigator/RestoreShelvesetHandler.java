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

import com.cwctravel.plugins.shelvesetreview.jobs.ShelvesetItemUpdateJob;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class RestoreShelvesetHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			if (treeSelection.size() >= 1) {
				List<ShelvesetItem> shelvesetItems = new ArrayList<ShelvesetItem>();
				@SuppressWarnings("unchecked")
				Iterator<Object> selectionItr = treeSelection.iterator();
				while (selectionItr.hasNext()) {
					Object element = selectionItr.next();
					shelvesetItems.add((ShelvesetItem) element);
				}
				new ShelvesetItemUpdateJob(shelvesetItems, ShelvesetItemUpdateJob.UPDATE_TYPE_MARK_SHELVESET_ACTIVE)
						.schedule();
			}
		}
		return null;
	}

}
