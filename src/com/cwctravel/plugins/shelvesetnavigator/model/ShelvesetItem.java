package com.cwctravel.plugins.shelvesetnavigator.model;

import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class ShelvesetItem {
	private final ShelvesetItemContainer parent;
	private Shelveset shelveset;

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
}
