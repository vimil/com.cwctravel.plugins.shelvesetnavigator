package com.cwctravel.plugins.shelvesetreview.handlers.editor;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.annotator.DiscussionAnnotation;
import com.cwctravel.plugins.shelvesetreview.filesystem.TFSFileStore;
import com.cwctravel.plugins.shelvesetreview.util.EditorUtil;

public class EditDiscussionHandler extends AbstractHandler {
	private DiscussionAnnotation discussionAnnotation;
	private boolean isEditorClicked;

	public void setEnabled(Object evaluationContext) {
		if (evaluationContext instanceof IEvaluationContext) {
			IEvaluationContext context = (IEvaluationContext) evaluationContext;
			String contextId = (String) context.getVariable("debugString");
			isEditorClicked = contextId != null && contextId.endsWith("EditorContext");
		}
	}

	public boolean isEnabled() {
		discussionAnnotation = null;

		boolean result = false;
		if (isEditorClicked) {
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
							TextSelection selection = (TextSelection) textEditor.getSelectionProvider().getSelection();
							if (selection != null) {
								discussionAnnotation = EditorUtil.getDiscussionAnnotationAtOffset(editorPart, selection.getOffset(),
										selection.getLength(), false);
								result = discussionAnnotation != null;
							}
						}

					} catch (CoreException e) {
						ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
					}
				}
			}
		}
		return result;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (discussionAnnotation != null) {
			IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IEditorPart editorPart = activeWorkbenchWindow.getActivePage().getActiveEditor();
			EditorUtil.showDiscussionDialog(editorPart, discussionAnnotation.getStartLine(), discussionAnnotation.getStartColumn(),
					discussionAnnotation.getEndLine(), discussionAnnotation.getEndColumn());
		}
		return null;
	}
}
