package com.cwctravel.plugins.shelvesetreview.handlers.navigator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.jobs.ShelvesetItemsRefreshJob;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class RefreshHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<ShelvesetItem> shelvesetItems = new ArrayList<ShelvesetItem>();
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			if (treeSelection.size() >= 1) {
				@SuppressWarnings("unchecked")
				Iterator<Object> selectionItr = treeSelection.iterator();
				while (selectionItr.hasNext()) {
					Object element = selectionItr.next();
					if (element instanceof IAdaptable) {
						ShelvesetItem shelvesetItem = (ShelvesetItem) ((IAdaptable) element).getAdapter(ShelvesetItem.class);
						if (shelvesetItem != null) {
							shelvesetItem.scheduleRefresh();
							shelvesetItems.add(shelvesetItem);
						}
					}
				}
			}
		}

		if (!shelvesetItems.isEmpty()) {
			ShelvesetItemsRefreshJob shelvesetItemsRefreshJob = new ShelvesetItemsRefreshJob(shelvesetItems);
			shelvesetItemsRefreshJob.schedule();
		} else {
			ShelvesetReviewPlugin.getDefault().scheduleRefreshShelvesetGroupItems();
		}
		return null;
	}
}
