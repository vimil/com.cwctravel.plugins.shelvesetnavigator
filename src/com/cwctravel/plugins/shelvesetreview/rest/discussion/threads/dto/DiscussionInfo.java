package com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto;

import java.util.List;

public class DiscussionInfo {
	private List<DiscussionThreadInfo> threads;

	public List<DiscussionThreadInfo> getThreads() {
		return threads;
	}

	public void setThreads(List<DiscussionThreadInfo> threads) {
		this.threads = threads;
	}

}
