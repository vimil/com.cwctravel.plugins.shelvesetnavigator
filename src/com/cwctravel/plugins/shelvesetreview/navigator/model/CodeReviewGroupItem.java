package com.cwctravel.plugins.shelvesetreview.navigator.model;

import org.eclipse.core.runtime.IAdaptable;

public class CodeReviewGroupItem implements Comparable<CodeReviewGroupItem>, IAdaptable {

	public static final int GROUP_TYPE_CURRENT_USER_CODEREVIEWS = 0;
	public static final int GROUP_TYPE_OPEN_CODEREVIEWS = 1;
	public static final int GROUP_TYPE_ACCEPTED_CODEREVIEWS = 2;

	private final CodeReviewItemContainer parent;
	private final int groupType;

	public CodeReviewGroupItem(CodeReviewItemContainer codeReviewItemContainer, int groupType) {
		this.parent = codeReviewItemContainer;
		this.groupType = groupType;
	}

	public CodeReviewItemContainer getParent() {
		return parent;
	}

	public int getGroupType() {
		return groupType;
	}

	public String getName() {
		switch (groupType) {
			case GROUP_TYPE_CURRENT_USER_CODEREVIEWS:
				return "My CodeReviews";
			case GROUP_TYPE_OPEN_CODEREVIEWS:
				return "Open CodeReviews";
			case GROUP_TYPE_ACCEPTED_CODEREVIEWS:
				return "Accepted CodeReviews";
			default:
				return "";
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + groupType;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof CodeReviewGroupItem)) {
			return false;
		}
		CodeReviewGroupItem other = (CodeReviewGroupItem) obj;
		if (groupType != other.groupType) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(CodeReviewGroupItem o) {
		return groupType - o.groupType;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (CodeReviewGroupItem.class.equals(adapter)) {
			return this;
		} else if (CodeReviewItemContainer.class.equals(adapter)) {
			return getParent();
		}
		return null;
	}
}
