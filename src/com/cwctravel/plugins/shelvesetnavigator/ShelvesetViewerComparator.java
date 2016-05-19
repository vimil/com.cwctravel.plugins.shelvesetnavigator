package com.cwctravel.plugins.shelvesetnavigator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetGroupItem;

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
		if (o1.getClass() == o2.getClass()) {
			if (o1 instanceof ShelvesetGroupItem) {
				ShelvesetGroupItem item1 = (ShelvesetGroupItem) o1;
				ShelvesetGroupItem item2 = (ShelvesetGroupItem) o2;
				return item1.compareTo(item2);
			}
		}
		return super.compare(viewer, o1, o2);
	}

}
