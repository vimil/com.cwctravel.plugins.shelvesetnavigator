package com.cwctravel.plugins.shelvesetreview.navigator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.cwctravel.plugins.shelvesetreview.navigator.model.IItemContainer;

public class ShelvesetViewerComparator extends ViewerComparator {

	@Override
	public int compare(Viewer viewer, Object o1, Object o2) {
		if (o1 == o2) {
			return 0;
		}
		if (o1 == null) {
			return -1;
		}
		if (o2 == null) {
			return 1;
		}
		if (o1 instanceof IItemContainer<?, ?> && o2 instanceof IItemContainer<?, ?>) {
			IItemContainer<?, ?> itemContainer1 = (IItemContainer<?, ?>) o1;
			IItemContainer<?, ?> itemContainer2 = (IItemContainer<?, ?>) o2;
			return itemContainer1.itemCompareTo(itemContainer2);
		}

		return super.compare(viewer, o1, o2);
	}

}
