package com.cwctravel.plugins.shelvesetreview.handlers.editor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.annotator.DiscussionAnnotation;
import com.cwctravel.plugins.shelvesetreview.compare.CompareShelvesetItemInput;
import com.cwctravel.plugins.shelvesetreview.filesystem.TFSFileStore;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.util.CompareUtil;
import com.cwctravel.plugins.shelvesetreview.util.EditorUtil;

public class AddCommentHandler extends AbstractHandler {
	private boolean isEditorClicked;
	private boolean isCompareEditorClicked;
	private boolean isRulerClicked;
	private int startLine;
	private int endLine;
	private int startColumn;
	private int endColumn;
	private ShelvesetFileItem shelvesetFileItem;

	public void setEnabled(Object evaluationContext) {
		shelvesetFileItem = null;

		if (evaluationContext instanceof IEvaluationContext) {
			IEvaluationContext context = (IEvaluationContext) evaluationContext;
			String contextId = (String) context.getVariable("debugString");
			isEditorClicked = contextId != null && contextId.endsWith("EditorContext");
			isRulerClicked = contextId != null && contextId.endsWith("RulerContext");
			isCompareEditorClicked = contextId != null && contextId.equals("popup:org.eclipse.compare.CompareEditor");

			if (!isEditorClicked && !isRulerClicked && !isCompareEditorClicked) {
				Object selection = context.getVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME);
				if (selection instanceof TreeSelection) {
					TreeSelection treeSelection = (TreeSelection) selection;
					if (treeSelection.size() == 1) {
						Object firstElement = treeSelection.getFirstElement();
						if (firstElement instanceof ShelvesetFileItem) {
							shelvesetFileItem = (ShelvesetFileItem) firstElement;
						}
					}
				}
			}

		}
	}

	public boolean isEnabled() {
		boolean result = false;
		if (isEditorClicked || isRulerClicked) {
			IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IEditorPart editorPart = activeWorkbenchWindow.getActivePage().getActiveEditor();
			if (editorPart instanceof AbstractTextEditor) {
				IEditorInput editorInput = editorPart.getEditorInput();
				if (editorInput instanceof FileStoreEditorInput) {
					FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput) editorInput;
					try {
						IFileStore fileStore = EFS.getStore(fileStoreEditorInput.getURI());
						if (fileStore instanceof TFSFileStore) {
							AbstractTextEditor textEditor = (AbstractTextEditor) editorPart;
							if (isEditorClicked) {
								TextSelection selection = (TextSelection) textEditor.getSelectionProvider().getSelection();
								if (selection != null) {
									DiscussionAnnotation discussionAnnotation = EditorUtil.getDiscussionAnnotationAtOffset(editorPart,
											selection.getOffset(), selection.getLength(), false);
									if (discussionAnnotation == null) {
										IDocumentProvider documentProvider = textEditor.getDocumentProvider();
										IDocument document = documentProvider.getDocument(editorInput);

										startLine = selection.getStartLine() + 1;
										endLine = selection.getEndLine() + 1;
										startColumn = selection.getOffset() - document.getLineOffset(startLine - 1) + 1;
										endColumn = selection.getOffset() + selection.getLength() - document.getLineOffset(endLine - 1) + 1;
										result = true;
									}
								}
							} else {
								IVerticalRulerInfo rulerInfo = (IVerticalRulerInfo) textEditor.getAdapter(IVerticalRulerInfo.class);
								if (rulerInfo != null) {
									int rulerLineNumber = rulerInfo.getLineOfLastMouseButtonActivity();
									DiscussionAnnotation discussionAnnotation = EditorUtil.getDiscussionAnnotationAtLine(textEditor, rulerLineNumber);
									if (discussionAnnotation == null) {
										IDocumentProvider documentProvider = textEditor.getDocumentProvider();
										IDocument document = documentProvider.getDocument(editorInput);
										startLine = rulerLineNumber + 1;
										endLine = startLine;
										startColumn = 1;
										endColumn = document.getLineLength(rulerLineNumber) + 1;
										result = true;
									}
								}
							}
						}

					} catch (CoreException | BadLocationException e) {
						ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
					}
				}
			}
		} else if (isCompareEditorClicked) {
			IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IEditorPart editorPart = activeWorkbenchWindow.getActivePage().getActiveEditor();
			if (editorPart != null) {
				IEditorInput editorInput = editorPart.getEditorInput();
				if (editorInput instanceof CompareShelvesetItemInput) {
					try {
						CompareShelvesetItemInput compareShelvesetItemInput = (CompareShelvesetItemInput) editorInput;
						TextViewer textViewer = compareShelvesetItemInput.getTextViewer(CompareUtil.FOCUSED_LEG);
						if (textViewer != null) {
							TextSelection selection = (TextSelection) textViewer.getSelectionProvider().getSelection();
							if (selection != null) {
								DiscussionAnnotation discussionAnnotation = EditorUtil.getDiscussionAnnotationAtOffset(
										compareShelvesetItemInput.getAnnotationModel(CompareUtil.FOCUSED_LEG), selection.getOffset(),
										selection.getLength(), false);
								if (discussionAnnotation == null) {
									shelvesetFileItem = compareShelvesetItemInput.getShelvesetFileItem(CompareUtil.FOCUSED_LEG);
									IDocument document = textViewer.getDocument();
									startLine = selection.getStartLine() + 1;
									endLine = selection.getEndLine() + 1;
									startColumn = selection.getOffset() - document.getLineOffset(startLine - 1) + 1;
									endColumn = selection.getOffset() + selection.getLength() - document.getLineOffset(endLine - 1) + 1;
									result = true;
								}
							}
						}
					} catch (BadLocationException e) {
						ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
					}
				}
			}
		} else if (shelvesetFileItem != null) {
			result = true;
		}
		return result;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (isEditorClicked || isRulerClicked) {
			IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IEditorPart editorPart = activeWorkbenchWindow.getActivePage().getActiveEditor();
			AbstractTextEditor textEditor = (AbstractTextEditor) editorPart;
			EditorUtil.showDiscussionCommentDialog(textEditor, startLine, startColumn, endLine, endColumn);
		} else if (isCompareEditorClicked) {
			EditorUtil.showDiscussionCommentDialog(shelvesetFileItem, startLine, startColumn, endLine, endColumn);
		} else {
			EditorUtil.showDiscussionCommentDialog(shelvesetFileItem);
		}
		return null;
	}
}
