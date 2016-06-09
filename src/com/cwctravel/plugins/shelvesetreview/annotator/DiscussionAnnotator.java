package com.cwctravel.plugins.shelvesetreview.annotator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.events.ShelvesetItemRefreshEvent;
import com.cwctravel.plugins.shelvesetreview.filesystem.TFSFileStore;
import com.cwctravel.plugins.shelvesetreview.listeners.IShelvesetItemRefreshListener;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.DiscussionService;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionCommentInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadPropertiesInfo;
import com.cwctravel.plugins.shelvesetreview.util.DateUtil;
import com.cwctravel.plugins.shelvesetreview.util.DiscussionUtil;
import com.cwctravel.plugins.shelvesetreview.util.EditorUtil;
import com.cwctravel.plugins.shelvesetreview.util.StringUtil;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.client.common.repository.RepositoryManagerEvent;
import com.microsoft.tfs.client.common.repository.RepositoryManagerListener;
import com.microsoft.tfs.core.TFSConnection;

public class DiscussionAnnotator implements RepositoryManagerListener, IWindowListener, IPartListener, IShelvesetItemRefreshListener {
	private static class AnnotationMouseListener implements MouseListener {
		private IEditorPart editorPart;

		public AnnotationMouseListener(IEditorPart editorPart) {
			this.editorPart = editorPart;
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
			Object source = e.getSource();
			if (source != null && source.getClass().getSimpleName().startsWith("AnnotationRulerColumn")) {
				IVerticalRulerInfo verticalRulerInfo = (IVerticalRulerInfo) editorPart.getAdapter(IVerticalRulerInfo.class);
				if (verticalRulerInfo instanceof CompositeRuler) {
					CompositeRuler compositeRuler = (CompositeRuler) verticalRulerInfo;
					int lineNumber = compositeRuler.toDocumentLineNumber(e.y);
					DiscussionAnnotation discussionAnnotation = EditorUtil.getDiscussionAnnotationAtLine(editorPart, lineNumber);
					if (discussionAnnotation != null) {
						EditorUtil.showDiscussionDialog(editorPart, discussionAnnotation.getStartLine(), discussionAnnotation.getStartColumn(),
								discussionAnnotation.getEndLine(), discussionAnnotation.getEndColumn());
					}
				}
			}
		}

		@Override
		public void mouseDown(MouseEvent e) {

		}

		@Override
		public void mouseUp(MouseEvent e) {

		}

	}

	private void annotate(TFSFileStore tfsFileStore, IDocument document, IAnnotationModel annotationModel, IProgressMonitor monitor) {
		String shelvesetName = tfsFileStore.getShelvesetName();
		String shelvesetOwner = tfsFileStore.getShelvesetOwnerName();
		try {
			TFSConnection tfsConnection = TFSUtil.getTFSConnection();
			if (tfsConnection != null) {
				Map<String, Annotation> discussionAnnotationsMap = getAllDiscussionAnnotations(annotationModel);

				DiscussionInfo discussionInfo = DiscussionService.getShelvesetDiscussion(tfsConnection, shelvesetName, shelvesetOwner);
				List<DiscussionThreadInfo> discussionThreadInfos = DiscussionUtil.findAllDiscussionThreads(discussionInfo, tfsFileStore.getPath());
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

	private String buildAnnotationKey(int threadId, int commentId, int startLine, int startColumn, int endLine, int endColumn) {
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

	private Map<String, Annotation> getAllDiscussionAnnotations(IAnnotationModel annotationModel) {
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

	@Override
	public void onRepositoryAdded(RepositoryManagerEvent event) {
	}

	@Override
	public void onRepositoryRemoved(RepositoryManagerEvent event) {
	}

	@Override
	public void onDefaultRepositoryChanged(RepositoryManagerEvent event) {
		Display.getDefault().asyncExec(() -> {
			refreshEditors(null);
		});
	}

	private void refreshEditors(ShelvesetItem shelvesetItem) {
		List<IEditorPart> tfsFileStoreEditors = EditorUtil.getTFSFileStoreEditors();
		for (IEditorPart editor : tfsFileStoreEditors) {
			FileStoreEditorInput editorInput = (FileStoreEditorInput) editor.getEditorInput();
			try {
				TFSFileStore tfsFileStore = (TFSFileStore) EFS.getStore(editorInput.getURI());
				if (shelvesetItem == null || (StringUtil.equals(shelvesetItem.getName(), tfsFileStore.getShelvesetName())
						&& StringUtil.equals(shelvesetItem.getOwnerName(), tfsFileStore.getShelvesetOwnerName()))) {
					annotateEditorPart(editor);
				}
			} catch (CoreException e) {
				ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
			}
		}
	}

	private void annotateEditorPart(IEditorPart editorPart) {
		if (editorPart != null) {
			IEditorInput editorInput = editorPart.getEditorInput();
			if (editorInput instanceof FileStoreEditorInput) {
				FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput) editorInput;
				try {
					IFileStore fileStore = EFS.getStore(fileStoreEditorInput.getURI());
					if (fileStore instanceof TFSFileStore) {
						TFSFileStore tfsFileStore = (TFSFileStore) fileStore;
						if (editorPart instanceof ITextEditor) {
							ITextEditor textEditor = (ITextEditor) editorPart;
							IDocumentProvider documentProvider = textEditor.getDocumentProvider();
							IDocument document = documentProvider.getDocument(editorInput);

							IAnnotationModel annotationModel = documentProvider.getAnnotationModel(editorInput);
							new Job("Updating Review Comments") {
								@Override
								protected IStatus run(IProgressMonitor monitor) {
									DiscussionAnnotator.this.annotate(tfsFileStore, document, annotationModel, monitor);
									return Status.OK_STATUS;
								}
							}.schedule();

							IVerticalRulerInfo verticalRulerInfo = (IVerticalRulerInfo) editorPart.getAdapter(IVerticalRulerInfo.class);
							if (verticalRulerInfo instanceof CompositeRuler) {
								CompositeRuler compositeRuler = (CompositeRuler) verticalRulerInfo;
								Control control = compositeRuler.getControl();
								if (control instanceof Canvas) {
									Canvas canvas = (Canvas) control;
									AnnotationMouseListener discussionAnnotationMouseListener = (AnnotationMouseListener) canvas
											.getData("discussionAnnotationMouseListener");
									if (discussionAnnotationMouseListener == null) {
										discussionAnnotationMouseListener = new AnnotationMouseListener(editorPart);
										canvas.addMouseListener(discussionAnnotationMouseListener);
										canvas.setData("discussionAnnotationMouseListener", discussionAnnotationMouseListener);
									}
								}
							}
						}
					}
				} catch (CoreException e) {
					ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
	}

	@Override
	public void partClosed(IWorkbenchPart part) {
	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {
	}

	@Override
	public void partOpened(IWorkbenchPart part) {
		if (part instanceof IEditorPart) {
			IEditorPart editorPart = (IEditorPart) part;
			annotateEditorPart(editorPart);
		}
	}

	@Override
	public void windowActivated(IWorkbenchWindow window) {
	}

	@Override
	public void windowDeactivated(IWorkbenchWindow window) {
	}

	@Override
	public void windowClosed(IWorkbenchWindow window) {
	}

	@Override
	public void windowOpened(IWorkbenchWindow window) {
		window.getPartService().addPartListener(this);
	}

	@Override
	public void onShelvesetItemRefreshed(ShelvesetItemRefreshEvent event) {
		refreshEditors(event.getShelvesetItem());
	}

}
