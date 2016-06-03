package com.cwctravel.plugins.shelvesetreview.annotator;

import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

public class DiscussionJavaSourceViewConfiguration extends JavaSourceViewerConfiguration {

	public DiscussionJavaSourceViewConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}

}
