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
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.annotator.DiscussionAnnotation;
import com.cwctravel.plugins.shelvesetreview.filesystem.TFSFileStore;
import com.cwctravel.plugins.shelvesetreview.util.EditorUtil;

public class AddCommentHandler extends AbstractHandler {
	private boolean isEditorClicked;
	private boolean isRulerClicked;
	private int startLine;
	private int endLine;
	private int startColumn;
	private int endColumn;

	public void setEnabled(Object evaluationContext) {
		if (evaluationContext instanceof IEvaluationContext) {
			IEvaluationContext context = (IEvaluationContext) evaluationContext;
			String contextId = (String) context.getVariable("debugString");
			isEditorClicked = contextId != null && contextId.endsWith("EditorContext");
			isRulerClicked = contextId != null && contextId.endsWith("RulerContext");
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
		}
		return result;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IEditorPart editorPart = activeWorkbenchWindow.getActivePage().getActiveEditor();
		AbstractTextEditor textEditor = (AbstractTextEditor) editorPart;
		EditorUtil.showDiscussionCommentDialog(textEditor, startLine, startColumn, endLine, endColumn);
		return null;
	}
}
