package com.cwctravel.plugins.shelvesetreview.compare;

import org.eclipse.compare.ICompareInputLabelProvider;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;

final class ShelvesetCompareLabelProvider extends LabelProvider implements ICompareInputLabelProvider {

	@Override
	public String getRightLabel(Object input) {
		String result = null;
		if (input instanceof DiffNode) {
			DiffNode diffNode = (DiffNode) input;
			result = getShelvesetFileItemLabel(diffNode.getRight());
		}
		return result;
	}

	@Override
	public Image getRightImage(Object input) {
		return null;
	}

	@Override
	public String getLeftLabel(Object input) {
		String result = null;
		if (input instanceof DiffNode) {
			DiffNode diffNode = (DiffNode) input;
			result = getShelvesetFileItemLabel(diffNode.getLeft());
		}
		return result;
	}

	private String getShelvesetFileItemLabel(ITypedElement leftElement) {
		String result = null;
		if (leftElement instanceof CompareShelvesetFileItem) {
			CompareShelvesetFileItem compareShelvesetItem = (CompareShelvesetFileItem) leftElement;
			ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) compareShelvesetItem.getShelvesetResourceItem();
			result = "[" + shelvesetFileItem.getShelvesetName() + "] " + shelvesetFileItem.getPath();
		}
		return result;
	}

	@Override
	public Image getLeftImage(Object input) {
		return null;
	}

	@Override
	public String getAncestorLabel(Object input) {
		String result = null;
		if (input instanceof DiffNode) {
			DiffNode diffNode = (DiffNode) input;
			result = getShelvesetFileItemLabel(diffNode.getAncestor());
		}
		return result;
	}

	@Override
	public Image getAncestorImage(Object input) {
		return null;
	}
}