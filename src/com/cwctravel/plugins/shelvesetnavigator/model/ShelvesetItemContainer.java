package com.cwctravel.plugins.shelvesetnavigator.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.PlatformObject;

import com.cwctravel.plugins.shelvesetnavigator.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class ShelvesetItemContainer extends PlatformObject {
	private List<ShelvesetItem> shelvesetItems;

	public ShelvesetItemContainer() {
		shelvesetItems = new ArrayList<>();
	}

	public List<ShelvesetItem> getShelvesetItems() {
		return shelvesetItems;
	}

	public void refreshShelvesetItems() {
		shelvesetItems.clear();
		VersionControlClient vC = TFSUtil.getVersionControlClient();
		if (vC != null) {
			Shelveset[] shelvesets = vC.queryShelvesets(null, TFSUtil.getCurrentUser(),
					new String[] { "cwctravel.reviewerIds", "cwctravel.approverIds" });
			if (shelvesets != null) {
				shelvesetItems = new ArrayList<ShelvesetItem>();
				for (Shelveset shelveset : shelvesets) {
					ShelvesetItem shelvesetItem = new ShelvesetItem(this, shelveset);
					shelvesetItems.add(shelvesetItem);
				}
			}
		}
	}
}
