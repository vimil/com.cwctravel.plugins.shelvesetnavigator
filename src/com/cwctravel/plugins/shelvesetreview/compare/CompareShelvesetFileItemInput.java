package com.cwctravel.plugins.shelvesetreview.compare;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareViewerSwitchingPane;
import org.eclipse.compare.Splitter;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.IProgressMonitor;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public class CompareShelvesetFileItemInput extends CompareEditorInput {
	private ShelvesetFileItem shelvesetFileItem;

	public CompareShelvesetFileItemInput(ShelvesetFileItem shelvesetFileItem, String title) {
		super(new ShelvesetCompareConfiguration());
		init(shelvesetFileItem, title);
	}

	private void init(ShelvesetFileItem shelvesetFileItem, String title) {
		this.shelvesetFileItem = shelvesetFileItem;
		setTitle(title);
		getCompareConfiguration().setLeftEditable(false);
		getCompareConfiguration().setRightEditable(false);
		getCompareConfiguration().setLeftLabel("[" + shelvesetFileItem.getShelvesetName() + "] " + shelvesetFileItem.getPath());
		getCompareConfiguration().setRightLabel(shelvesetFileItem.getSourcePath());
	}

	protected ImageHelper getImageHelper() {
		ShelvesetCompareConfiguration compareConfiguration = (ShelvesetCompareConfiguration) getCompareConfiguration();
		return compareConfiguration.getImageHelper();
	}

	protected CompareViewerSwitchingPane createContentViewerSwitchingPane(Splitter parent, int style, CompareEditorInput cei) {
		return new CompareShelvesetFileItemContentViewerSwitchingPane(parent, style, cei);
	}

	protected Object prepareInput(IProgressMonitor pm) {
		CompareShelvesetFileItem shelvedItem = new CompareShelvesetFileItem(shelvesetFileItem, getImageHelper());
		CompareShelvesetFileItem sourceItem = new CompareShelvesetFileItem(shelvesetFileItem, getImageHelper());

		return new DiffNode(shelvedItem, sourceItem);
	}

}
