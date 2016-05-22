package com.cwctravel.plugins.shelvesetreview.propertypages;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ReviewerInfo;

public class ReviewerContentProvider implements IStructuredContentProvider {
	private Set<String> reviewerIds;
	private List<ReviewerInfo> reviewerInfos;

	public ReviewerContentProvider(List<ReviewerInfo> reviewerInfos) {
		this.reviewerIds = new HashSet<String>();
		this.reviewerInfos = reviewerInfos;
		if (reviewerInfos != null) {
			for (ReviewerInfo reviewerInfo : reviewerInfos) {
				reviewerIds.add(reviewerInfo.getReviewerId());
			}
		}
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

	public boolean addReviewer(String reviewerId) {
		boolean result = false;
		if (!reviewerIds.contains(reviewerId)) {
			ReviewerInfo reviewerInfo = new ReviewerInfo();
			reviewerInfo.setReviewerId(reviewerId);
			reviewerInfo.setModifiable(true);
			reviewerInfo.setSource(ReviewerInfo.SOURCE_SHELVESET);
			reviewerIds.add(reviewerId);
			reviewerInfos.add(reviewerInfo);
			result = true;
		}
		return result;
	}

	public boolean reviewerIdExists(String reviewerId) {
		return reviewerIds.contains(reviewerId);
	}

	public void removeElementsAt(List<Integer> rowIndices) {
		if (rowIndices != null && rowIndices.size() > 0) {
			for (int i = rowIndices.size() - 1; i >= 0; i--) {
				ReviewerInfo reviewerInfo = reviewerInfos.remove((int) rowIndices.get(i));
				reviewerIds.remove(reviewerInfo.getReviewerId());
			}
		}
	}

}
