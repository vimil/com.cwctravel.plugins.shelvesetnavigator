package com.cwctravel.plugins.shelvesetnavigator.model;

import java.util.ArrayList;
import java.util.List;

import com.cwctravel.plugins.shelvesetnavigator.util.ShelvesetUtil;
import com.cwctravel.plugins.shelvesetnavigator.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.ItemType;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingChange;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingSet;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class ShelvesetItem {
	private final ShelvesetItemContainer parent;
	private Shelveset shelveset;

	private List<ShelvesetResourceItem> children;

	public ShelvesetItem(ShelvesetItemContainer shelvesetItemContainer, Shelveset shelveset) {
		this.shelveset = shelveset;
		this.parent = shelvesetItemContainer;
	}

	public ShelvesetItemContainer getParent() {
		return parent;
	}

	public String getName() {
		return shelveset.getName();
	}

	public String getComment() {
		return shelveset.getComment();
	}

	public String getOwnerDisplayName() {
		return shelveset.getOwnerDisplayName();
	}

	public String getOwnerName() {
		return shelveset.getOwnerName();
	}

	public List<ShelvesetResourceItem> getChildren() {
		if (children == null) {
			refreshShelvesetFileItems();
		}

		return children;
	}

	private void refreshShelvesetFileItems() {
		List<ShelvesetFileItem> shelvesetFileItems = new ArrayList<ShelvesetFileItem>();

		VersionControlClient vC = TFSUtil.getVersionControlClient();
		PendingSet[] pendingSets = vC.queryShelvedChanges(getName(), getOwnerName(), null, true, null);
		if (pendingSets != null) {
			for (PendingSet pendingSet : pendingSets) {
				PendingChange[] pendingChanges = pendingSet.getPendingChanges();
				if (pendingChanges != null) {
					for (PendingChange pendingChange : pendingChanges) {
						ItemType itemType = pendingChange.getItemType();
						if (itemType == ItemType.FILE) {
							ShelvesetFileItem shelvesetResourceItem = new ShelvesetFileItem(this, pendingSet,
									pendingChange);
							shelvesetFileItems.add(shelvesetResourceItem);
						}

					}
				}
			}
		}

		children = ShelvesetUtil.groupShelvesetFileItems(this, shelvesetFileItems);
	}
}
