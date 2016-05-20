package com.cwctravel.plugins.shelvesetreview.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.IProgressMonitor;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;

public class CompareInput extends CompareEditorInput {
	private ShelvesetFileItem shelvesetFileItem;

	public CompareInput(ShelvesetFileItem shelvesetFileItem, String title) {
		super(new CompareConfiguration());
		init(shelvesetFileItem, title);
	}

	private void init(ShelvesetFileItem shelvesetFileItem, String title) {
		this.shelvesetFileItem = shelvesetFileItem;
		setTitle(title);
		getCompareConfiguration().setLeftEditable(false);
		getCompareConfiguration().setRightEditable(false);
		getCompareConfiguration()
				.setLeftLabel("[" + shelvesetFileItem.getShelvesetName() + "] " + shelvesetFileItem.getPath());
		getCompareConfiguration().setRightLabel(shelvesetFileItem.getSourcePath());
	}

	protected Object prepareInput(IProgressMonitor pm) {
		CompareItem shelvedItem = new CompareItem(shelvesetFileItem.getPath(),
				shelvesetFileItem.getShelvedDownloadURL());
		CompareItem sourceItem = new CompareItem(shelvesetFileItem.getSourcePath(), shelvesetFileItem.getDownloadUrl());

		return new DiffNode(shelvedItem, sourceItem);
	}

}
