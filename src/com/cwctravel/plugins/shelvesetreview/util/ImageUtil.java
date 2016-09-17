package com.cwctravel.plugins.shelvesetreview.util;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;

public class ImageUtil {

	public static final Image getImageForFile(final String filename, final boolean useEditorRegistryImages) {
		if (useEditorRegistryImages && filename != null) {
			final ImageDescriptor imageDescriptor = PlatformUI.getWorkbench().getEditorRegistry().getImageDescriptor(filename);

			if (imageDescriptor != null) {
				return ShelvesetReviewPlugin.getDefault().getImageHelper().getImage(imageDescriptor);
			}
		}

		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
	}

	public static final Image getImageForFolder() {
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
	}
}
