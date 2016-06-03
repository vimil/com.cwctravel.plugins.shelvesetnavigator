package com.cwctravel.plugins.shelvesetreview.dialogs;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.util.DateUtil;

public class DiscussionLabelProvider implements ITableLabelProvider {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {

	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		String result = null;
		if (element instanceof ShelvesetDiscussionItem) {
			ShelvesetDiscussionItem item = (ShelvesetDiscussionItem) element;
			if (columnIndex == 0) {
				result = item.getName();
			} else if (columnIndex == 1) {
				result = item.getAuthorDisplayName();
			} else if (columnIndex == 2) {
				result = DateUtil.formatDate(item.getLastUpdatedDate());
			}
		}
		return result;
	}

}
