package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.util.IconManager;
import com.cwctravel.plugins.shelvesetreview.util.ImageUtil;

public class ShelvesetFolderItem extends ShelvesetResourceItem implements IItemContainer<Object, ShelvesetResourceItem> {

	private final String folderName;

	private List<ShelvesetResourceItem> children;

	public ShelvesetFolderItem(ShelvesetItem root, String folderName) {
		super(root);
		this.folderName = folderName;

	}

	public void addChild(ShelvesetResourceItem shelvesetResourceItem) {
		getChildren().add(shelvesetResourceItem);
		shelvesetResourceItem.setParentFolder(this);
	}

	public String getName() {
		return folderName;
	}

	public List<ShelvesetResourceItem> getChildren() {
		if (children == null) {
			children = new ArrayList<ShelvesetResourceItem>();
		}
		return children;
	}

	public Object getItemParent() {
		Object result = getParentFolder();
		if (result == null) {
			result = getParent();
		}

		return result;
	}

	public String getText() {
		return getName();
	}

	@Override
	public String getPath() {
		String result = folderName;
		ShelvesetFolderItem parentFolder = getParentFolder();
		if (parentFolder != null) {
			result = parentFolder.getPath() + "/" + result;
		}
		return result;
	}

	public boolean hasDiscussions() {
		boolean result = false;
		for (ShelvesetResourceItem shelvesetResourceItem : getChildren()) {
			if (shelvesetResourceItem instanceof ShelvesetFileItem) {
				ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) shelvesetResourceItem;
				if (shelvesetFileItem.hasDiscussions()) {
					result = true;
					break;
				}
			} else if (shelvesetResourceItem instanceof ShelvesetFolderItem) {
				ShelvesetFolderItem shelvesetFolderItem = (ShelvesetFolderItem) shelvesetResourceItem;
				if (shelvesetFolderItem.hasDiscussions()) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	@Override
	public Image getImage() {
		Image image = ImageUtil.getImageForFolder();
		return image;
	}

	@Override
	public int itemCompareTo(IItemContainer<?, ?> itemContainer) {
		if (itemContainer instanceof ShelvesetFolderItem) {
			return getPath().compareTo(((ShelvesetFolderItem) itemContainer).getPath());
		} else if (itemContainer instanceof ShelvesetDiscussionItem) {
			return 1;
		} else if (itemContainer instanceof ShelvesetWorkItemContainer) {
			return -1;
		}
		return 0;
	}

	public void decorate(IDecoration decoration) {
		if (hasDiscussions()) {
			ImageDescriptor discussionOverlayImageDescriptor = IconManager.getDescriptor(IconManager.DISCUSSION_OVR_ICON_ID);
			decoration.addOverlay(discussionOverlayImageDescriptor, IDecoration.TOP_LEFT);
		}
	}
}
