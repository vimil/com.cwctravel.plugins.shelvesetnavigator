package com.cwctravel.plugins.shelvesetnavigator;

import com.microsoft.tfs.client.common.repository.RepositoryManagerEvent;
import com.microsoft.tfs.client.common.repository.RepositoryManagerListener;

final class ShelvesetNavigatorRefresher implements RepositoryManagerListener {
	@Override
	public void onRepositoryRemoved(RepositoryManagerEvent event) {}

	@Override
	public void onRepositoryAdded(RepositoryManagerEvent event) {}

	@Override
	public void onDefaultRepositoryChanged(RepositoryManagerEvent event) {
		ShelvesetNavigatorPlugin.getDefault().refreshShelvesetItems();
	}
}