package com.cwctravel.plugins.shelvesetreview.compare;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.ITypedElement;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetResourceItem;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public class CompareShelvesetFolderItem extends CompareShelvesetResourceItem {
	private String[] nameFragments;
	private int fragmentIndex;

	public CompareShelvesetFolderItem(ShelvesetFolderItem shelvesetFolderItem, ImageHelper imageHelper) {
		this(shelvesetFolderItem, imageHelper, 0);
	}

	public CompareShelvesetFolderItem(ShelvesetFolderItem shelvesetFolderItem, ImageHelper imageHelper, int fragmentIndex) {
		super(shelvesetFolderItem, imageHelper);
		this.fragmentIndex = fragmentIndex;
		String shelvesetFolderName = shelvesetFolderItem.getName();
		String[] fragments = shelvesetFolderName.split("/");
		if (fragments.length >= 2 && "$".equals(fragments[0])) {
			fragments[1] = "$/" + fragments[1];
			String[] newFragments = new String[fragments.length - 1];
			System.arraycopy(fragments, 1, newFragments, 0, fragments.length - 1);
			fragments = newFragments;
		}

		nameFragments = fragments;
	}

	protected CompareShelvesetFolderItem(ShelvesetFolderItem shelvesetFolderItem, ImageHelper imageHelper, int fragmentIndex,
			String[] nameFragments) {
		super(shelvesetFolderItem, imageHelper);
		this.fragmentIndex = fragmentIndex;
		this.nameFragments = nameFragments;
	}

	@Override
	public String getType() {
		return ITypedElement.FOLDER_TYPE;
	}

	@Override
	public Object[] getChildren() {
		Object[] result = null;
		List<CompareShelvesetResourceItem> resultList = new ArrayList<CompareShelvesetResourceItem>();
		ShelvesetFolderItem shelvesetFolderItem = (ShelvesetFolderItem) getShelvesetResourceItem();
		if (fragmentIndex < nameFragments.length - 1) {
			resultList.add(new CompareShelvesetFolderItem(shelvesetFolderItem, getImageHelper(), fragmentIndex + 1, nameFragments));
		} else {
			List<ShelvesetResourceItem> shelvesetResourceItems = shelvesetFolderItem.getChildren();
			if (shelvesetResourceItems != null) {

				for (ShelvesetResourceItem shelvesetResourceItem : shelvesetResourceItems) {
					if ((shelvesetResourceItem instanceof ShelvesetFolderItem)) {
						ShelvesetFolderItem childFolderItem = (ShelvesetFolderItem) shelvesetResourceItem;
						resultList.add(new CompareShelvesetFolderItem(childFolderItem, getImageHelper()));
					} else if (shelvesetResourceItem instanceof ShelvesetFileItem) {
						ShelvesetFileItem childFileItem = (ShelvesetFileItem) shelvesetResourceItem;
						resultList.add(new CompareShelvesetFileItem(childFileItem, true, getImageHelper()));
					}
				}
			}
		}
		if (!resultList.isEmpty()) {
			result = resultList.toArray(new CompareShelvesetResourceItem[0]);
		}
		return result;
	}

	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof CompareShelvesetFolderItem) {
			CompareShelvesetFolderItem otherCompareShelvesetFileItem = (CompareShelvesetFolderItem) other;
			result = getName().equals(otherCompareShelvesetFileItem.getName());
		}
		return result;
	}

	public String getName() {
		return nameFragments[fragmentIndex];
	}

	public int hashCode() {
		return getName().hashCode();
	}
}
