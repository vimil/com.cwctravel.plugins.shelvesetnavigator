package com.cwctravel.plugins.shelvesetreview.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;

public class ShelvesetGroupItemsRefreshJob extends Job {

	public ShelvesetGroupItemsRefreshJob() {
		super("Refreshing Shelvesets");
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		ShelvesetReviewPlugin plugin = ShelvesetReviewPlugin.getDefault();
		plugin.refreshShelvesetGroupItems(false, monitor);
		return Status.OK_STATUS;
	}

}
