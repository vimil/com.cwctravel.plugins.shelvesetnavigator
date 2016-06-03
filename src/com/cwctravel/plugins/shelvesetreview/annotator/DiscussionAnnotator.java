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
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
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
					if (EditorUtil.getDiscussionAnnotationAtLine(editorPart, lineNumber) != null) {
						EditorUtil.showDiscussionDialog(editorPart, lineNumber);
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
				DiscussionInfo discussionInfo = DiscussionService.getShelvesetDiscussion(tfsConnection, shelvesetName, shelvesetOwner);
				List<DiscussionThreadInfo> discussionThreadInfos = DiscussionUtil.findAllDiscussionThreads(discussionInfo, tfsFileStore.getPath());
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

									Annotation annotation = new DiscussionAnnotation(discussionCommentInfo.getThreadId(),
											discussionCommentInfo.getId(), commentsBuilder.toString());
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
		Display.getDefault().asyncExec(() -> {
			refreshEditors(null);
		});
	}

	private void refreshEditors(ShelvesetItem shelvesetItem) {
		IWorkbenchWindow[] workbenchWIndows = PlatformUI.getWorkbench().getWorkbenchWindows();
		if (workbenchWIndows != null) {
			for (IWorkbenchWindow workbenchWIndow : workbenchWIndows) {
				IWorkbenchPage[] workbenchPages = workbenchWIndow.getPages();
				if (workbenchPages != null) {
					for (IWorkbenchPage workbenchPage : workbenchPages) {
						for (IEditorReference editorReference : workbenchPage.getEditorReferences()) {
							IEditorPart editorPart = editorReference.getEditor(false);

							IEditorInput editorInput = editorPart.getEditorInput();
							if (editorInput instanceof FileStoreEditorInput) {
								FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput) editorInput;
								try {
									IFileStore fileStore = EFS.getStore(fileStoreEditorInput.getURI());
									if (fileStore instanceof TFSFileStore) {
										TFSFileStore tfsFileStore = (TFSFileStore) fileStore;

										if (shelvesetItem == null
												|| (StringUtil.equals(shelvesetItem.getName(), tfsFileStore.getShelvesetName()) && StringUtil.equals(
														shelvesetItem.getOwnerName(), tfsFileStore.getShelvesetOwnerName()))) {

											annotateEditorPart(editorPart);
										}

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
								} catch (CoreException e) {
									ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param editorPart
	 */
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
