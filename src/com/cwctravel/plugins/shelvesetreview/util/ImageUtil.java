package com.cwctravel.plugins.shelvesetreview.util;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.CodeReviewItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetUserCategoryItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetUserItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetWorkItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetWorkItemContainer;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public class ImageUtil {

	public static Image getItemImage(ImageHelper imageHelper, Object element) {
		Image result = null;
		Image image = null;
		if (element instanceof ShelvesetGroupItem) {
			ShelvesetGroupItem shelvesetGroupItem = (ShelvesetGroupItem) element;
			switch (shelvesetGroupItem.getGroupType()) {
				case ShelvesetGroupItem.GROUP_TYPE_CURRENT_USER_SHELVESETS:
					image = ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.USER_GROUP_ICON_ID);
					break;
				case ShelvesetGroupItem.GROUP_TYPE_OTHER_USER_SHELVESETS:
					image = ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.REVIEW_GROUP_ICON_ID);
					break;
				case ShelvesetGroupItem.GROUP_TYPE_INACTIVE_SHELVESETS:
					image = ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.INACTIVE_GROUP_ICON_ID);
					break;
			}
		} else if (element instanceof ShelvesetUserItem) {
			ShelvesetUserItem shelvesetUserItem = (ShelvesetUserItem) element;
			List<ShelvesetUserCategoryItem> userCategoryItems = shelvesetUserItem.getShelvesetUserCategoryItems();
			int userCategoryCount = userCategoryItems.size();
			if (userCategoryCount > 1) {
				image = ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.MIXED_USER_ICON_ID);
			} else if (userCategoryCount == 1) {
				ShelvesetUserCategoryItem shelvesetUserCategoryItem = userCategoryItems.get(0);
				image = ShelvesetReviewPlugin.getImage(shelvesetUserCategoryItem.getIconId());
			} else {
				image = ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.USER_ICON_ID);
			}
		} else if (element instanceof ShelvesetUserCategoryItem) {
			ShelvesetUserCategoryItem shelvesetUserCategoryItem = (ShelvesetUserCategoryItem) element;
			image = ShelvesetReviewPlugin.getImage(shelvesetUserCategoryItem.getIconId());
		} else if (element instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) element;
			if (shelvesetItem.isInactive()) {
				image = ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.INACTIVE_SHELVESET_ICON_ID);
			} else {
				image = ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.SHELVESET_ICON_ID);
			}

		} else if (element instanceof ShelvesetFileItem) {
			ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) element;
			image = getImageForFile(imageHelper, shelvesetFileItem.getPath(), true);
		} else if (element instanceof ShelvesetFolderItem) {
			image = getImageForFolder();
		} else if (element instanceof ShelvesetDiscussionItem) {
			image = ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.DISCUSSION_ICON_ID);
		} else if (element instanceof ShelvesetWorkItemContainer) {
			image = ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.WORKITEMS_ICON_ID);
		} else if (element instanceof ShelvesetWorkItem) {
			image = ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.WORKITEM_ICON_ID);
		} else if (element instanceof CodeReviewItemContainer) {
			image = ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.CODEREVIEW_ICON_ID);
		}
		result = image;

		return result;
	}

	protected static final Image getImageForFile(final ImageHelper imageHelper, final String filename, final boolean useEditorRegistryImages) {
		if (useEditorRegistryImages && filename != null) {
			final ImageDescriptor imageDescriptor = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(filename);

			if (imageDescriptor != null) {
				return imageHelper.getImage(imageDescriptor);
			}
		}

		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	}

	protected static final Image getImageForFolder() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
	}
}
