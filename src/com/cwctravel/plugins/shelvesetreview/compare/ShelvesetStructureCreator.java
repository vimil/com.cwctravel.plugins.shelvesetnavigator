package com.cwctravel.plugins.shelvesetreview.compare;

import java.io.UnsupportedEncodingException;

import org.eclipse.compare.structuremergeviewer.IStructureComparator;
import org.eclipse.compare.structuremergeviewer.IStructureCreator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;

public class ShelvesetStructureCreator implements IStructureCreator {
	private String title;

	public ShelvesetStructureCreator(String title) {
		this.title = title;
	}

	@Override
	public String getName() {
		return title;
	}

	@Override
	public IStructureComparator getStructure(Object input) {
		return (IStructureComparator) input;
	}

	@Override
	public IStructureComparator locate(Object path, Object input) {
		return null;
	}

	@Override
	public String getContents(Object node, boolean ignoreWhitespace) {
		String result = null;
		if (node instanceof CompareShelvesetFileItem) {
			CompareShelvesetFileItem compareShelvesetFileItem = (CompareShelvesetFileItem) node;
			try {
				result = compareShelvesetFileItem.getContentsAsString();
			} catch (CoreException | UnsupportedEncodingException e) {
				ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
			}
		}
		return result;
	}

	@Override
	public void save(IStructureComparator node, Object input) {
	}

}
