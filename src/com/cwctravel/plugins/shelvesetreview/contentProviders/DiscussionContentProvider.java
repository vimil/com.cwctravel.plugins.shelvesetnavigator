package com.cwctravel.plugins.shelvesetreview.contentProviders;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.DiscussionUtil;

public class DiscussionContentProvider implements ITreeContentProvider {
	private String path;
	private int lineNumber;
	private int columnNumber;

	public DiscussionContentProvider(String path, int lineNumber, int columnNumber) {
		this.path = path;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof ShelvesetItem && path != null) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) inputElement;
			inputElement = shelvesetItem.findFile(path);
		}
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
