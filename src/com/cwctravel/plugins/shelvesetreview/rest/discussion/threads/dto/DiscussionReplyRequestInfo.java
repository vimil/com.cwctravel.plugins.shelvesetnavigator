package com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto;

public class DiscussionReplyRequestInfo {
	private int threadId;
	private String comment;
	private String authorId;

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public String getAuthorId() {
		return authorId;
	}

	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
