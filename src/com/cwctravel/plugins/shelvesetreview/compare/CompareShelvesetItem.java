package com.cwctravel.plugins.shelvesetreview.compare;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.ITypedElement;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetResourceItem;

public class CompareShelvesetItem extends CompareShelvesetResourceItem {
	private ShelvesetItem shelvesetItem;

	public CompareShelvesetItem(ShelvesetItem shelvesetItem) {
		super(null);
		this.shelvesetItem = shelvesetItem;
	}

	@Override
	public String getName() {
		return shelvesetItem.getName();
	}

	@Override
	public String getType() {
		return ITypedElement.FOLDER_TYPE;
	}

	public ShelvesetItem getShelvesetItem() {
		return shelvesetItem;
	}

	@Override
	public Object[] getChildren() {
		Object[] result = null;
		List<ShelvesetResourceItem> shelvesetResourceItems = shelvesetItem.getChildren();

		if (shelvesetResourceItems != null) {
			List<CompareShelvesetResourceItem> resultList = new ArrayList<CompareShelvesetResourceItem>();
			for (ShelvesetResourceItem shelvesetResourceItem : shelvesetResourceItems) {
				if ((shelvesetResourceItem instanceof ShelvesetFolderItem)) {
					ShelvesetFolderItem childFolderItem = (ShelvesetFolderItem) shelvesetResourceItem;
					resultList.add(new CompareShelvesetFolderItem(childFolderItem));
				} else if (shelvesetResourceItem instanceof ShelvesetFileItem) {
					ShelvesetFileItem childFileItem = (ShelvesetFileItem) shelvesetResourceItem;
					resultList.add(new CompareShelvesetFileItem(childFileItem, true));
				}
			}
			result = resultList.toArray(new CompareShelvesetResourceItem[0]);
		}
		return result;
	}

	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof CompareShelvesetItem) {
			CompareShelvesetItem otherCompareShelvesetItem = (CompareShelvesetItem) other;
			result = getName().equals(otherCompareShelvesetItem.getName());
		}
		return result;
	}

	public int hashCode() {
		return getName().hashCode();
	}
}
