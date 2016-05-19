package com.cwctravel.plugins.shelvesetnavigator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class ShelvesetViewerSorter extends ViewerSorter {
	private ShelvesetViewerComparator fComparator;

	public ShelvesetViewerSorter() {
		super(null);
		fComparator = new ShelvesetViewerComparator();
	}

	public int category(Object element) {
		return fComparator.category(element);
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		return fComparator.compare(viewer, e1, e2);
	}
}
