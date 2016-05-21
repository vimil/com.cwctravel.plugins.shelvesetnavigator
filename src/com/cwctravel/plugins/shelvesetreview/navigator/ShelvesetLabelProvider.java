package com.cwctravel.plugins.shelvesetreview.navigator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetResourceItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetUserItem;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public class ShelvesetLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {
	private final ImageHelper imageHelper;

	public ShelvesetLabelProvider() {
		imageHelper = new ImageHelper();
	}

	public Image getImage(Object element) {
		Image result = null;
		Image image = null;
		if (element instanceof ShelvesetGroupItem) {
			ShelvesetGroupItem shelvesetGroupItem = (ShelvesetGroupItem) element;
			switch (shelvesetGroupItem.getGroupType()) {
				case ShelvesetGroupItem.GROUP_TYPE_USER_SHELVESETS:
					image = ShelvesetReviewPlugin.getDefault().getImageRegistry()
							.get(ShelvesetReviewPlugin.USER_GROUP_ICON_ID);
					break;
				case ShelvesetGroupItem.GROUP_TYPE_REVIEWER_SHELVESETS:
					image = ShelvesetReviewPlugin.getDefault().getImageRegistry()
							.get(ShelvesetReviewPlugin.REVIEW_GROUP_ICON_ID);
					break;
				case ShelvesetGroupItem.GROUP_TYPE_INACTIVE_SHELVESETS:
					image = ShelvesetReviewPlugin.getDefault().getImageRegistry()
							.get(ShelvesetReviewPlugin.INACTIVE_GROUP_ICON_ID);
					break;
			}
		} else if (element instanceof ShelvesetUserItem) {
			image = ShelvesetReviewPlugin.getDefault().getImageRegistry().get(ShelvesetReviewPlugin.USER_ICON_ID);
		} else if (element instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) element;
			if (shelvesetItem.isInactive()) {
				image = ShelvesetReviewPlugin.getDefault().getImageRegistry()
						.get(ShelvesetReviewPlugin.INACTIVE_SHELVESET_ICON_ID);
			} else {
				image = ShelvesetReviewPlugin.getDefault().getImageRegistry()
						.get(ShelvesetReviewPlugin.SHELVESET_ICON_ID);
			}

		} else if (element instanceof ShelvesetFileItem) {
			ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) element;
			image = getImageForFile(shelvesetFileItem.getPath(), true);
		} else if (element instanceof ShelvesetFolderItem) {
			image = getImageForFolder();
		}

		result = image;

		return result;
	}

	public String getText(Object element) {
		String result = null;
		if (element instanceof ShelvesetGroupItem) {
			ShelvesetGroupItem shelvesetGroupItem = (ShelvesetGroupItem) element;
			result = shelvesetGroupItem.getName();
		} else if (element instanceof ShelvesetUserItem) {
			ShelvesetUserItem shelvesetUserItem = (ShelvesetUserItem) element;
			result = shelvesetUserItem.getShelvesetOwner();
		} else if (element instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) element;
			result = shelvesetItem.getName();
		} else if (element instanceof ShelvesetResourceItem) {
			ShelvesetResourceItem shelvesetResourceItem = (ShelvesetResourceItem) element;
			result = shelvesetResourceItem.getName();

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

	protected final Image getImageForFile(final String filename, final boolean useEditorRegistryImages) {
		if (useEditorRegistryImages && filename != null) {
			final ImageDescriptor imageDescriptor = PlatformUI.getWorkbench().getEditorRegistry()
					.getImageDescriptor(filename);

			if (imageDescriptor != null) {
				return imageHelper.getImage(imageDescriptor);
			}
		}

		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	}

	protected final Image getImageForFolder() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
	}
}
