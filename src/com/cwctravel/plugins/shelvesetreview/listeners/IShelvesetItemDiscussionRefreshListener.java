package com.cwctravel.plugins.shelvesetreview.listeners;

import java.util.EventListener;

import com.cwctravel.plugins.shelvesetreview.events.ShelvesetItemDiscussionRefreshEvent;

public interface IShelvesetItemDiscussionRefreshListener extends EventListener {

	public void onShelvesetItemDiscussionRefreshed(ShelvesetItemDiscussionRefreshEvent event);
}
