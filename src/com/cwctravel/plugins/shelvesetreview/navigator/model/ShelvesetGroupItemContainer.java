package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;

import com.cwctravel.plugins.shelvesetreview.constants.ShelvesetPropertyConstants;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class ShelvesetGroupItemContainer extends PlatformObject {
	private List<ShelvesetGroupItem> shelvesetGroupItems;

	public ShelvesetGroupItemContainer() {
		shelvesetGroupItems = new ArrayList<>();
		ShelvesetGroupItem userShelvesetGroupItem = new ShelvesetGroupItem(this, ShelvesetGroupItem.GROUP_TYPE_USER_SHELVESETS);
		ShelvesetGroupItem reviewerShelvesetGroupItem = new ShelvesetGroupItem(this, ShelvesetGroupItem.GROUP_TYPE_REVIEWER_SHELVESETS);
		ShelvesetGroupItem inactiveShelvesetGroupItem = new ShelvesetGroupItem(this, ShelvesetGroupItem.GROUP_TYPE_INACTIVE_SHELVESETS);

		shelvesetGroupItems.add(userShelvesetGroupItem);
		shelvesetGroupItems.add(reviewerShelvesetGroupItem);
		shelvesetGroupItems.add(inactiveShelvesetGroupItem);
	}

	public List<ShelvesetGroupItem> getShelvesetGroupItems() {
		return shelvesetGroupItems;
	}

	public void refreshShelvesetGroupItems(IProgressMonitor monitor) {
		VersionControlClient vC = TFSUtil.getVersionControlClient();
		if(vC != null) {
			Shelveset[] shelvesets = vC.queryShelvesets(null, null, null);

			if(shelvesets != null) {
				Map<String, List<Shelveset>> userShelvesetItemsMap = new HashMap<String, List<Shelveset>>();
				for(int i = 0; i < shelvesets.length; i++) {
					Shelveset shelveset = shelvesets[i];
					String shelvesetOwner = shelveset.getOwnerName();

					List<Shelveset> userShelvesetList = userShelvesetItemsMap.get(shelvesetOwner);
					if(userShelvesetList == null) {
						userShelvesetList = new ArrayList<Shelveset>();
						userShelvesetItemsMap.put(shelvesetOwner, userShelvesetList);
					}
					userShelvesetList.add(shelveset);
				}

				monitor.beginTask("Refreshing Shelvsets", userShelvesetItemsMap.size());
				for(Map.Entry<String, List<Shelveset>> userShelvesetItemsMapEntry: userShelvesetItemsMap.entrySet()) {
					String shelvesetOwner = userShelvesetItemsMapEntry.getKey();
					monitor.subTask("Refreshing Shelvesets for " + shelvesetOwner);

					Shelveset[] shelvesetsWithProperties = vC.queryShelvesets(null, shelvesetOwner, ShelvesetPropertyConstants.SHELVESET_PROPERTIES);
					List<Shelveset> userShelvesetItems = new ArrayList<>();
					if(shelvesetsWithProperties != null) {
						for(Shelveset userShelveset: shelvesetsWithProperties) {
							userShelvesetItems.add(userShelveset);
						}
					}
					userShelvesetItemsMapEntry.setValue(userShelvesetItems);
					monitor.worked(1);
				}

				for(ShelvesetGroupItem shelvesetGroupItem: shelvesetGroupItems) {
					shelvesetGroupItem.createShelvesetItems(userShelvesetItemsMap);
				}
				monitor.done();
			}
		}
	}

	public ShelvesetItem findShelvesetItem(String shelvesetName, String shelvesetOwnerName) {
		ShelvesetItem result = null;
		for(ShelvesetGroupItem shelvesetGroupItem: shelvesetGroupItems) {
			result = shelvesetGroupItem.findShelvesetItem(shelvesetName, shelvesetOwnerName);
			if(result != null) {
				break;
			}
		}
		return result;
	}

}
