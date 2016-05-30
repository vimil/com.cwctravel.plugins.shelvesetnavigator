package com.cwctravel.plugins.shelvesetreview.annotator;

import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;

public class DiscussionAnnotationHover extends DefaultAnnotationHover {
	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
		return super.getHoverInfo(sourceViewer, lineNumber);
	}
}
