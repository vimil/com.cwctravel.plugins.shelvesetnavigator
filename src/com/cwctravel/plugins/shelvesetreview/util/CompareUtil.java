package com.cwctravel.plugins.shelvesetreview.util;

import java.io.IOException;
import java.util.List;

import org.eclipse.compare.contentmergeviewer.IMergeViewerContentProvider;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.compare.CompareShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.DiscussionService;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadPropertiesInfo;

@SuppressWarnings("restriction")
public class CompareUtil {
	public static final int LEFT_LEG = 0;
	public static final int RIGHT_LEG = 1;
	public static final int FOCUSED_LEG = 2;

	public static void setBackgroundColor(TextViewer textViewer, Color color, int start, int length, boolean controlRedraw) {
		StyledText textWidget = getStyledText(textViewer);
		if (textWidget != null) {
			StyleRange s = new StyleRange();
			s.background = color;
			s.start = start;
			s.length = length;
			s = modelStyleRange2WidgetStyleRange(textViewer, s);
			if (s != null) {
				if (controlRedraw)
					textWidget.setRedraw(false);
				try {
					textWidget.setStyleRange(s);
				} finally {
					if (controlRedraw)
						textWidget.setRedraw(true);
				}
			}
		}
	}

	private static StyleRange modelStyleRange2WidgetStyleRange(TextViewer textViewer, StyleRange s) {
		StyleRange result = null;
		if (textViewer != null) {
			result = (StyleRange) ReflectionUtil.invokeMethod(textViewer, "modelStyleRange2WidgetStyleRange", new Class<?>[] { StyleRange.class },
					new Object[] { s }, false);
		}
		return result;
	}

	public static StyledText getStyledText(TextViewer textViewer) {
		StyledText result = null;
		if (textViewer != null) {
			result = (StyledText) ReflectionUtil.getFieldValue(textViewer, "fTextWidget", false);
		}

		return result;
	}

	public static TextViewer getTextViewer(TextMergeViewer textMergeViewer, int leg) {
		TextViewer result = null;
		if (textMergeViewer != null) {
			String legFieldName = "fLeft";
			if (leg == 1) {
				legFieldName = "fRight";
			} else if (leg == 2) {
				legFieldName = "fFocusPart";
			}

			MergeSourceViewer mergeSourceViewer = (MergeSourceViewer) ReflectionUtil.getFieldValue(textMergeViewer, legFieldName, false);
			if (mergeSourceViewer != null) {
				result = mergeSourceViewer.getSourceViewer();
			}
		}
		return result;
	}

	public static void annotate(TextMergeViewer textMergeViewer, IProgressMonitor monitor) {
		if (textMergeViewer != null) {
			IMergeViewerContentProvider mergeViewerContentProvider = (IMergeViewerContentProvider) textMergeViewer.getContentProvider();
			if (mergeViewerContentProvider != null) {
				CompareShelvesetFileItem leftCompareShelvesetFileItem = (CompareShelvesetFileItem) mergeViewerContentProvider
						.getLeftContent(textMergeViewer.getInput());
				if (leftCompareShelvesetFileItem != null) {
					TextViewer leftTextViewer = getTextViewer(textMergeViewer, LEFT_LEG);
					if (leftTextViewer != null) {
						ShelvesetFileItem leftShelvesetFileItem = (ShelvesetFileItem) leftCompareShelvesetFileItem.getShelvesetResourceItem();
						if (leftShelvesetFileItem != null) {
							try {
								DiscussionInfo discussionInfo = DiscussionService.getShelvesetDiscussion(TFSUtil.getTFSConnection(),
										leftShelvesetFileItem.getShelvesetName(), leftShelvesetFileItem.getShelvesetOwnerName());
								List<DiscussionThreadInfo> discussionThreadInfos = DiscussionUtil.findAllDiscussionThreads(discussionInfo,
										leftShelvesetFileItem.getPath());

								Color highlightColor = Display.getCurrent().getSystemColor(SWT.COLOR_RED);

								IDocument leftDocument = leftTextViewer.getDocument();
								for (DiscussionThreadInfo discussionThreadInfo : discussionThreadInfos) {
									DiscussionThreadPropertiesInfo discussionThreadPropertiesInfo = discussionThreadInfo.getThreadProperties();
									if (discussionThreadPropertiesInfo != null) {
										int startLine = discussionThreadPropertiesInfo.getStartLine();
										int startColumn = discussionThreadPropertiesInfo.getStartColumn();
										int endLine = discussionThreadPropertiesInfo.getEndLine();
										int endColumn = discussionThreadPropertiesInfo.getEndColumn();

										int startOffset = leftDocument.getLineOffset(startLine - 1) + startColumn - 1;
										int endOffset = leftDocument.getLineOffset(endLine - 1) + endColumn - 1;

										CompareUtil.setBackgroundColor(leftTextViewer, highlightColor, startOffset, endOffset - startOffset, false);
									}
								}

								StyledText leftTextWidget = getStyledText(leftTextViewer);
								leftTextWidget.redraw();
							} catch (IOException | BadLocationException e) {
								ShelvesetReviewPlugin.log(IStatus.ERROR, e.getMessage(), e);
							}

						}
					}
				}
			}
		}
	}
}
