package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.jobs.ShelvesetFileItemsRefreshJob;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionInfo;
import com.cwctravel.plugins.shelvesetreview.util.ShelvesetUtil;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.ItemType;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingChange;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingSet;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class ShelvesetItem {
	private final ShelvesetGroupItemContainer parent;
	private final ShelvesetGroupItem parentGroup;
	private final ShelvesetUserItem parentUser;

	private Shelveset shelveset;

	private DiscussionInfo discussionInfo;

	private List<ShelvesetResourceItem> children;
	private boolean isChildrenRefreshed;

	public ShelvesetItem(ShelvesetGroupItemContainer shelvesetItemContainer, ShelvesetGroupItem shelvesetGroup, Shelveset shelveset) {
		this(shelvesetItemContainer, shelvesetGroup, null, shelveset);
	}

	public ShelvesetItem(ShelvesetGroupItemContainer shelvesetItemContainer, ShelvesetGroupItem shelvesetGroup, ShelvesetUserItem shelvesetUser,
			Shelveset shelveset) {
		this.shelveset = shelveset;
		this.parentGroup = shelvesetGroup;
		this.parentUser = shelvesetUser;
		this.parent = shelvesetItemContainer;
	}

	public ShelvesetGroupItemContainer getParent() {
		return parent;
	}

	public String getName() {
		return shelveset.getName();
	}

	public String getComment() {
		return shelveset.getComment();
	}

	public String getOwnerDisplayName() {
		return shelveset.getOwnerDisplayName();
	}

	public String getOwnerName() {
		return shelveset.getOwnerName();
	}

	public List<ShelvesetResourceItem> getChildren() {
		return children;
	}

	public ShelvesetGroupItem getParentGroup() {
		return parentGroup;
	}

	public ShelvesetUserItem getParentUser() {
		return parentUser;
	}

	public boolean isChildrenRefreshed() {
		return isChildrenRefreshed;
	}

	public void scheduleRefresh() {
		new ShelvesetFileItemsRefreshJob(this).schedule();
	}

	public void refresh(IProgressMonitor monitor) {
		monitor.beginTask("Updating Shelveset", 0);
		List<ShelvesetFileItem> shelvesetFileItems = new ArrayList<ShelvesetFileItem>();

		VersionControlClient vC = TFSUtil.getVersionControlClient();
		PendingSet[] pendingSets = vC.queryShelvedChanges(getName(), getOwnerName(), null, true, null);

		if (pendingSets != null) {
			for (PendingSet pendingSet : pendingSets) {
				PendingChange[] pendingChanges = pendingSet.getPendingChanges();
				if (pendingChanges != null) {
					for (PendingChange pendingChange : pendingChanges) {
						ItemType itemType = pendingChange.getItemType();
						if (itemType == ItemType.FILE) {
							ShelvesetFileItem shelvesetResourceItem = new ShelvesetFileItem(this, pendingSet, pendingChange);
							shelvesetFileItems.add(shelvesetResourceItem);
						}

					}
				}
			}
		}

		children = ShelvesetUtil.groupShelvesetFileItems(this, shelvesetFileItems);
		isChildrenRefreshed = true;

		monitor.done();

		new UIJob("Shelveset Item Refresh") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				ShelvesetReviewPlugin.getDefault().fireShelvesetItemRefreshed(ShelvesetItem.this);
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getName().hashCode();
		result = prime * result + getOwnerName().hashCode();

		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShelvesetItem other = (ShelvesetItem) obj;
		if (other.getName().equals(getName()) && other.getOwnerName().equals(getOwnerName())) {
			return true;
		}
		return false;
	}

	public ShelvesetFileItem findFile(String path) {
		ShelvesetFileItem result = null;
		List<ShelvesetResourceItem> children = getChildren();
		if (children != null) {
			for (ShelvesetResourceItem child : children) {
				result = findFileInternal(child, path);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	private ShelvesetFileItem findFileInternal(ShelvesetResourceItem resourceItem, String path) {
		ShelvesetFileItem result = null;
		if (resourceItem instanceof ShelvesetFileItem && resourceItem.getPath().equals(path)) {
			result = (ShelvesetFileItem) resourceItem;
		} else if (resourceItem instanceof ShelvesetFolderItem) {
			ShelvesetFolderItem shelvesetFolderItem = (ShelvesetFolderItem) resourceItem;
			for (ShelvesetResourceItem child : shelvesetFolderItem.getChildren()) {
				result = findFileInternal(child, path);
				if (result != null) {
					break;
				}
			}
		}
		return result;
	}

	public boolean isInactive() {
		return ShelvesetUtil.isShelvesetInactive(shelveset);
	}

	public boolean canActivate() {
		return ShelvesetUtil.canActivateShelveset(shelveset);
	}

	public void markShelvesetActive(IProgressMonitor monitor) {
		ShelvesetUtil.markShelvesetActive(shelveset);
	}

	public void markShelvesetInactive(IProgressMonitor monitor) {
		ShelvesetUtil.markShelvesetInactive(shelveset);
	}

	public boolean delete(IProgressMonitor monitor) {
		boolean result = false;
		if (isInactive()) {
			ShelvesetUtil.deleteShelveset(shelveset);
			result = getParent().removeShelveset(shelveset);
		}
		return result;
	}

	public String getBuildId() {
		return ShelvesetUtil.getShelvesetBuildId(shelveset);
	}

	public String getChangesetNumber() {
		return ShelvesetUtil.getShelvesetChangesetNumber(shelveset);
	}

	public List<ReviewerInfo> getReviewers() {
		return ShelvesetUtil.getShelvesetReviewers(shelveset);
	}

	public boolean canAssignReviewers() {
		return ShelvesetUtil.canAssignReviewers(shelveset);
	}

	public void assignReviewers(List<ReviewerInfo> reviewerInfos) {
		ShelvesetUtil.assignReviewers(shelveset, reviewerInfos);
	}

	public void refreshDiscussion(IProgressMonitor monitor) throws IOException {
		monitor.beginTask("Retrieveing discussion for Shelveset " + getName(), 1);
		discussionInfo = ShelvesetUtil.retrieveDiscussion(shelveset);
		monitor.worked(1);
		monitor.done();
	}

	public DiscussionInfo getDiscussionInfo() {
		return discussionInfo;
	}

}
