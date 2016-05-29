package com.cwctravel.plugins.shelvesetreview.listeners;

import java.util.EventListener;

import com.cwctravel.plugins.shelvesetreview.events.ShelvesetItemRefreshEvent;

public interface IShelvesetItemRefreshListener extends EventListener {

	public void onShelvesetItemRefreshed(ShelvesetItemRefreshEvent event);

}
