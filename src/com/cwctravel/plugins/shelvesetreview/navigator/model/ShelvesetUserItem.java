package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.List;

public class ShelvesetUserItem {
	private final String shelvesetOwner;

	private final ShelvesetGroupItem parentGroup;

	private final List<ShelvesetItem> shelvesetItems;

	public ShelvesetUserItem(ShelvesetGroupItem parentGroup, String shelvesetOwner, List<ShelvesetItem> shelvesetItems) {
		this.parentGroup = parentGroup;
		this.shelvesetOwner = shelvesetOwner;
		this.shelvesetItems = shelvesetItems;
	}

	public String getShelvesetOwner() {
		return shelvesetOwner;
	}

	public ShelvesetGroupItem getParentGroup() {
		return parentGroup;
	}

	public List<ShelvesetItem> getShelvesetItems() {
		return shelvesetItems;
	}

}
