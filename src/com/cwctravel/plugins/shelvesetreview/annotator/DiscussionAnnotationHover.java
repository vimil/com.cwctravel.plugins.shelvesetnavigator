package com.cwctravel.plugins.shelvesetreview.annotator;

import org.eclipse.jface.text.DefaultTextHover;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.ISourceViewer;

public class DiscussionAnnotationHover extends DefaultTextHover {
	public DiscussionAnnotationHover(ISourceViewer sourceViewer) {
		super(sourceViewer);
	}

	@SuppressWarnings("deprecation")
	public String getHoverInfo(ISourceViewer sourceViewer, IRegion hoverRegion) {
		return super.getHoverInfo(sourceViewer, hoverRegion);
	}
}
