package com.cwctravel.plugins.shelvesetreview.navigator.model;

import org.eclipse.core.runtime.IProgressMonitor;

import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class CodeReviewShelvesetItem extends ShelvesetItem {
	private CodeReviewItem parent;

	public CodeReviewShelvesetItem(CodeReviewItem parent, Shelveset shelveset) {
		super(null, null, shelveset);
		this.parent = parent;
	}

	public CodeReviewItem getCodeReviewItem() {
		return parent;
	}

	public Object getItemParent() {
		return getCodeReviewItem();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter.isAssignableFrom(CodeReviewShelvesetItem.class)) {
			return (T) this;
		} else if (CodeReviewItem.class.equals(adapter)) {
			return (T) getCodeReviewItem();
		}
		return getCodeReviewItem().getAdapter(adapter);
	}

	public boolean delete(IProgressMonitor monitor) {
		return false;
	}
}
