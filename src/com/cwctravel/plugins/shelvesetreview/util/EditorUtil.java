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
					int lineOffset = document.getLineOffset(lineNumber);
					result = getDiscussionAnnotationClosestToOffset(editor, lineOffset);
					if (result != null) {
						if (lineNumber != result.getStartLine() - 1) {
							result = null;
						}
					}
				}
			}
		} catch (CoreException | BadLocationException e) {
			ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
		}
		return result;
	}

	private static IAnnotationModel getDiscussionAnnotationModel(IEditorPart editor) {
		IAnnotationModel result = null;
		try {
			IEditorInput editorInput = editor.getEditorInput();
			if (editorInput instanceof FileStoreEditorInput) {
				FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput) editorInput;
				IFileStore fileStore = EFS.getStore(fileStoreEditorInput.getURI());
				if (editor instanceof ITextEditor && fileStore instanceof TFSFileStore) {
					ITextEditor textEditor = (ITextEditor) editor;
					IDocumentProvider documentProvider = textEditor.getDocumentProvider();
					result = documentProvider.getAnnotationModel(editorInput);
				}
			}
		} catch (CoreException e) {
			ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
		}
		return result;
	}

	public static DiscussionAnnotation getDiscussionAnnotationClosestToOffset(IEditorPart editor, int offset) {
		DiscussionAnnotation result = getDiscussionAnnotationClosestToOffset(getDiscussionAnnotationModel(editor), offset);
		return result;
	}

	public static DiscussionAnnotation getDiscussionAnnotationClosestToOffset(IAnnotationModel annotationModel, int offset) {
		DiscussionAnnotation result = null;
		if (annotationModel != null) {
			Iterator<?> itr = annotationModel.getAnnotationIterator();
			int distanceFromOffset = -1;
			while (itr.hasNext()) {
				Annotation annotation = (Annotation) itr.next();
				if (annotation instanceof DiscussionAnnotation) {
					Position position = annotationModel.getPosition(annotation);
					if (position != null) {
						int annotationOffset = position.getOffset();
						if (annotationOffset >= offset && (distanceFromOffset == -1 || distanceFromOffset > (annotationOffset - offset))) {
							result = (DiscussionAnnotation) annotation;
							distanceFromOffset = annotationOffset - offset;
						}
					}
				}
			}
		}
		return result;
	}

	public static DiscussionAnnotation getDiscussionAnnotationAtOffset(IEditorPart editor, int offset, int length, boolean isContained) {
		DiscussionAnnotation result = getDiscussionAnnotationAtOffset(getDiscussionAnnotationModel(editor), offset, length, isContained);
		return result;
	}

	public static DiscussionAnnotation getDiscussionAnnotationAtOffset(IAnnotationModel annotationModel, int offset, int length,
			boolean isContained) {
		DiscussionAnnotation result = null;
		if (annotationModel != null) {
			Iterator<?> itr = annotationModel.getAnnotationIterator();
			while (itr.hasNext()) {
				Annotation annotation = (Annotation) itr.next();
				if (annotation instanceof DiscussionAnnotation) {
					Position position = annotationModel.getPosition(annotation);
					if (position != null) {
						int annotationOffset = position.getOffset();
						int annotationLength = position.getLength();

						if (isContained && annotationOffset >= offset && annotationOffset + annotationLength <= offset + length) {
							result = (DiscussionAnnotation) annotation;
							break;
						} else if (!isContained && annotationOffset <= offset && annotationOffset + annotationLength >= offset + length) {
							result = (DiscussionAnnotation) annotation;
							break;
						}
					}
				}
			}
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
					String shelvesetFileItemPath = tfsFileStore.getPath();
					showDiscussionDialog(shelvesetItem, shelvesetFileItemPath, startLine, startColumn, endLine, endColumn);
				}
			} catch (CoreException e) {
				ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
			}
		}
	}

	public static void showDiscussionDialog(ShelvesetItem shelvesetItem, String shelvesetFileItemPath, int startLine, int startColumn, int endLine,
			int endColumn) {
		if (shelvesetItem != null) {
			new Job("") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					shelvesetItem.refresh(monitor);

					Display.getDefault().asyncExec(() -> {
						Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						DiscussionDialog discussionDialog = new DiscussionDialog(shelvesetItem, shelvesetFileItemPath, startLine, startColumn,
								endLine, endColumn, shell);
						discussionDialog.create();
						discussionDialog.open();
					});
					return Status.OK_STATUS;
				}
			}.schedule();

		}
	}

	public static void showDiscussionDialog(ShelvesetItem shelvesetItem, int threadId) {
		if (shelvesetItem != null) {
			new Job("") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					shelvesetItem.refresh(monitor);

					Display.getDefault().asyncExec(() -> {
						Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						DiscussionDialog discussionDialog = new DiscussionDialog(shelvesetItem, threadId, shell);
						discussionDialog.create();
						discussionDialog.open();
					});
					return Status.OK_STATUS;
				}
			}.schedule();

		}
	}

	public static DiscussionCommentDialog showDiscussionCommentDialog(String title, String defaultComment) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		DiscussionCommentDialog discussionCommentDialog = new DiscussionCommentDialog(title, defaultComment, shell);
		discussionCommentDialog.create();
		discussionCommentDialog.open();
		return discussionCommentDialog;
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
						showDiscussionCommentDialog(shelvesetFileItem, startLine, startColumn, endLine, endColumn);
					}
				}
			} catch (CoreException e) {
				ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
			}
		}
	}

	public static void showDiscussionCommentDialog(ShelvesetFileItem shelvesetFileItem) {
		showDiscussionCommentDialog(shelvesetFileItem, -1, -1, -1, -1);
	}

	public static void showDiscussionCommentDialog(ShelvesetFileItem shelvesetFileItem, int startLine, int startColumn, int endLine, int endColumn) {
		if (shelvesetFileItem != null) {
			ShelvesetItem shelvesetItem = shelvesetFileItem.getParent();
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			DiscussionCommentDialog discussionCommentDialog = new DiscussionCommentDialog(shelvesetItem, shelvesetFileItem.getPath(), startLine,
					startColumn, endLine, endColumn, shell);
			discussionCommentDialog.create();
			discussionCommentDialog.open();
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
