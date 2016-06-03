package com.cwctravel.plugins.shelvesetreview.contentProviders;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.util.DiscussionUtil;

public class DiscussionContentProvider implements ITreeContentProvider {
	private int lineNumber;
	private int columnNumber;

	public DiscussionContentProvider(Object inputItem, int lineNumber, int columnNumber) {
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		List<ShelvesetDiscussionItem> shelvesetDiscussionItems = DiscussionUtil.getTopLevelDiscussionItems(inputElement, lineNumber, columnNumber);
		return shelvesetDiscussionItems.toArray(new ShelvesetDiscussionItem[0]);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return getElements(parentElement);
	}

	@Override
	public Object getParent(Object element) {
		Object result = null;
		if (element instanceof ShelvesetDiscussionItem) {
			ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) element;
			result = shelvesetDiscussionItem.getParentDiscussion();
		}
		return result;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}
