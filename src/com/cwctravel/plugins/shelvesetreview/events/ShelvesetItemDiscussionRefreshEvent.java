package com.cwctravel.plugins.shelvesetreview.events;

import org.eclipse.swt.widgets.Event;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class ShelvesetItemDiscussionRefreshEvent extends Event {
	private final ShelvesetItem shelvesetItem;

	public ShelvesetItemDiscussionRefreshEvent(ShelvesetItem shelvesetItem) {
		this.shelvesetItem = shelvesetItem;
	}

	public ShelvesetItem getShelvesetItem() {
		return shelvesetItem;
	}

}
