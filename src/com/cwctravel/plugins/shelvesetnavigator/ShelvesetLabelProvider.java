package com.cwctravel.plugins.shelvesetnavigator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetResourceItem;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public class ShelvesetLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {
	private ILabelDecorator fLabelDecorator = null;
	private final ImageHelper imageHelper;

	public ShelvesetLabelProvider() {
		fLabelDecorator = new ShelvesetLabelDecorator();
		imageHelper = new ImageHelper();
	}

	public Image getImage(Object element) {
		Image result = null;
		Image image = null;
		if (element instanceof ShelvesetItem) {
			image = ShelvesetNavigatorPlugin.getDefault().getImageRegistry()
					.get(ShelvesetNavigatorPlugin.SHELVESET_ICON_ID);

		} else if (element instanceof ShelvesetFileItem) {
			ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) element;
			image = getImageForFile(shelvesetFileItem.getPath(), true);
		} else if (element instanceof ShelvesetFolderItem) {
			image = getImageForFolder();
		}

		Image decorated = fLabelDecorator.decorateImage(image, element);

		if (decorated != null) {
			result = decorated;
		} else {
			result = image;
		}
		return result;
	}

	public String getText(Object element) {
		String result = null;
		if (element instanceof ShelvesetItem) {
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
