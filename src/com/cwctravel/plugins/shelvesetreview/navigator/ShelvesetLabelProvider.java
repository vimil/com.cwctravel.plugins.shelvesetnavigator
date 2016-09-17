package com.cwctravel.plugins.shelvesetreview.navigator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;

import com.cwctravel.plugins.shelvesetreview.navigator.model.IItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public class ShelvesetLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {
	private final ImageHelper imageHelper;

	public ShelvesetLabelProvider() {
		imageHelper = new ImageHelper();
	}

	public Image getImage(Object element) {
		Image image = null;
		if (element instanceof IItemContainer<?, ?>) {
			IItemContainer<?, ?> itemContainer = (IItemContainer<?, ?>) element;
			image = itemContainer.getImage();
		}
		return image;
	}

	public String getText(Object element) {
		String result = null;
		if (element instanceof IItemContainer<?, ?>) {
			IItemContainer<?, ?> itemContainer = (IItemContainer<?, ?>) element;
			result = itemContainer.getText();
		}
		return result;
	}

	public String getDescription(Object element) {
		if (element instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) element;
			return shelvesetItem.getComment();
		}
		return null;
	}

	@Override
	public void dispose() {
		imageHelper.dispose();
	}

}
