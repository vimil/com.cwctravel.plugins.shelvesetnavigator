package com.cwctravel.plugins.shelvesetreview.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class ShelvesetItemDeleteJob extends Job {
	private ShelvesetItem shelvesetItem;

	public ShelvesetItemDeleteJob(ShelvesetItem shelvesetItem) {
		super("Deleting Shelveset " + shelvesetItem.getName());
		this.shelvesetItem = shelvesetItem;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (shelvesetItem.delete(monitor)) {
			ShelvesetReviewPlugin.getDefault().refreshShelvesetGroupItems(true, monitor);
		}
		return Status.OK_STATUS;
	}
}
