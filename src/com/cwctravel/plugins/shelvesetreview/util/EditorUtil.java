package com.cwctravel.plugins.shelvesetreview.util;

import java.util.ArrayList;
import java.util.Iterator;
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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.annotator.DiscussionAnnotation;
import com.cwctravel.plugins.shelvesetreview.dialogs.DiscussionCommentDialog;
import com.cwctravel.plugins.shelvesetreview.dialogs.DiscussionDialog;
import com.cwctravel.plugins.shelvesetreview.filesystem.TFSFileStore;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class EditorUtil {
	public static DiscussionAnnotation getDiscussionAnnotationAtLine(IEditorPart editor, int lineNumber) {
		DiscussionAnnotation result = null;
		try {
			IEditorInput editorInput = editor.getEditorInput();
			if (editorInput instanceof FileStoreEditorInput) {
				FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput) editorInput;
				IFileStore fileStore = EFS.getStore(fileStoreEditorInput.getURI());
				if (editor instanceof ITextEditor && fileStore instanceof TFSFileStore) {
					ITextEditor textEditor = (ITextEditor) editor;
					IDocumentProvider documentProvider = textEditor.getDocumentProvider();
					IDocument document = documentProvider.getDocument(editorInput);
					IAnnotationModel annotationModel = documentProvider.getAnnotationModel(editorInput);

					Iterator<?> itr = annotationModel.getAnnotationIterator();
					while (itr.hasNext()) {
						Annotation annotation = (Annotation) itr.next();
						if (annotation instanceof DiscussionAnnotation) {
							Position position = annotationModel.getPosition(annotation);
							if (position != null) {
								int offset = position.getOffset();
								int annotationLineNumber = document.getLineOfOffset(offset);

								if (lineNumber == annotationLineNumber) {
									result = (DiscussionAnnotation) annotation;
									break;
								}
							}
						}
					}
				}
			}
		} catch (CoreException | BadLocationException e) {
			ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
		}
		return result;
	}

	public static IRegion getLineInfo(IEditorPart editor, int lineNumber) {
		IRegion result = null;
		try {
			IEditorInput editorInput = editor.getEditorInput();
			if (editorInput instanceof FileStoreEditorInput) {
				FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput) editorInput;
				IFileStore fileStore = EFS.getStore(fileStoreEditorInput.getURI());
				if (editor instanceof ITextEditor && fileStore instanceof TFSFileStore) {
					ITextEditor textEditor = (ITextEditor) editor;
					IDocumentProvider documentProvider = textEditor.getDocumentProvider();
					IDocument document = documentProvider.getDocument(editorInput);
					result = document.getLineInformation(lineNumber);
				}
			}
		} catch (CoreException | BadLocationException e) {
			ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
		}
		return result;
	}

	public static void showDiscussionDialog(IEditorPart editor, int startLine, int startColumn, int endLine, int endColumn) {
		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput instanceof FileStoreEditorInput) {
			FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput) editorInput;
			IFileStore fileStore;
			try {
				fileStore = EFS.getStore(fileStoreEditorInput.getURI());
				if (fileStore instanceof TFSFileStore) {
					TFSFileStore tfsFileStore = (TFSFileStore) fileStore;
					ShelvesetItem shelvesetItem = ShelvesetReviewPlugin.getDefault().getShelvesetGroupItemContainer()
							.findShelvesetItem(tfsFileStore.getShelvesetName(), tfsFileStore.getShelvesetOwnerName());
					if (shelvesetItem != null) {
						new Job("") {

							@Override
							protected IStatus run(IProgressMonitor monitor) {
								shelvesetItem.refresh(monitor);
								ShelvesetFileItem shelvesetFileItem = shelvesetItem.findFile(tfsFileStore.getPath());
								if (shelvesetFileItem != null) {
									Display.getDefault().asyncExec(() -> {
										Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
										DiscussionDialog discussionDialog = new DiscussionDialog(shelvesetItem, shelvesetFileItem.getPath(),
												startLine, startColumn, endLine, endColumn, shell);
										discussionDialog.create();
										discussionDialog.open();
									});
								}
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

	public static void showDiscussionCommentDialog(ITextEditor editor, int startLine, int startColumn, int endLine, int endColumn) {
		IEditorInput editorInput = editor.getEditorInput();
		if (editorInput instanceof FileStoreEditorInput) {
			FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput) editorInput;
			IFileStore fileStore;
			try {
				fileStore = EFS.getStore(fileStoreEditorInput.getURI());
				if (fileStore instanceof TFSFileStore) {
					TFSFileStore tfsFileStore = (TFSFileStore) fileStore;
					ShelvesetItem shelvesetItem = ShelvesetReviewPlugin.getDefault().getShelvesetGroupItemContainer()
							.findShelvesetItem(tfsFileStore.getShelvesetName(), tfsFileStore.getShelvesetOwnerName());
					if (shelvesetItem != null) {
						ShelvesetFileItem shelvesetFileItem = shelvesetItem.findFile(tfsFileStore.getPath());
						if (shelvesetFileItem != null) {
							Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
							DiscussionCommentDialog discussionCommentDialog = new DiscussionCommentDialog(shelvesetItem, shelvesetFileItem.getPath(),
									startLine, startColumn, endLine, endColumn, shell);
							discussionCommentDialog.create();
							discussionCommentDialog.open();
						}
					}
				}
			} catch (CoreException e) {
				ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
			}
		}
	}

	public static List<IEditorPart> getTFSFileStoreEditors() {
		List<IEditorPart> result = new ArrayList<IEditorPart>();
		IWorkbenchWindow[] workbenchWIndows = PlatformUI.getWorkbench().getWorkbenchWindows();
		if (workbenchWIndows != null) {
			for (IWorkbenchWindow workbenchWIndow : workbenchWIndows) {
				IWorkbenchPage[] workbenchPages = workbenchWIndow.getPages();
				if (workbenchPages != null) {
					for (IWorkbenchPage workbenchPage : workbenchPages) {
						for (IEditorReference editorReference : workbenchPage.getEditorReferences()) {
							IEditorPart editorPart = editorReference.getEditor(true);
							if (editorPart != null) {
								IEditorInput editorInput = editorPart.getEditorInput();
								if (editorInput instanceof FileStoreEditorInput) {
									FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput) editorInput;
									try {
										IFileStore fileStore = EFS.getStore(fileStoreEditorInput.getURI());
										if (fileStore instanceof TFSFileStore) {
											result.add(editorPart);
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
		return result;
	}

}
