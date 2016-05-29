package com.cwctravel.plugins.shelvesetreview.util;

import java.util.ArrayList;
import java.util.List;

import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadPropertiesInfo;

public class DiscussionUtil {

	public static List<DiscussionThreadInfo> findAllThreads(DiscussionInfo discussionInfo, String path) {
		List<DiscussionThreadInfo> result = new ArrayList<DiscussionThreadInfo>();
		if (discussionInfo != null) {
			for (DiscussionThreadInfo discussionThreadInfo : discussionInfo.getThreads()) {
				DiscussionThreadPropertiesInfo discussiomThreadPropertiesInfo = discussionThreadInfo.getThreadProperties();
				if (discussiomThreadPropertiesInfo != null) {
					if (path.equals(discussiomThreadPropertiesInfo.getItemPath())) {
						result.add(discussionThreadInfo);
					}
				}
			}
		}
		return result;
	}

}
