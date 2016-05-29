package com.cwctravel.plugins.shelvesetreview;

import com.microsoft.tfs.client.common.repository.RepositoryManagerEvent;
import com.microsoft.tfs.client.common.repository.RepositoryManagerListener;

public class ShelvesetRepositoryManagerListener implements RepositoryManagerListener {
	@Override
	public void onRepositoryRemoved(RepositoryManagerEvent event) {
	}

	@Override
	public void onRepositoryAdded(RepositoryManagerEvent event) {
	}

	@Override
	public void onDefaultRepositoryChanged(RepositoryManagerEvent event) {
		ShelvesetReviewPlugin.getDefault().scheduleRefreshShelvesetGroupItems();
	}
}