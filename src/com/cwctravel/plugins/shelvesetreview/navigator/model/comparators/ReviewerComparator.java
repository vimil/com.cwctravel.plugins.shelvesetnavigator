package com.cwctravel.plugins.shelvesetreview.navigator.model.comparators;

import java.util.Comparator;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ReviewerInfo;

public class ReviewerComparator implements Comparator<ReviewerInfo> {
	public static final ReviewerComparator INSTANCE = new ReviewerComparator();

	private ReviewerComparator() {
	}

	@Override
	public int compare(ReviewerInfo o1, ReviewerInfo o2) {
		return o1.getReviewerId().compareTo(o2.getReviewerId());
	}
}