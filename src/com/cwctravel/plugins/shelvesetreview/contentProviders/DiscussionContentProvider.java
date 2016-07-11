package com.cwctravel.plugins.shelvesetreview.contentProviders;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.DiscussionUtil;

public class DiscussionContentProvider implements ITreeContentProvider {
	private String path;
	private int threadId;
	private int startLine;
	private int startColumn;
	private int endLine;
	private int endColumn;

	public DiscussionContentProvider(String path, int startLine, int startColumn) {
		this(-1, path, startLine, startColumn, startLine, startColumn);
	}

	public DiscussionContentProvider(int threadId) {
		this(threadId, null, -1, -1, -1, -1);
	}

	public DiscussionContentProvider(String path, int startLine, int startColumn, int endLine, int endColumn) {
		this(-1, path, startLine, startColumn, endLine, endColumn);
	}

	protected DiscussionContentProvider(int threadId, String path, int startLine, int startColumn, int endLine, int endColumn) {
		this.threadId = threadId;
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
		List<ShelvesetDiscussionItem> shelvesetDiscussionItems = Collections.emptyList();
		if (threadId >= 0) {
			shelvesetDiscussionItems = DiscussionUtil.getTopLevelDiscussionItems(inputElement, threadId);
		} else if (inputElement instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) inputElement;
			if (path != null) {
				inputElement = shelvesetItem.findFile(path);
				shelvesetDiscussionItems = DiscussionUtil.getTopLevelDiscussionItems(inputElement, startLine, startColumn, endLine, endColumn);
			} else {
				shelvesetDiscussionItems = DiscussionUtil.getTopLevelDiscussionItems(shelvesetItem);
			}
		} else {
			shelvesetDiscussionItems = DiscussionUtil.getTopLevelDiscussionItems(inputElement, startLine, startColumn, endLine, endColumn);
		}
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
