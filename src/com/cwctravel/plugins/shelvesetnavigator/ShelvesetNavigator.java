package com.cwctravel.plugins.shelvesetnavigator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.navigator.CommonNavigator;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItemContainer;

public class ShelvesetNavigator extends CommonNavigator {
	private ShelvesetItemContainer shelvesetItemContainer;

	public ShelvesetNavigator() {
		shelvesetItemContainer = ShelvesetNavigatorPlugin.getDefault().getShelvesetItemContainer();
	}

	protected IAdaptable getInitialInput() {
		return shelvesetItemContainer;
	}

}
