package com.cwctravel.plugins.shelvesetnavigator;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItem;

public class ShelvesetLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {
	private ILabelDecorator fLabelDecorator = null;

	public ShelvesetLabelProvider() {
		fLabelDecorator = new ShelvesetLabelDecorator();
	}

	public Image getImage(Object element) {
		Image result = null;
		if (element instanceof ShelvesetItem) {
			Image image = ShelvesetNavigatorPlugin.getDefault().getImageRegistry()
					.get(ShelvesetNavigatorPlugin.SHELVESET_ICON_ID);
			Image decorated = fLabelDecorator.decorateImage(image, element);

			if (decorated != null) {
				result = decorated;
			} else {
				result = image;
			}
		}
		return result;
	}

	public String getText(Object element) {
		if (element instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) element;
			return shelvesetItem.getName();
		}
		return null;
	}

	public String getDescription(Object element) {
		if (element instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) element;
			return shelvesetItem.getComment();
		}
		return null;
	}
}
