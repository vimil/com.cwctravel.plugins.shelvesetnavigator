package com.cwctravel.plugins.shelvesetreview.navigator;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.microsoft.tfs.client.common.repository.RepositoryManagerEvent;
import com.microsoft.tfs.client.common.repository.RepositoryManagerListener;

public class ShelvesetNavigatorRefresher implements RepositoryManagerListener {
	@Override
	public void onRepositoryRemoved(RepositoryManagerEvent event) {}

	@Override
	public void onRepositoryAdded(RepositoryManagerEvent event) {}

	@Override
	public void onDefaultRepositoryChanged(RepositoryManagerEvent event) {
		ShelvesetReviewPlugin.getDefault().refreshShelvesetGroupItems();
	}
}