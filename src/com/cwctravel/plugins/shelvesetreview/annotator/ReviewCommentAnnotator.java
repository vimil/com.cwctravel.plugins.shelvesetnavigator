package com.cwctravel.plugins.shelvesetreview.annotator;

import java.io.IOException;
import java.util.List;

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
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.filesystem.TFSFileStore;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.DiscussionService;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionCommentInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionThreadPropertiesInfo;
import com.cwctravel.plugins.shelvesetreview.util.DateUtil;
import com.cwctravel.plugins.shelvesetreview.util.DiscussionUtil;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.client.common.repository.RepositoryManagerEvent;
import com.microsoft.tfs.client.common.repository.RepositoryManagerListener;
import com.microsoft.tfs.core.TFSConnection;

public class ReviewCommentAnnotator implements RepositoryManagerListener, IWindowListener, IPartListener {
	private static final String REVIEW_COMMENT_MARKER = "com.cwctravel.plugins.shelvesetreview.commentMarker";

	private void annotate(TFSFileStore tfsFileStore, IDocument document, IAnnotationModel annotationModel, IProgressMonitor monitor) {
		String shelvesetName = tfsFileStore.getShelvesetName();
		String shelvesetOwner = tfsFileStore.getShelvesetOwnerName();
		try {
			TFSConnection tfsConnection = TFSUtil.getTFSConnection();
			if (tfsConnection != null) {
				DiscussionInfo discussionInfo = DiscussionService.getShelvesetDiscussion(tfsConnection, shelvesetName, shelvesetOwner);
				List<DiscussionThreadInfo> discussionThreadInfos = DiscussionUtil.findAllThreads(discussionInfo, tfsFileStore.getPath());
				for (DiscussionThreadInfo discussionThreadInfo : discussionThreadInfos) {
					DiscussionThreadPropertiesInfo discussionThreadPropertiesInfo = discussionThreadInfo.getThreadProperties();
					if (discussionThreadPropertiesInfo != null) {
						try {
							int startOffset = document.getLineOffset(discussionThreadPropertiesInfo.getStartLine() - 1)
									+ discussionThreadPropertiesInfo.getStartColumn() - 1;

							int endOffset = document.getLineOffset(discussionThreadPropertiesInfo.getEndLine() - 1)
									+ discussionThreadPropertiesInfo.getEndColumn() - 1;
							List<DiscussionCommentInfo> discussionComments = discussionThreadInfo.getComments();

							if (discussionComments != null) {
								for (DiscussionCommentInfo discussionCommentInfo : discussionComments) {
									StringBuilder commentsBuilder = new StringBuilder();
									commentsBuilder.append(discussionCommentInfo.getAuthor().getDisplayName());
									commentsBuilder.append("  ");
									commentsBuilder.append(DateUtil.formatDate(discussionCommentInfo.getLastUpdatedDate()));
									commentsBuilder.append(": ");
									commentsBuilder.append(discussionCommentInfo.getContent());

									Annotation annotation = new Annotation(REVIEW_COMMENT_MARKER, false, commentsBuilder.toString());
									Position position = new Position(startOffset, endOffset - startOffset);
									annotationModel.addAnnotation(annotation, position);
								}
							}
						} catch (BadLocationException e) {
						}
					}
				}
			}
		} catch (IOException e) {
			ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
		}
	}

	@Override
	public void onRepositoryAdded(RepositoryManagerEvent event) {
	}

	@Override
	public void onRepositoryRemoved(RepositoryManagerEvent event) {
	}

	@Override
	public void onDefaultRepositoryChanged(RepositoryManagerEvent event) {
		IWorkbenchWindow[] workbenchWIndows = PlatformUI.getWorkbench().getWorkbenchWindows();
		if (workbenchWIndows != null) {
			for (IWorkbenchWindow workbenchWIndow : workbenchWIndows) {
				IWorkbenchPage[] workbenchPages = workbenchWIndow.getPages();
				if (workbenchPages != null) {
					for (IWorkbenchPage workbenchPage : workbenchPages) {
						for (IEditorReference editorReference : workbenchPage.getEditorReferences()) {
							IEditorPart editorPart = editorReference.getEditor(false);
							annotateEditorPart(editorPart);
						}
					}
				}
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
						if (editorPart instanceof AbstractTextEditor) {
							AbstractTextEditor abstractTextEditor = (AbstractTextEditor) editorPart;
							IDocumentProvider documentProvider = abstractTextEditor.getDocumentProvider();
							IDocument document = documentProvider.getDocument(editorInput);
							IAnnotationModel annotationModel = documentProvider.getAnnotationModel(editorInput);
							new Job("Updating Review Comments") {
								@Override
								protected IStatus run(IProgressMonitor monitor) {
									ReviewCommentAnnotator.this.annotate(tfsFileStore, document, annotationModel, monitor);
									return Status.OK_STATUS;
								}
							}.schedule();
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

}
