package com.cwctravel.plugins.shelvesetreview.jobs;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class ShelvesetItemUpdateJob extends Job {
	public static final int UPDATE_TYPE_MARK_SHELVESET_ACTIVE = 0;
	public static final int UPDATE_TYPE_MARK_SHELVESET_INACTIVE = 1;

	private List<ShelvesetItem> shelvesetItems;

	private int updateType;

	public ShelvesetItemUpdateJob(List<ShelvesetItem> shelvesetItems, int updateType) {
		super("Updating Shelvesets");
		this.shelvesetItems = shelvesetItems;
		this.updateType = updateType;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Updating Shelvesets", shelvesetItems.size());
		for (ShelvesetItem shelvesetItem : shelvesetItems) {
			monitor.subTask("Updating Shelveset" + shelvesetItem.getName());
			switch (updateType) {
				case UPDATE_TYPE_MARK_SHELVESET_ACTIVE: {
					shelvesetItem.markShelvesetActive();
					break;
				}
				case UPDATE_TYPE_MARK_SHELVESET_INACTIVE: {
					shelvesetItem.markShelvesetInactive();
					break;
				}
			}
			monitor.worked(1);
		}
		monitor.done();
		ShelvesetReviewPlugin.getDefault().getShelvesetGroupItemContainer().refreshShelvesetGroupItems(true, true,
				monitor);
		return Status.OK_STATUS;
	}
}
