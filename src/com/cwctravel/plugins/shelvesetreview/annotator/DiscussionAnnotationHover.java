package com.cwctravel.plugins.shelvesetreview.annotator;

import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;

public class DiscussionAnnotationHover extends DefaultAnnotationHover {
	public DiscussionAnnotationHover() {
	}

	public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
		return super.getHoverInfo(sourceViewer, lineNumber);
	}

	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new DiscussionAnnotationHover();
	}
}
