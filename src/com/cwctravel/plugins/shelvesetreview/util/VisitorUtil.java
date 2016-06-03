package com.cwctravel.plugins.shelvesetreview.util;

import java.util.List;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetResourceItem;

public class VisitorUtil {
	public static interface Visitor {
		public boolean onVisit(Object item);
	}

	public static void visit(Object item, Visitor visitor) {
		List<ShelvesetResourceItem> children = null;
		if (item instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) item;
			children = shelvesetItem.getChildren();
		} else if (item instanceof ShelvesetFolderItem) {
			ShelvesetFolderItem shelvesetFolderItem = (ShelvesetFolderItem) item;
			children = shelvesetFolderItem.getChildren();
		} else if (item instanceof ShelvesetFileItem) {
			ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) item;
			children = shelvesetFileItem.getDiscussions();
		} else if (item instanceof ShelvesetDiscussionItem) {
			ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) item;
			children = shelvesetDiscussionItem.getChildDiscussions();
		}

		if (children != null) {
			for (ShelvesetResourceItem child : children) {
				if (visitor.onVisit(child)) {
					visit(child, visitor);
				}
			}
		}
	}
}
