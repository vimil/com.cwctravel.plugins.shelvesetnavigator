package com.cwctravel.plugins.shelvesetreview.propertypages;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ReviewerInfo;

public class ReviewerContentProvider implements IStructuredContentProvider {
	private List<ReviewerInfo> reviewerInfos;

	public ReviewerContentProvider(List<ReviewerInfo> reviewerInfos) {
		this.reviewerInfos = reviewerInfos;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {
		return reviewerInfos.toArray(new ReviewerInfo[0]);
	}

	public List<ReviewerInfo> getReviewers() {
		return reviewerInfos;
	}

}
