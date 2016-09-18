package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.DiscussionService;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionAuthorInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionCommentInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadPropertiesInfo;
import com.cwctravel.plugins.shelvesetreview.util.IconManager;
import com.cwctravel.plugins.shelvesetreview.util.IdentityUtil;
import com.cwctravel.plugins.shelvesetreview.util.StringUtil;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;

public class ShelvesetDiscussionItem extends ShelvesetResourceItem implements IItemContainer<Object, ShelvesetResourceItem> {
	private final DiscussionThreadInfo discussionThreadInfo;
	private final DiscussionCommentInfo discussionCommentInfo;

	private final ShelvesetFileItem parentFile;

	private final ShelvesetDiscussionItem parentDiscussion;

	private List<ShelvesetResourceItem> childDiscussions;

	public ShelvesetDiscussionItem(ShelvesetItem parent, ShelvesetFileItem parentFile, ShelvesetDiscussionItem parentDiscussion,
			DiscussionThreadInfo discussionThreadInfo, DiscussionCommentInfo discussionCommentInfo) {
		super(parent);
		this.discussionThreadInfo = discussionThreadInfo;
		this.discussionCommentInfo = discussionCommentInfo;
		this.parentFile = parentFile;
		this.parentDiscussion = parentDiscussion;
	}

	public boolean isOverallDiscussion() {
		return discussionCommentInfo == null;
	}

	@Override
	public String getName() {
		String result = "<Overall Discussion>";
		if (discussionCommentInfo != null) {
			result = StringUtil.truncateTo(discussionCommentInfo.getContent(), 25);
		}
		return result;
	}

	public String getComment() {
		return discussionCommentInfo.getContent();
	}

	@Override
	public String getPath() {
		String result = null;
		if (parentFile != null) {
			result = parentFile.getPath();
		}
		return result;
	}

	public List<ShelvesetResourceItem> getChildDiscussions() {
		if (childDiscussions == null) {
			childDiscussions = new ArrayList<ShelvesetResourceItem>();
		}
		return childDiscussions;
	}

	public void setChildDiscussions(List<ShelvesetResourceItem> childDiscussions) {
		this.childDiscussions = childDiscussions;
	}

	public ShelvesetFileItem getParentFile() {
		return parentFile;
	}

	public ShelvesetDiscussionItem getParentDiscussion() {
		return parentDiscussion;
	}

	public int getThreadId() {
		int result = 0;
		if (discussionCommentInfo != null) {
			result = discussionCommentInfo.getThreadId();
		}
		return result;
	}

	public int getId() {
		int result = 0;
		if (discussionCommentInfo != null) {
			result = discussionCommentInfo.getId();
		}
		return result;
	}

	public int hashCode() {
		int result = super.hashCode();
		final int prime = 31;
		result = prime * result + getThreadId();
		result = prime * result + getId();

		return result;
	}

	public boolean equals(Object obj) {
		boolean result = super.equals(obj);
		if (result) {
			ShelvesetDiscussionItem other = (ShelvesetDiscussionItem) obj;
			result = getThreadId() == other.getThreadId() && getId() == other.getId();
		}
		return result;
	}

	public int getStartLine() {
		int result = 0;
		if (discussionThreadInfo != null) {
			DiscussionThreadPropertiesInfo discussionThreadPropertiesInfo = discussionThreadInfo.getThreadProperties();
			if (discussionThreadPropertiesInfo != null) {
				result = discussionThreadPropertiesInfo.getStartLine();
			}
		}
		return result;
	}

	public int getStartColumn() {
		int result = 0;
		if (discussionThreadInfo != null) {
			DiscussionThreadPropertiesInfo discussionThreadPropertiesInfo = discussionThreadInfo.getThreadProperties();
			if (discussionThreadPropertiesInfo != null) {
				result = discussionThreadPropertiesInfo.getStartColumn();
			}
		}
		return result;
	}

	public int getEndLine() {
		int result = 0;
		if (discussionThreadInfo != null) {
			DiscussionThreadPropertiesInfo discussionThreadPropertiesInfo = discussionThreadInfo.getThreadProperties();
			if (discussionThreadPropertiesInfo != null) {
				result = discussionThreadPropertiesInfo.getEndLine();
			}
		}
		return result;
	}

	public int getEndColumn() {
		int result = 0;
		if (discussionThreadInfo != null) {
			DiscussionThreadPropertiesInfo discussionThreadPropertiesInfo = discussionThreadInfo.getThreadProperties();
			if (discussionThreadPropertiesInfo != null) {
				result = discussionThreadPropertiesInfo.getEndColumn();
			}
		}
		return result;
	}

	public Calendar getLastUpdatedDate() {
		Calendar result = null;
		if (discussionCommentInfo != null) {
			result = discussionCommentInfo.getLastUpdatedDate();
		}
		return result;
	}

	public String getAuthorDisplayName() {
		String result = null;
		if (discussionCommentInfo != null) {
			DiscussionAuthorInfo discussionAuthorInfo = discussionCommentInfo.getAuthor();
			if (discussionAuthorInfo != null) {
				result = discussionAuthorInfo.getDisplayName();
			}
		}
		return result;
	}

	public String getAuthorId() {
		String result = null;
		if (discussionCommentInfo != null) {
			DiscussionAuthorInfo discussionAuthorInfo = discussionCommentInfo.getAuthor();
			if (discussionAuthorInfo != null) {
				result = discussionAuthorInfo.getId();
			}
		}
		return result;
	}

	public String getAuthorName() {
		String result = null;
		if (discussionCommentInfo != null) {
			DiscussionAuthorInfo discussionAuthorInfo = discussionCommentInfo.getAuthor();
			if (discussionAuthorInfo != null) {
				result = discussionAuthorInfo.getUniqueName();
			}
		}
		return result;
	}

	public boolean updateComment(String updatedComment) throws IOException {
		boolean result = false;
		String oldContent = discussionCommentInfo.getContent();
		String newContent = StringUtil.normalizeNewLines(updatedComment);
		if (!StringUtil.equals(oldContent, newContent)) {
			discussionCommentInfo.setContent(newContent);
			DiscussionService.updateShelvesetDiscussionComment(TFSUtil.getTFSConnection(), discussionCommentInfo);
			result = true;
		}
		return result;
	}

	public boolean canEdit() {
		return StringUtil.equals(getAuthorId(), IdentityUtil.getCurrentUserId());
	}

	public boolean canReply() {
		boolean result = false;
		if (childDiscussions == null || childDiscussions.isEmpty()) {
			if (parentDiscussion != null && !parentDiscussion.isOverallDiscussion()) {
				List<ShelvesetResourceItem> childDiscussions = parentDiscussion.getChildDiscussions();
				if (childDiscussions.get(childDiscussions.size() - 1) == this) {
					result = true;
				}
			} else {
				result = true;
			}
		}
		return result;
	}

	public boolean canDelete() {
		return StringUtil.equals(getAuthorId(), IdentityUtil.getCurrentUserId());
	}

	@Override
	public List<ShelvesetResourceItem> getChildren() {
		return getChildDiscussions();
	}

	public Object getItemParent() {
		Object result = null;
		ShelvesetDiscussionItem parentDiscussion = getParentDiscussion();
		if (parentDiscussion != null) {
			result = parentDiscussion;
		} else {
			ShelvesetFileItem parentFile = getParentFile();
			if (parentFile != null) {
				result = parentFile;
			} else {
				result = getParent();
			}
		}
		return result;
	}

	public String getText() {
		return getName();
	}

	@Override
	public Image getImage() {
		Image image = IconManager.getIcon(IconManager.DISCUSSION_ICON_ID);
		return image;
	}

	@Override
	public int itemCompareTo(IItemContainer<?, ?> itemContainer) {
		if (itemContainer instanceof ShelvesetDiscussionItem) {
			ShelvesetDiscussionItem item2 = (ShelvesetDiscussionItem) itemContainer;
			String path1 = getPath();
			String path2 = item2.getPath();
			if (path1 != path2) {
				if (path1 == null) {
					return -1;
				} else if (path2 == null) {
					return 1;
				}
			}
			int result = path1 == path2 ? 0 : path1.compareTo(path2);
			if (result == 0) {
				int startLine1 = getStartLine();
				int startColumn1 = getStartColumn();
				int endLine1 = getEndLine();
				int endColumn1 = getEndColumn();

				int startLine2 = item2.getStartLine();
				int startColumn2 = item2.getStartColumn();
				int endLine2 = item2.getEndLine();
				int endColumn2 = item2.getEndColumn();

				if (startLine1 < startLine2) {
					return -1;
				}
				if (startLine1 > startLine2) {
					return 1;
				}

				if (startColumn1 < startColumn2) {
					return -1;
				}
				if (startColumn1 > startColumn2) {
					return 1;
				}

				if (endLine1 < endLine2) {
					return -1;
				}
				if (endLine1 > endLine2) {
					return 1;
				}

				if (endColumn1 < endColumn2) {
					return -1;
				}
				if (endColumn1 > endColumn2) {
					return 1;
				}
			}
			return result;
		} else if (itemContainer instanceof ShelvesetResourceItem) {
			return -1;
		}
		return 0;
	}

	public void decorate(IDecoration decoration) {
		String authorDisplayName = getAuthorDisplayName();
		if (authorDisplayName != null) {
			decoration.addSuffix(" [" + authorDisplayName + "]");
		}
	}
}
