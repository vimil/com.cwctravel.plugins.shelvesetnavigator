package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.exceptions.ApproveException;
import com.cwctravel.plugins.shelvesetreview.jobs.ShelvesetItemsRefreshJob;
import com.cwctravel.plugins.shelvesetreview.jobs.ui.RefreshShelvesetsJob;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionInfo;
import com.cwctravel.plugins.shelvesetreview.rest.workitems.dto.WorkItemInfo;
import com.cwctravel.plugins.shelvesetreview.util.DiscussionUtil;
import com.cwctravel.plugins.shelvesetreview.util.IdentityUtil;
import com.cwctravel.plugins.shelvesetreview.util.ShelvesetUtil;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.cwctravel.plugins.shelvesetreview.util.WorkItemUtil;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.ItemType;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingChange;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingSet;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;
import com.microsoft.tfs.core.clients.versioncontrol.workspacecache.WorkItemCheckedInfo;

public class ShelvesetItem implements IAdaptable {
	private final ShelvesetGroupItemContainer parent;
	private final ShelvesetGroupItem parentGroup;
	private final ShelvesetUserItem parentUser;
	private final ShelvesetUserCategoryItem parentUserCategory;

	private Shelveset shelveset;

	private DiscussionInfo discussionInfo;

	private List<WorkItemInfo> workItems;

	private List<ShelvesetResourceItem> children;
	private boolean isChildrenRefreshed;

	public ShelvesetItem(ShelvesetGroupItemContainer shelvesetItemContainer, ShelvesetGroupItem shelvesetGroup, Shelveset shelveset) {
		this(shelvesetItemContainer, shelvesetGroup, null, null, shelveset);
	}

	public ShelvesetItem(ShelvesetGroupItemContainer shelvesetItemContainer, ShelvesetGroupItem shelvesetGroup, ShelvesetUserItem shelvesetUser,
			Shelveset shelveset) {
		this(shelvesetItemContainer, shelvesetGroup, shelvesetUser, null, shelveset);
	}

	public ShelvesetItem(ShelvesetGroupItemContainer shelvesetItemContainer, ShelvesetGroupItem shelvesetGroup, ShelvesetUserItem shelvesetUser,
			ShelvesetUserCategoryItem shelvesetUserCategory, Shelveset shelveset) {
		this.shelveset = shelveset;
		this.parentGroup = shelvesetGroup;
		this.parentUser = shelvesetUser;
		this.parentUserCategory = shelvesetUserCategory;
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

	public ShelvesetUserCategoryItem getParentUserCategory() {
		return parentUserCategory;
	}

	public boolean isChildrenRefreshed() {
		return isChildrenRefreshed;
	}

	public void scheduleRefresh() {
		scheduleRefresh(false);
	}

	public void scheduleRefresh(boolean waitForCompletion) {
		List<ShelvesetItem> shelvesetItems = new ArrayList<ShelvesetItem>();
		shelvesetItems.add(this);
		ShelvesetItemsRefreshJob shelvesetItemsRefreshJob = new ShelvesetItemsRefreshJob(shelvesetItems);
		shelvesetItemsRefreshJob.schedule();
		if (waitForCompletion) {
			try {
				shelvesetItemsRefreshJob.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
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
		try {
			discussionInfo = ShelvesetUtil.retrieveDiscussion(shelveset);
		} catch (IOException e) {
			ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
		}

		try {
			WorkItemCheckedInfo[] workItemCheckedInfos = shelveset.getBriefWorkItemInfo();
			if (workItemCheckedInfos != null && workItemCheckedInfos.length > 0) {
				List<Integer> workItemIds = new ArrayList<Integer>();
				for (WorkItemCheckedInfo workItemCheckedInfo : workItemCheckedInfos) {
					workItemIds.add(workItemCheckedInfo.getID());
				}
				workItems = WorkItemUtil.retrieveWorkItems(workItemIds);
			}
		} catch (IOException e) {
			ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
		}

		children = ShelvesetUtil.groupShelvesetFileItems(this, shelvesetFileItems, discussionInfo, workItems);

		isChildrenRefreshed = true;

		monitor.done();

		new RefreshShelvesetsJob(this).schedule();
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

	public List<ReviewerInfo> getReviewers(boolean includeDefaultReviewersGroupIfApproved) {
		return ShelvesetUtil.getReviewers(shelveset, includeDefaultReviewersGroupIfApproved ? parent.getDefaultReviewersGroup() : null, false);
	}

	public boolean canRequestCodeReview() {
		return ShelvesetUtil.canRequestCodeReview(shelveset);
	}

	public void createCodeReviewRequest(ShelvesetWorkItem shelvesetWorkItem, List<ReviewerInfo> reviewerInfos) {
		ShelvesetUtil.createCodeReviewRequest(shelveset, shelvesetWorkItem.getWorkItemID(), reviewerInfos);
	}

	public boolean hasDiscussions() {
		return DiscussionUtil.isDiscussionPresent(discussionInfo);
	}

	public Calendar getCreationDate() {
		return shelveset.getCreationDate();
	}

	public boolean canApprove() {
		return ShelvesetUtil.canApprove(shelveset, parent.getDefaultReviewersGroup());
	}

	public void approve(String approvalComment) throws ApproveException {
		ShelvesetUtil.approve(shelveset, approvalComment, parent.getDefaultReviewersGroup());

	}

	public boolean isApprovedByUser(String userId) {
		return ShelvesetUtil.isApprovedByUser(shelveset, userId);
	}

	public boolean isApproved() {
		return ShelvesetUtil.isApproved(shelveset);
	}

	public void unapprove(String revokeApprovalComment) throws ApproveException {
		ShelvesetUtil.unapprove(shelveset, revokeApprovalComment, parent.getDefaultReviewersGroup());
	}

	public Boolean canUnapprove() {
		return ShelvesetUtil.canUnapprove(shelveset, parent.getDefaultReviewersGroup());
	}

	public boolean isCurrentUserOwner() {
		return IdentityUtil.userNamesSame(IdentityUtil.getCurrentUserName(), shelveset.getOwnerName());
	}

	public Shelveset getShelveset() {
		return shelveset;
	}

	public String getShelvesetUrl() {
		return ShelvesetUtil.getShelvesetUrl(shelveset);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (ShelvesetItem.class.equals(adapter)) {
			return this;
		} else if (ShelvesetUserCategoryItem.class.equals(adapter)) {
			return getParentUserCategory();
		} else if (ShelvesetUserItem.class.equals(adapter)) {
			return getParentUser();
		}
		if (ShelvesetGroupItem.class.equals(adapter)) {
			return getParentGroup();
		} else if (ShelvesetGroupItemContainer.class.equals(adapter)) {
			return getParent();
		}
		return null;
	}

	public ShelvesetWorkItemContainer getWorkItemContainer() {
		ShelvesetWorkItemContainer result = null;
		List<ShelvesetResourceItem> children = getChildren();
		if (children != null && children.size() > 0) {
			ShelvesetResourceItem shelvesetResourceItem = children.get(0);
			if (shelvesetResourceItem instanceof ShelvesetWorkItemContainer) {
				result = (ShelvesetWorkItemContainer) shelvesetResourceItem;
			}
		}
		return result;
	}

}
