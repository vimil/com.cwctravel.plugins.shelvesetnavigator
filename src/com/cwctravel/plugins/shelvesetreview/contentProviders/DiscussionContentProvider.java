package com.cwctravel.plugins.shelvesetreview.contentProviders;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.DiscussionUtil;

public class DiscussionContentProvider implements ITreeContentProvider {
	private String path;
	private int startLine;
	private int startColumn;
	private int endLine;
	private int endColumn;

	public DiscussionContentProvider(String path, int startLine, int startColumn) {
		this(path, startLine, startColumn, startLine, startColumn);
	}

	public DiscussionContentProvider(String path, int startLine, int startColumn, int endLine, int endColumn) {
		this.path = path;
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
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
		List<ShelvesetDiscussionItem> shelvesetDiscussionItems = DiscussionUtil.getTopLevelDiscussionItems(inputElement, startLine, startColumn,
				endLine, endColumn);
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
