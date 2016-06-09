package com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto;

public class DiscussionCreateRequestInfo {
	private String shelvesetName;
	private String shelvesetOwnerName;
	private String path;
	private String authorId;
	private String comment;
	private int parentId;
	private int threadId;
	private int startLine;
	private int startColumn;
	private int endLine;
	private int endColumn;

	public String getShelvesetName() {
		return shelvesetName;
	}

	public void setShelvesetName(String shelvesetName) {
		this.shelvesetName = shelvesetName;
	}

	public String getShelvesetOwnerName() {
		return shelvesetOwnerName;
	}

	public void setShelvesetOwnerName(String shelvesetOwnerName) {
		this.shelvesetOwnerName = shelvesetOwnerName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public int getStartLine() {
		return startLine;
	}

	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getStartColumn() {
		return startColumn;
	}

	public void setStartColumn(int startColumn) {
		this.startColumn = startColumn;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public int getEndColumn() {
		return endColumn;
	}

	public void setEndColumn(int endColumn) {
		this.endColumn = endColumn;
	}

}