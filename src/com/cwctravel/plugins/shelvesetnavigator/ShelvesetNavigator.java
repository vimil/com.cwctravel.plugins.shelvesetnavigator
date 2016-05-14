package com.cwctravel.plugins.shelvesetnavigator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.navigator.CommonNavigator;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItemContainer;

public class ShelvesetNavigator extends CommonNavigator {
	private ShelvesetItemContainer shelvesetItemContainer;

	public ShelvesetNavigator() {
		shelvesetItemContainer = new ShelvesetItemContainer();
	}

	protected IAdaptable getInitialInput() {
		return shelvesetItemContainer;
	}

	public void refresh(boolean updateLabels) {
		shelvesetItemContainer.refreshShelvesetItems();
		getCommonViewer().refresh(updateLabels);
	}
}
