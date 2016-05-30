package com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto;

import java.util.Calendar;

public class DiscussionCommentInfo {
	private int id;
	private int parentId;
	private int threadId;
	private String content;
	private Calendar publishedDate;
	private Calendar lastUpdatedDate;

	private DiscussionAuthorInfo author;

	private boolean isDeleted;
	private boolean canDelete;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public int getThreadId() {
		return threadId;
	}

	public void setThreadId(int threadId) {
		this.threadId = threadId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Calendar getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Calendar publishedDate) {
		this.publishedDate = publishedDate;
	}

	public Calendar getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Calendar lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	public DiscussionAuthorInfo getAuthor() {
		return author;
	}

	public void setAuthor(DiscussionAuthorInfo author) {
		this.author = author;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public boolean isCanDelete() {
		return canDelete;
	}

	public void setCanDelete(boolean canDelete) {
		this.canDelete = canDelete;
	}

}
