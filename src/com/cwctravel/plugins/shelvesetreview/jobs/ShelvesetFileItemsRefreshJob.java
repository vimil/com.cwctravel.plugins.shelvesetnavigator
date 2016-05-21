package com.cwctravel.plugins.shelvesetreview.jobs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.cwctravel.plugins.shelvesetreview.jobs.ui.RefreshShelvesetNavigatorJob;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.ShelvesetUtil;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.ItemType;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingChange;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingSet;

public class ShelvesetFileItemsRefreshJob extends Job {
	ShelvesetItem shelvesetItem;
	boolean expand;

	public ShelvesetFileItemsRefreshJob(ShelvesetItem shelvesetItem, boolean expand) {
		super("Refreshing Shelveset");
		this.shelvesetItem = shelvesetItem;
		this.expand = expand;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Updating Shelveset", 0);
		List<ShelvesetFileItem> shelvesetFileItems = new ArrayList<ShelvesetFileItem>();

		VersionControlClient vC = TFSUtil.getVersionControlClient();
		PendingSet[] pendingSets = vC.queryShelvedChanges(shelvesetItem.getName(), shelvesetItem.getOwnerName(), null,
				true, null);

		if (pendingSets != null) {
			for (PendingSet pendingSet : pendingSets) {
				PendingChange[] pendingChanges = pendingSet.getPendingChanges();
				if (pendingChanges != null) {
					for (PendingChange pendingChange : pendingChanges) {
						ItemType itemType = pendingChange.getItemType();
						if (itemType == ItemType.FILE) {
							ShelvesetFileItem shelvesetResourceItem = new ShelvesetFileItem(shelvesetItem, pendingSet,
									pendingChange);
							shelvesetFileItems.add(shelvesetResourceItem);
						}

					}
				}
			}
		}

		shelvesetItem.setChildren(ShelvesetUtil.groupShelvesetFileItems(shelvesetItem, shelvesetFileItems));

		monitor.done();

		RefreshShelvesetNavigatorJob refreshShelvesetNavigatorJob = new RefreshShelvesetNavigatorJob();
		refreshShelvesetNavigatorJob.setShelvesetItem(shelvesetItem);
		refreshShelvesetNavigatorJob.setExpand(expand);
		refreshShelvesetNavigatorJob.schedule();
		this.shelvesetItem.setChildrenRefreshed(true);
		return Status.OK_STATUS;
	}
}