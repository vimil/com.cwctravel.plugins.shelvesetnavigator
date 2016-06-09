package com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto;

public class DiscussionCommentDeleteRequestInfo {
	private int threadId;
	private int commentId;

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

}
