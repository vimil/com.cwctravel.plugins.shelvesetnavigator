package com.cwctravel.plugins.shelvesetreview.util;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.cwctravel.plugins.shelvesetreview.rest.workitems.WorkItemService;
import com.cwctravel.plugins.shelvesetreview.rest.workitems.dto.WorkItemInfo;
import com.microsoft.tfs.core.TFSConnection;

public class WorkItemUtil {
	public static List<WorkItemInfo> retrieveWorkItems(List<Integer> workItemIds) throws IOException {
		List<WorkItemInfo> result = Collections.emptyList();
		TFSConnection connection = TFSUtil.getTFSConnection();
		if (connection != null) {
			result = WorkItemService.getWorkItems(connection, workItemIds);
		}
		return result;
	}

	public static List<String> getWorkItemHyperLinks(int workItemId) throws IOException {
		List<String> result = Collections.emptyList();
		TFSConnection connection = TFSUtil.getTFSConnection();
		if (connection != null) {
			result = WorkItemService.getWorkItemHyperLinks(connection, workItemId);
		}
		return result;
	}

	public static void updateWorkItem(int workItemId, List<Map<String, Object>> request) throws IOException {
		TFSConnection connection = TFSUtil.getTFSConnection();
		if (connection != null) {
			WorkItemService.updateWorkItem(connection, workItemId, request);
		}
	}
}