package com.cwctravel.plugins.shelvesetreview.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkItemUpdateRequestBuilder {
	private List<Map<String, Object>> request;

	public WorkItemUpdateRequestBuilder() {
		request = new ArrayList<Map<String, Object>>();
	}

	public WorkItemUpdateRequestBuilder addComment(String comment) {
		Map<String, Object> fieldUpdateRequest = new HashMap<String, Object>();
		fieldUpdateRequest.put("op", "add");
		fieldUpdateRequest.put("path", "/fields/System.History");
		fieldUpdateRequest.put("value", comment);
		request.add(fieldUpdateRequest);
		return this;
	}

	public WorkItemUpdateRequestBuilder addHyperLink(String url) {
		Map<String, Object> fieldUpdateRequest = new HashMap<String, Object>();
		fieldUpdateRequest.put("op", "add");
		fieldUpdateRequest.put("path", "/relations/-");

		Map<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put("rel", "HyperLink");
		valueMap.put("url", url);
		fieldUpdateRequest.put("value", valueMap);
		request.add(fieldUpdateRequest);
		return this;
	}

	public List<Map<String, Object>> build() {
		return request;
	}
}
