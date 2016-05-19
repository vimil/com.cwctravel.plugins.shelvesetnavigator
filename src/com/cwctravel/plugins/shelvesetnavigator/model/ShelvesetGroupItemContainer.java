package com.cwctravel.plugins.shelvesetnavigator.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.PlatformObject;

import com.cwctravel.plugins.shelvesetnavigator.constants.ShelvesetPropertyConstants;
import com.cwctravel.plugins.shelvesetnavigator.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class ShelvesetGroupItemContainer extends PlatformObject {
	private List<ShelvesetGroupItem> shelvesetGroupItems;

	public ShelvesetGroupItemContainer() {
		shelvesetGroupItems = new ArrayList<>();
		ShelvesetGroupItem userShelvesetGroupItem = new ShelvesetGroupItem(this,
				ShelvesetGroupItem.GROUP_TYPE_USER_SHELVESETS);
		ShelvesetGroupItem reviewerShelvesetGroupItem = new ShelvesetGroupItem(this,
				ShelvesetGroupItem.GROUP_TYPE_REVIEWER_SHELVESETS);
		ShelvesetGroupItem inactiveShelvesetGroupItem = new ShelvesetGroupItem(this,
				ShelvesetGroupItem.GROUP_TYPE_INACTIVE_SHELVESETS);

		shelvesetGroupItems.add(userShelvesetGroupItem);
		shelvesetGroupItems.add(reviewerShelvesetGroupItem);
		shelvesetGroupItems.add(inactiveShelvesetGroupItem);
	}

	public List<ShelvesetGroupItem> getShelvesetGroupItems() {
		return shelvesetGroupItems;
	}

	public void refreshShelvesetGroupItems() {
		VersionControlClient vC = TFSUtil.getVersionControlClient();
		if (vC != null) {
			Shelveset[] shelvesets = vC.queryShelvesets(null, null, ShelvesetPropertyConstants.SHELVESET_PROPERTIES);
			for (ShelvesetGroupItem shelvesetGroupItem : shelvesetGroupItems) {
				shelvesetGroupItem.createShelvesetItems(shelvesets);
			}
		}
	}

	public ShelvesetItem findShelvesetItem(String shelvesetName, String shelvesetOwnerName) {
		ShelvesetItem result = null;
		for (ShelvesetGroupItem shelvesetGroupItem : shelvesetGroupItems) {
			result = shelvesetGroupItem.findShelvesetItem(shelvesetName, shelvesetOwnerName);
			if (result != null) {
				break;
			}
		}
		return result;
	}

}
