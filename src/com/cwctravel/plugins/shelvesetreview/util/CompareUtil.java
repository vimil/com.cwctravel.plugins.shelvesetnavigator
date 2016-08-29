package com.cwctravel.plugins.shelvesetreview.util;

import java.util.List;

import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;

import com.cwctravel.plugins.shelvesetreview.constants.ShelvesetReviewConstants;

@SuppressWarnings("restriction")
public class CompareUtil {
	public static final int LEFT_LEG = 0;
	public static final int RIGHT_LEG = 1;
	public static final int FOCUSED_LEG = 2;

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

	public static AnnotationPreference getDiscussionAnnotationPreference() {
		AnnotationPreference result = null;
		MarkerAnnotationPreferences markerAnnotationPreferences = EditorsPlugin.getDefault().getMarkerAnnotationPreferences();
		@SuppressWarnings("unchecked")
		List<AnnotationPreference> annotationPreferenceList = markerAnnotationPreferences.getAnnotationPreferences();
		if (annotationPreferenceList != null) {
			for (AnnotationPreference annotationPreference : annotationPreferenceList) {
				if (ShelvesetReviewConstants.ANNOTATION_TYPE_DISCUSSION_MARKER.equals(annotationPreference.getAnnotationType())) {
					result = annotationPreference;
					break;
				}
			}
		}
		return result;
	}

}
