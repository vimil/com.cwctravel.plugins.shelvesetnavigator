package com.cwctravel.plugins.shelvesetreview.events;

import org.eclipse.swt.widgets.Event;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class ShelvesetItemRefreshEvent extends Event {
	private final ShelvesetItem shelvesetItem;

	public ShelvesetItemRefreshEvent(ShelvesetItem shelvesetItem) {
		this.shelvesetItem = shelvesetItem;
	}

	public ShelvesetItem getShelvesetItem() {
		return shelvesetItem;
	}
}
