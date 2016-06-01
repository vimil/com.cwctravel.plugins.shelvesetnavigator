package com.cwctravel.plugins.shelvesetreview.annotator;

import org.eclipse.jface.text.source.Annotation;

public class DiscussionAnnotation extends Annotation implements Comparable<DiscussionAnnotation> {
	static final String DISCUSSION_MARKER = "com.cwctravel.plugins.shelvesetreview.discussionMarker";

	private int threadId;
	private int commentd;

	public DiscussionAnnotation(int threadId, int commentId, String text) {
		super(DISCUSSION_MARKER, false, text);
		this.threadId = threadId;
		this.commentd = commentId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + commentd;
		result = prime * result + threadId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DiscussionAnnotation other = (DiscussionAnnotation) obj;
		if (commentd != other.commentd)
			return false;
		if (threadId != other.threadId)
			return false;
		return true;
	}

	@Override
	public int compareTo(DiscussionAnnotation other) {
		if (other == null) {
			return 1;
		}
		int result = threadId - other.threadId;
		if (result == 0) {
			result = commentd - other.commentd;
		}

		return result;
	}

}
