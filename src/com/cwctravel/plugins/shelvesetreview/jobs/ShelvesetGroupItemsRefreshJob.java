package com.cwctravel.plugins.shelvesetreview.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;

public class ShelvesetGroupItemsRefreshJob extends Job {
	private boolean refreshNavigator;

	public ShelvesetGroupItemsRefreshJob(boolean refreshNavigator) {
		super("Refreshing Shelvesets");
		this.refreshNavigator = refreshNavigator;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		ShelvesetReviewPlugin.getDefault().getShelvesetGroupItemContainer().refreshShelvesetGroupItems(refreshNavigator,
				false, monitor);
		return Status.OK_STATUS;
	}

}
