package com.cwctravel.plugins.shelvesetreview.jobs;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.cwctravel.plugins.shelvesetreview.jobs.ui.RefreshShelvesetsJob;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class ShelvesetItemsRefreshJob extends Job {
	private List<ShelvesetItem> shelvesetItems;

	public ShelvesetItemsRefreshJob(List<ShelvesetItem> shelvesetItems) {
		super("Refreshing Shelvesets");
		this.shelvesetItems = shelvesetItems;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (shelvesetItems != null) {
			monitor.beginTask("Refreshing Shelvesets", shelvesetItems.size());
			for (ShelvesetItem shelvesetItem : shelvesetItems) {
				monitor.subTask("Refreshing shelveset " + shelvesetItem.getName());
				shelvesetItem.refresh(monitor);
				monitor.worked(1);
			}
			monitor.done();

			new RefreshShelvesetsJob(shelvesetItems).schedule();
		}
		return Status.OK_STATUS;
	}
}