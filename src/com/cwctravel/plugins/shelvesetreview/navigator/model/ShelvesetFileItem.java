package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.DiscussionService;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionCreateRequestInfo;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.ChangeType;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingChange;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingSet;

public class ShelvesetFileItem extends ShelvesetResourceItem {
	private String name;

	private PendingSet pendingSet;
	private PendingChange pendingChange;

	private List<ShelvesetResourceItem> discussions;

	public ShelvesetFileItem(ShelvesetItem root, PendingSet pendingSet, PendingChange pendingChange) {
		super(root);
		this.pendingSet = pendingSet;
		this.pendingChange = pendingChange;
	}

	public String getPath() {
		return pendingChange.getServerItem();
	}

	public String getSourcePath() {
		String sourcePath = pendingChange.getSourceServerItem();
		if (sourcePath == null) {
			sourcePath = pendingChange.getServerItem();
		}
		return sourcePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShelvedDownloadURL() {
		return pendingChange.getShelvedDownloadURL();
	}

	public String getDownloadUrl() {
		return pendingChange.getDownloadURL();
	}

	public URI getURI() {
		String path = getPath();
		String shelvedDownloadURL = pendingChange.getShelvedDownloadURL();
		URI encodedDownloadURL = TFSUtil.encodeURI(path, getShelvesetName(), getShelvesetOwnerName(), shelvedDownloadURL);
		return encodedDownloadURL;
	}

	public ChangeType getChangeType() {
		return pendingChange.getChangeType();
	}

	public List<ShelvesetResourceItem> getDiscussions() {
		return discussions;
	}

	public void setDiscussions(List<ShelvesetResourceItem> discussions) {
		this.discussions = discussions;
	}

	public boolean hasDiscussions() {
		return discussions != null && !discussions.isEmpty();
	}

	public ShelvesetDiscussionItem findDiscussionItem(int startLine, int startCol, int endLine, int endCol) {
		ShelvesetDiscussionItem result = null;
		if (discussions != null) {
			for (ShelvesetResourceItem item : discussions) {
				ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) item;
				int dStartLine = shelvesetDiscussionItem.getStartLine();
				int dStartColumn = shelvesetDiscussionItem.getStartColumn();
				int dEndLine = shelvesetDiscussionItem.getEndLine();
				int dEndColumn = shelvesetDiscussionItem.getEndColumn();

				if (startLine >= dStartLine && endLine <= dEndLine && (startLine != endLine || (startCol >= dStartColumn && endCol <= dEndColumn))) {
					result = shelvesetDiscussionItem;
					if (dStartLine == startLine && dStartColumn == startCol && dEndLine == endLine && dEndColumn == endCol) {
						break;
					}
				}
			}
		}
		return result;
	}

	public void createDiscussion(String comment, int startLine, int startCol, int endLine, int endCol) throws IOException {
		DiscussionCreateRequestInfo discussionCreateRequestInfo = new DiscussionCreateRequestInfo();
		ShelvesetItem shelvesetItem = getParent();
		discussionCreateRequestInfo.setShelvesetName(shelvesetItem.getName());
		discussionCreateRequestInfo.setShelvesetOwnerName(shelvesetItem.getOwnerName());
		discussionCreateRequestInfo.setPath(getPath());
		discussionCreateRequestInfo.setAuthorId(TFSUtil.getCurrentUserId());
		discussionCreateRequestInfo.setStartLine(startLine);
		discussionCreateRequestInfo.setStartColumn(startCol);
		discussionCreateRequestInfo.setEndLine(endLine);
		discussionCreateRequestInfo.setEndColumn(endCol);
		discussionCreateRequestInfo.setComment(comment);

		DiscussionService.createDiscussion(TFSUtil.getTFSConnection(), discussionCreateRequestInfo);
	}

	public int getEncoding() {
		return pendingChange.getEncoding();
	}

}
