package com.cwctravel.plugins.shelvesetreview.compare;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.Splitter;
import org.eclipse.compare.internal.CompareContentViewerSwitchingPane;

@SuppressWarnings("restriction")
public class ShelvesetCompareContentViewerSwitchingPane extends CompareContentViewerSwitchingPane {
	private CompareShelvesetItemInput compareShelvesetItemInput;

	public ShelvesetCompareContentViewerSwitchingPane(Splitter parent, int style, CompareEditorInput cei) {
		super(parent, style, cei);
		compareShelvesetItemInput = (CompareShelvesetItemInput) cei;
	}

	public void setInput(Object input) {
		super.setInput(input);
		compareShelvesetItemInput.installListeners();
	}
}
