package com.cwctravel.plugins.shelvesetreview.compare;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetResourceItem;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public abstract class CompareShelvesetResourceItem implements IStructureComparator, ITypedElement {
	private ShelvesetResourceItem shelvesetResourceItem;

	public CompareShelvesetResourceItem(ShelvesetResourceItem shelvesetResourceItem) {
		this.shelvesetResourceItem = shelvesetResourceItem;
	}

	@Override
	public String getName() {
		return shelvesetResourceItem.getName();
	}

	@Override
	public Image getImage() {
		return shelvesetResourceItem.getImage();
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
		return ShelvesetReviewPlugin.getDefault().getImageHelper();
	}

}
