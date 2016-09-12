package com.cwctravel.plugins.shelvesetreview.jobs;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class ShelvesetItemsDeleteJob extends Job {
	private List<ShelvesetItem> shelvesetItems;

	public ShelvesetItemsDeleteJob(List<ShelvesetItem> shelvesetItems) {
		super("Deleting Shelvesets ");
		this.shelvesetItems = shelvesetItems;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (shelvesetItems != null) {
			monitor.beginTask("Deleting Shelvesets", shelvesetItems.size());
			for (ShelvesetItem shelvesetItem : shelvesetItems) {
				monitor.subTask("Deleting Shelveset " + shelvesetItem.getName());
				shelvesetItem.delete(monitor);
				monitor.worked(1);
			}
			monitor.done();
			ShelvesetReviewPlugin.getDefault().refresh(true, monitor);
		}
		return Status.OK_STATUS;
	}
}
