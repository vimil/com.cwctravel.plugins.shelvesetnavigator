package com.cwctravel.plugins.shelvesetreview;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.core.clients.workitem.WorkItem;
import com.microsoft.tfs.core.clients.workitem.WorkItemClient;

public class WorkItemCache {
	private static final int MAX_ENTRIES = 50;

	private static WorkItemCache _instance;

	private Map<Integer, WorkItem> workItemMap;

	private WorkItemCache() {
		workItemMap = Collections.synchronizedMap(new LinkedHashMap<Integer, WorkItem>(MAX_ENTRIES + 1, .75F, true) {
			private static final long serialVersionUID = -6298047524177314054L;

			public boolean removeEldestEntry(Map.Entry<Integer, WorkItem> eldest) {
				return size() > MAX_ENTRIES;
			}
		});
	}

	public synchronized WorkItem getWorkItem(int workItemId) {
		WorkItem result = workItemMap.get(workItemId);
		if (result == null) {
			WorkItemClient workItemClient = TFSUtil.getWorkItemClient();
			if (workItemClient != null) {
				result = workItemClient.getWorkItemByID(workItemId);
				workItemMap.put(workItemId, result);
			}
		}
		return result;
	}

	public static synchronized WorkItemCache getInstance() {
		if (_instance == null) {
			_instance = new WorkItemCache();
		}
		return _instance;
	}
}
