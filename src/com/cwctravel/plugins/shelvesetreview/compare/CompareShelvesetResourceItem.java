package com.cwctravel.plugins.shelvesetreview.compare;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetResourceItem;
import com.cwctravel.plugins.shelvesetreview.util.ImageUtil;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public abstract class CompareShelvesetResourceItem implements IStructureComparator, ITypedElement {
	private ShelvesetResourceItem shelvesetResourceItem;
	private final ImageHelper imageHelper;

	public CompareShelvesetResourceItem(ShelvesetResourceItem shelvesetResourceItem, ImageHelper imageHelper) {
		this.shelvesetResourceItem = shelvesetResourceItem;
		this.imageHelper = imageHelper;
	}

	@Override
	public String getName() {
		return shelvesetResourceItem.getName();
	}

	@Override
	public Image getImage() {
		return ImageUtil.getItemImage(imageHelper, shelvesetResourceItem);
	}

	@Override
	public String getType() {
		return null;
	}

	@Override
	public Object[] getChildren() {
		return null;
	}

	public ShelvesetResourceItem getShelvesetResourceItem() {
		return shelvesetResourceItem;
	}

	public ImageHelper getImageHelper() {
		return imageHelper;
	}

}
