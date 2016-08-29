package com.cwctravel.plugins.shelvesetreview.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.annotator.DiscussionAnnotation;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.DiscussionService;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionCommentInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadPropertiesInfo;
import com.microsoft.tfs.core.TFSConnection;

public class AnnotationUtil {

	public static void annotateDocument(IDocument document, IAnnotationModel annotationModel, String shelvesetName, String shelvesetOwner,
			String path, IProgressMonitor monitor) {
		try {
			TFSConnection tfsConnection = TFSUtil.getTFSConnection();
			if (tfsConnection != null) {
				Map<String, Annotation> discussionAnnotationsMap = getAllDiscussionAnnotations(annotationModel);

				DiscussionInfo discussionInfo = DiscussionService.getShelvesetDiscussion(tfsConnection, shelvesetName, shelvesetOwner);

				List<DiscussionThreadInfo> discussionThreadInfos = DiscussionUtil.findAllDiscussionThreads(discussionInfo, path);
				for (DiscussionThreadInfo discussionThreadInfo : discussionThreadInfos) {
					DiscussionThreadPropertiesInfo discussionThreadPropertiesInfo = discussionThreadInfo.getThreadProperties();
					if (discussionThreadPropertiesInfo != null) {
						try {
							int startLine = discussionThreadPropertiesInfo.getStartLine();
							int startColumn = discussionThreadPropertiesInfo.getStartColumn();
							int endLine = discussionThreadPropertiesInfo.getEndLine();
							int endColumn = discussionThreadPropertiesInfo.getEndColumn();

							int startOffset = document.getLineOffset(startLine - 1) + startColumn - 1;

							int endOffset = document.getLineOffset(endLine - 1) + endColumn - 1;
							List<DiscussionCommentInfo> discussionComments = discussionThreadInfo.getComments();

							if (discussionComments != null) {
								for (DiscussionCommentInfo discussionCommentInfo : discussionComments) {
									StringBuilder commentsBuilder = new StringBuilder();
									commentsBuilder.append(discussionCommentInfo.getAuthor().getDisplayName());
									commentsBuilder.append("  ");
									commentsBuilder.append(DateUtil.formatDate(discussionCommentInfo.getLastUpdatedDate()));
									commentsBuilder.append(": ");
									commentsBuilder.append(discussionCommentInfo.getContent());

									String comment = commentsBuilder.toString();
									int threadId = discussionCommentInfo.getThreadId();
									int commentId = discussionCommentInfo.getId();
									String annotationKey = buildAnnotationKey(threadId, commentId, startLine, startColumn, endLine, endColumn);
									Annotation annotation = discussionAnnotationsMap.remove(annotationKey);
									if (annotation != null) {
										annotation.setText(comment);
									} else {
										annotation = new DiscussionAnnotation(discussionThreadInfo, discussionCommentInfo, comment);
										Position position = new Position(startOffset, endOffset - startOffset);
										annotationModel.addAnnotation(annotation, position);
									}
								}
							}
						} catch (BadLocationException e) {
						}
					}
				}

				for (Annotation annotation : discussionAnnotationsMap.values()) {
					annotationModel.removeAnnotation(annotation);
				}
			}
		} catch (IOException e) {
			ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
		}
	}

	private static String buildAnnotationKey(int threadId, int commentId, int startLine, int startColumn, int endLine, int endColumn) {
		StringBuilder keyBuilder = new StringBuilder();
		keyBuilder.append(threadId);
		keyBuilder.append(",");
		keyBuilder.append(commentId);
		keyBuilder.append(",");
		keyBuilder.append(startLine);
		keyBuilder.append(",");
		keyBuilder.append(startColumn);
		keyBuilder.append(",");
		keyBuilder.append(endLine);
		keyBuilder.append(",");
		keyBuilder.append(endColumn);
		return keyBuilder.toString();
	}

	private static Map<String, Annotation> getAllDiscussionAnnotations(IAnnotationModel annotationModel) {
		Map<String, Annotation> result = new HashMap<String, Annotation>();
		@SuppressWarnings("unchecked")
		Iterator<Annotation> annotationIterator = annotationModel.getAnnotationIterator();
		while (annotationIterator.hasNext()) {
			Annotation annotation = annotationIterator.next();
			if (annotation instanceof DiscussionAnnotation) {
				DiscussionAnnotation discussionAnnotation = (DiscussionAnnotation) annotation;
				int threadId = discussionAnnotation.getThreadId();
				int commentId = discussionAnnotation.getCommentId();
				int startLine = discussionAnnotation.getStartLine();
				int startColumn = discussionAnnotation.getStartColumn();
				int endLine = discussionAnnotation.getEndLine();
				int endColumn = discussionAnnotation.getEndColumn();
				String annotationKey = buildAnnotationKey(threadId, commentId, startLine, startColumn, endLine, endColumn);
				result.put(annotationKey, annotation);
			}
		}
		return result;
	}
}
