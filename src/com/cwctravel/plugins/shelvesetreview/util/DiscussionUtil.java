package com.cwctravel.plugins.shelvesetreview.util;

import java.util.ArrayList;
import java.util.List;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
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

	public static List<ShelvesetDiscussionItem> getTopLevelDiscussionItems(Object inputItem, int startLine, int startColumn) {
		return getTopLevelDiscussionItems(inputItem, startLine, startColumn, startLine, startColumn);
	}

	public static List<ShelvesetDiscussionItem> getTopLevelDiscussionItems(Object inputItem, int startLine, int startColumn, int endLine,
			int endColumn) {
		if (endLine < 0) {
			endLine = startLine;
		}
		if (endColumn < 0) {
			endColumn = startColumn;
		}

		return getTopLevelDiscussionItemsInternal(inputItem, startLine, startColumn, startLine, startColumn);
	}

	private static List<ShelvesetDiscussionItem> getTopLevelDiscussionItemsInternal(Object inputItem, int startLine, int startColumn, int endLine,
			int endColumn) {
		List<ShelvesetDiscussionItem> result = new ArrayList<ShelvesetDiscussionItem>();
		VisitorUtil.visit(inputItem, (item) -> {
			if (item instanceof ShelvesetDiscussionItem) {
				ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) item;
				if (startLine < 0 && startColumn < 0) {
					result.add(shelvesetDiscussionItem);
				} else if (startLine >= 0) {
					int discussionStartLine = shelvesetDiscussionItem.getStartLine();
					int discussionEndLine = shelvesetDiscussionItem.getEndLine();
					if (discussionEndLine < 0) {
						discussionEndLine = discussionStartLine;
					}
					if (discussionStartLine >= startLine && discussionEndLine <= endLine) {
						if (startColumn < 0) {
							result.add(shelvesetDiscussionItem);
						} else {
							int discussionStartColumn = shelvesetDiscussionItem.getStartColumn();
							int discussionEndcolumn = shelvesetDiscussionItem.getEndColumn();
							if (discussionStartColumn <= startColumn && discussionEndcolumn >= endColumn) {
								result.add(shelvesetDiscussionItem);
							}
						}
					}
				}
				return false;
			}
			return true;
		});
		return result;
	}

	public static List<ShelvesetDiscussionItem> getTopLevelDiscussionItems(Object inputItem, int threadId) {
		return getTopLevelDiscussionItemsInternal(inputItem, threadId);
	}

	private static List<ShelvesetDiscussionItem> getTopLevelDiscussionItemsInternal(Object inputItem, int threadId) {
		List<ShelvesetDiscussionItem> result = new ArrayList<ShelvesetDiscussionItem>();
		VisitorUtil.visit(inputItem, (item) -> {
			if (item instanceof ShelvesetDiscussionItem) {
				ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) item;
				if (shelvesetDiscussionItem.getThreadId() == threadId) {
					result.add(shelvesetDiscussionItem);
				}
				return shelvesetDiscussionItem.isOverallDiscussion();
			}
			return true;
		});
		return result;
	}

	public static List<ShelvesetDiscussionItem> getTopLevelDiscussionItems(ShelvesetItem shelvesetItem) {
		return getTopLevelDiscussionItemsInternal(shelvesetItem);
	}

	private static List<ShelvesetDiscussionItem> getTopLevelDiscussionItemsInternal(ShelvesetItem shelvesetItem) {
		List<ShelvesetDiscussionItem> result = new ArrayList<ShelvesetDiscussionItem>();
		VisitorUtil.visit(shelvesetItem, (item) -> {
			if (item instanceof ShelvesetFolderItem || item instanceof ShelvesetFileItem) {
				return false;
			}
			if (item instanceof ShelvesetDiscussionItem) {
				ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) item;
				if (!shelvesetDiscussionItem.isOverallDiscussion()) {
					result.add(shelvesetDiscussionItem);
					return false;
				}
				return true;
			}
			return true;
		});
		return result;
	}

}
