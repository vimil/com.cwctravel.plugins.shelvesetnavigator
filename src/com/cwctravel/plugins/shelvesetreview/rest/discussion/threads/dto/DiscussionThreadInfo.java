package com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto;

import java.util.Calendar;
import java.util.List;

public class DiscussionThreadInfo {
	private int id;
	private String artifactUri;
	private Calendar publishedDate;
	private Calendar lastUpdatedDate;

	private List<DiscussionCommentInfo> comments;
	private DiscussionThreadPropertiesInfo threadProperties;

	private String status;
	private boolean isDeleted;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getArtifactUri() {
		return artifactUri;
	}

	public void setArtifactUri(String artifactUri) {
		this.artifactUri = artifactUri;
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

	public List<DiscussionCommentInfo> getComments() {
		return comments;
	}

	public void setComments(List<DiscussionCommentInfo> comments) {
		this.comments = comments;
	}

	public DiscussionThreadPropertiesInfo getThreadProperties() {
		return threadProperties;
	}

	public void setThreadProperties(DiscussionThreadPropertiesInfo threadProperties) {
		this.threadProperties = threadProperties;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
