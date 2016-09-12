package com.cwctravel.plugins.shelvesetreview.navigator;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;

import com.cwctravel.plugins.shelvesetreview.navigator.model.CodeReviewItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetResourceItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetUserCategoryItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetUserItem;
import com.cwctravel.plugins.shelvesetreview.util.ImageUtil;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public class ShelvesetLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {
	private final ImageHelper imageHelper;

	public ShelvesetLabelProvider() {
		imageHelper = new ImageHelper();
	}

	public Image getImage(Object element) {
		return ImageUtil.getItemImage(imageHelper, element);
	}

	public String getText(Object element) {
		String result = null;
		if (element instanceof ShelvesetGroupItem) {
			ShelvesetGroupItem shelvesetGroupItem = (ShelvesetGroupItem) element;
			result = shelvesetGroupItem.getName();
		} else if (element instanceof ShelvesetUserItem) {
			ShelvesetUserItem shelvesetUserItem = (ShelvesetUserItem) element;
			result = shelvesetUserItem.getShelvesetOwner();
		} else if (element instanceof ShelvesetUserCategoryItem) {
			ShelvesetUserCategoryItem shelvesetUserCategoryItem = (ShelvesetUserCategoryItem) element;
			result = shelvesetUserCategoryItem.getCategoryName();
		} else if (element instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) element;
			result = shelvesetItem.getName();
		} else if (element instanceof ShelvesetDiscussionItem) {
			ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) element;
			result = shelvesetDiscussionItem.getName();
		} else if (element instanceof ShelvesetResourceItem) {
			ShelvesetResourceItem shelvesetResourceItem = (ShelvesetResourceItem) element;
			result = shelvesetResourceItem.getName();
		} else if (element instanceof CodeReviewItemContainer) {
			result = "Code Reviews";
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
