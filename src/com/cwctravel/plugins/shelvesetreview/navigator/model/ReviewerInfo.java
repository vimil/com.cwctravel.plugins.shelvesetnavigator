package com.cwctravel.plugins.shelvesetreview.navigator.model;

public class ReviewerInfo {
	public static final int SOURCE_SHELVESET = 0;
	public static final int SOURCE_WORKITEM = 1;
	public static final int SOURCE_CONFIG = 2;

	private String reviewerId;
	private boolean approved;
	private boolean modifiable;
	private int source;

	public String getReviewerId() {
		return reviewerId;
	}

	public void setReviewerId(String reviewerId) {
		this.reviewerId = reviewerId;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isModifiable() {
		return modifiable;
	}

	public void setModifiable(boolean modifiable) {
		this.modifiable = modifiable;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

}
