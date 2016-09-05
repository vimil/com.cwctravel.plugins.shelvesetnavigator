package com.cwctravel.plugins.shelvesetreview.contentProviders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ReviewerInfo;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.core.clients.webservices.TeamFoundationIdentity;

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
		TeamFoundationIdentity reviewerIdentity = TFSUtil.getIdentity(reviewerId);
		if (reviewerIdentity != null) {
			reviewerId = reviewerIdentity.getUniqueName();
			if (!reviewerIds.contains(reviewerId)) {
				ReviewerInfo reviewerInfo = new ReviewerInfo();
				reviewerInfo.setReviewerId(reviewerId);
				reviewerIds.add(reviewerId);
				reviewerInfos.add(reviewerInfo);
				result = true;
			}
		}
		return result;
	}

	public boolean reviewerIdExists(String reviewerId) {
		TeamFoundationIdentity reviewerIdentity = TFSUtil.getIdentity(reviewerId);
		if (reviewerIdentity != null) {
			reviewerId = reviewerIdentity.getUniqueName();
			return reviewerIds.contains(reviewerId);
		}
		return false;
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
