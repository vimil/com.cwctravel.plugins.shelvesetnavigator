package com.cwctravel.plugins.shelvesetreview.annotator;

import org.eclipse.jface.text.source.Annotation;

import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionCommentInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadPropertiesInfo;

public class DiscussionAnnotation extends Annotation implements Comparable<DiscussionAnnotation> {
	static final String DISCUSSION_MARKER = "com.cwctravel.plugins.shelvesetreview.discussionMarker";

	private DiscussionThreadInfo discussionThreadInfo;
	private DiscussionCommentInfo discussionCommentInfo;

	public DiscussionAnnotation(DiscussionThreadInfo discussionThreadInfo, DiscussionCommentInfo discussionCommentInfo, String text) {
		super(DISCUSSION_MARKER, false, text);
		this.discussionThreadInfo = discussionThreadInfo;
		this.discussionCommentInfo = discussionCommentInfo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + discussionCommentInfo.getId();
		result = prime * result + discussionThreadInfo.getId();
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
		if (discussionCommentInfo.getId() != other.discussionCommentInfo.getId())
			return false;
		if (discussionThreadInfo.getId() != other.discussionThreadInfo.getId())
			return false;
		return true;
	}

	@Override
	public int compareTo(DiscussionAnnotation other) {
		if (other == null) {
			return 1;
		}
		int result = discussionThreadInfo.getId() - other.discussionThreadInfo.getId();
		if (result == 0) {
			result = discussionCommentInfo.getId() - other.discussionCommentInfo.getId();
		}

		return result;
	}

	public int getStartLine() {
		int result = -1;
		DiscussionThreadPropertiesInfo threadProperties = discussionThreadInfo.getThreadProperties();
		if (threadProperties != null) {
			result = threadProperties.getStartLine();
		}
		return result;
	}

	public int getStartColumn() {
		int result = -1;
		DiscussionThreadPropertiesInfo threadProperties = discussionThreadInfo.getThreadProperties();
		if (threadProperties != null) {
			result = threadProperties.getStartColumn();
		}
		return result;
	}

	public int getEndColumn() {
		int result = -1;
		DiscussionThreadPropertiesInfo threadProperties = discussionThreadInfo.getThreadProperties();
		if (threadProperties != null) {
			result = threadProperties.getEndColumn();
		}
		return result;
	}

	public int getEndLine() {
		int result = -1;
		DiscussionThreadPropertiesInfo threadProperties = discussionThreadInfo.getThreadProperties();
		if (threadProperties != null) {
			result = threadProperties.getEndLine();
		}
		return result;
	}

}
