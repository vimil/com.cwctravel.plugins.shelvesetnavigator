package com.cwctravel.plugins.shelvesetreview.decorators;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import com.cwctravel.plugins.shelvesetreview.navigator.model.IItemContainer;

public class ShelvesetLabelDecorator implements ILightweightLabelDecorator {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IItemContainer<?, ?>) {
			IItemContainer<?, ?> itemContainer = (IItemContainer<?, ?>) element;
			itemContainer.decorate(decoration);
		}
	}
}
