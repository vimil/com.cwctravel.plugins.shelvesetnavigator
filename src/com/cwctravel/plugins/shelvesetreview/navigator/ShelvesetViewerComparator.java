package com.cwctravel.plugins.shelvesetreview.navigator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.cwctravel.plugins.shelvesetreview.navigator.model.CodeReviewItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItem;

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
			} else if (o1 instanceof ShelvesetDiscussionItem) {
				ShelvesetDiscussionItem item1 = (ShelvesetDiscussionItem) o1;
				ShelvesetDiscussionItem item2 = (ShelvesetDiscussionItem) o2;
				String path1 = item1.getPath();
				String path2 = item2.getPath();
				if (path1 != path2) {
					if (path1 == null) {
						return -1;
					} else if (path2 == null) {
						return 1;
					}
				}
				int result = path1 == path2 ? 0 : path1.compareTo(path2);
				if (result == 0) {
					int startLine1 = item1.getStartLine();
					int startColumn1 = item1.getStartColumn();
					int endLine1 = item1.getEndLine();
					int endColumn1 = item1.getEndColumn();

					int startLine2 = item2.getStartLine();
					int startColumn2 = item2.getStartColumn();
					int endLine2 = item2.getEndLine();
					int endColumn2 = item2.getEndColumn();

					if (startLine1 < startLine2) {
						return -1;
					}
					if (startLine1 > startLine2) {
						return 1;
					}

					if (startColumn1 < startColumn2) {
						return -1;
					}
					if (startColumn1 > startColumn2) {
						return 1;
					}

					if (endLine1 < endLine2) {
						return -1;
					}
					if (endLine1 > endLine2) {
						return 1;
					}

					if (endColumn1 < endColumn2) {
						return -1;
					}
					if (endColumn1 > endColumn2) {
						return 1;
					}

				}
				return result;
			}
		} else if (o1 instanceof ShelvesetGroupItem && o2 instanceof CodeReviewItemContainer) {
			return -1;
		} else if (o1 instanceof CodeReviewItemContainer && o2 instanceof ShelvesetGroupItem) {
			return 1;
		}
		return super.compare(viewer, o1, o2);
	}

}
