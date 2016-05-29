package com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto;

public class DiscussionThreadPropertiesInfo {
	private String itemPath;
	private String positionContext;
	private int startLine;
	private int startColumn;
	private int endLine;
	private int endColumn;

	public String getItemPath() {
		return itemPath;
	}

	public void setItemPath(String itemPath) {
		this.itemPath = itemPath;
	}

	public String getPositionContext() {
		return positionContext;
	}

	public void setPositionContext(String positionContext) {
		this.positionContext = positionContext;
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
