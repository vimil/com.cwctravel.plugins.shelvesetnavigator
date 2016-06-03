package com.cwctravel.plugins.shelvesetreview.jobs.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class RefreshShelvesetsJob extends UIJob {
	private List<ShelvesetItem> shelvesetItems;

	public RefreshShelvesetsJob(ShelvesetItem shelvesetItem) {
		super("Shelveset Item Refresh");
		shelvesetItems = new ArrayList<ShelvesetItem>();
		shelvesetItems.add(shelvesetItem);
	}

	public RefreshShelvesetsJob(List<ShelvesetItem> shelvesetItems) {
		super("Shelveset Items Refresh");
		this.shelvesetItems = shelvesetItems;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		if (shelvesetItems != null) {
			for (ShelvesetItem shelvesetItem : shelvesetItems) {
				ShelvesetReviewPlugin.getDefault().fireShelvesetItemRefreshed(shelvesetItem);
			}
		}
		return Status.OK_STATUS;
	}
}
