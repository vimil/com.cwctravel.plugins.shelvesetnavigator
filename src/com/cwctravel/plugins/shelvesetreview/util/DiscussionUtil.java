package com.cwctravel.plugins.shelvesetreview.util;

import java.util.ArrayList;
import java.util.List;

import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionCommentInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadPropertiesInfo;

public class DiscussionUtil {

	public static List<DiscussionThreadInfo> findAllDiscussionThreads(DiscussionInfo discussionInfo, String path) {
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

	public static List<DiscussionThreadInfo> findAllOverallDiscussionThreads(DiscussionInfo discussionInfo) {
		List<DiscussionThreadInfo> result = new ArrayList<DiscussionThreadInfo>();
		if (discussionInfo != null) {
			for (DiscussionThreadInfo discussionThreadInfo : discussionInfo.getThreads()) {
				DiscussionThreadPropertiesInfo discussiomThreadPropertiesInfo = discussionThreadInfo.getThreadProperties();
				if (discussiomThreadPropertiesInfo == null) {
					result.add(discussionThreadInfo);
				}
			}
		}
		return result;
	}

	public static List<DiscussionCommentInfo> findRootDiscussionComments(DiscussionThreadInfo discussionThreadInfo) {
		List<DiscussionCommentInfo> result = new ArrayList<DiscussionCommentInfo>();
		if (discussionThreadInfo != null) {
			List<DiscussionCommentInfo> discussionCommentInfos = discussionThreadInfo.getComments();
			for (DiscussionCommentInfo discussionCommentInfo : discussionCommentInfos) {
				if (discussionCommentInfo.getParentId() == 0) {
					result.add(discussionCommentInfo);
				}
			}
		}

		return result;
	}

	public static List<DiscussionCommentInfo> findChildDiscussions(DiscussionInfo discussionInfo, int threadId, int id,
			DiscussionThreadInfo[] discussionThreadInfoHolder) {
		List<DiscussionCommentInfo> result = new ArrayList<DiscussionCommentInfo>();
		if (discussionInfo != null) {
			for (DiscussionThreadInfo discussionThreadInfo : discussionInfo.getThreads()) {
				if (discussionThreadInfo.getId() == threadId) {
					List<DiscussionCommentInfo> discussionCommentInfos = discussionThreadInfo.getComments();
					for (DiscussionCommentInfo discussionCommentInfo : discussionCommentInfos) {
						if (discussionCommentInfo.getParentId() == id) {
							result.add(discussionCommentInfo);
						}
					}

					if (discussionThreadInfoHolder != null && discussionThreadInfoHolder.length == 1) {
						discussionThreadInfoHolder[0] = discussionThreadInfo;
					}
					break;
				}
			}
		}

		return result;
	}

	public static boolean isDiscussionPresent(DiscussionInfo discussionInfo) {
		boolean result = false;
		if (discussionInfo != null) {
			List<DiscussionThreadInfo> discussionThreads = discussionInfo.getThreads();
			if (discussionThreads != null) {
				for (DiscussionThreadInfo discussionThreadInfo : discussionThreads) {
					List<DiscussionCommentInfo> discussionComments = discussionThreadInfo.getComments();
					if (!discussionComments.isEmpty()) {
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}

}
