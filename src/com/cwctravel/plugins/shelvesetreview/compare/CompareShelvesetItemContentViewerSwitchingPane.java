package com.cwctravel.plugins.shelvesetreview.compare;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.Splitter;
import org.eclipse.compare.internal.CompareContentViewerSwitchingPane;

@SuppressWarnings("restriction")
public class CompareShelvesetItemContentViewerSwitchingPane extends CompareContentViewerSwitchingPane {
	private CompareShelvesetItemInput compareShelvesetItemInput;

	public CompareShelvesetItemContentViewerSwitchingPane(Splitter parent, int style, CompareEditorInput cei) {
		super(parent, style, cei);
		compareShelvesetItemInput = (CompareShelvesetItemInput) cei;
	}

	public void setInput(Object input) {
		super.setInput(input);
		compareShelvesetItemInput.installListeners();
	}
}
