package com.cwctravel.plugins.shelvesetreview.jobs;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.UIJob;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class ShelvesetItemDiscussionRefreshJob extends Job {
	private ShelvesetItem shelvesetItem;

	public ShelvesetItemDiscussionRefreshJob(ShelvesetItem shelvesetItem) {
		super("Retrieving Shelveset Discussions for " + shelvesetItem.getName());
		this.shelvesetItem = shelvesetItem;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			shelvesetItem.refreshDiscussion(monitor);
			new UIJob("Shelveset Discussion Refresh") {

				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) {
					ShelvesetReviewPlugin.getDefault().fireShelvesetItemDiscussionRefreshed(shelvesetItem);
					return Status.OK_STATUS;
				}
			}.schedule();
		} catch (IOException e) {
			ShelvesetReviewPlugin.log(IStatus.ERROR, e.getMessage(), e);
		}
		return Status.OK_STATUS;
	}
}
