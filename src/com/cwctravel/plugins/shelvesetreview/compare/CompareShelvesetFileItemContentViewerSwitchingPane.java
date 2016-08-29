package com.cwctravel.plugins.shelvesetreview.compare;

import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.Splitter;
import org.eclipse.compare.internal.CompareContentViewerSwitchingPane;

@SuppressWarnings("restriction")
public class CompareShelvesetFileItemContentViewerSwitchingPane extends CompareContentViewerSwitchingPane {
	private CompareShelvesetFileItemInput compareShelvesetFileItemInput;

	public CompareShelvesetFileItemContentViewerSwitchingPane(Splitter parent, int style, CompareEditorInput cei) {
		super(parent, style, cei);
		compareShelvesetFileItemInput = (CompareShelvesetFileItemInput) cei;
	}

	public void setInput(Object input) {
		super.setInput(input);
		compareShelvesetFileItemInput.installListeners();
	}
}
