package com.cwctravel.plugins.shelvesetreview.listeners;

import java.util.EventListener;

import com.cwctravel.plugins.shelvesetreview.events.ShelvesetContainerRefreshEvent;

public interface IShelvesetContainerRefreshListener extends EventListener {

	public void onShelvesetContainerRefreshed(ShelvesetContainerRefreshEvent event);

}
