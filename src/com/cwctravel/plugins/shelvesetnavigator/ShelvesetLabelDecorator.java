package com.cwctravel.plugins.shelvesetnavigator;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.graphics.Image;

public class ShelvesetLabelDecorator implements ILabelDecorator, ILightweightLabelDecorator {

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

	}

	@Override
	public Image decorateImage(Image image, Object element) {
		return image;
	}

	@Override
	public String decorateText(String text, Object element) {
		return text;
	}

}
