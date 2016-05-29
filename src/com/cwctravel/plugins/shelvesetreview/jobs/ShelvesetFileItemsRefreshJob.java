package com.cwctravel.plugins.shelvesetreview.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class ShelvesetFileItemsRefreshJob extends Job {
	ShelvesetItem shelvesetItem;

	public ShelvesetFileItemsRefreshJob(ShelvesetItem shelvesetItem) {
		super("Refreshing Shelveset");
		this.shelvesetItem = shelvesetItem;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		shelvesetItem.refresh(monitor);
		return Status.OK_STATUS;
	}
}