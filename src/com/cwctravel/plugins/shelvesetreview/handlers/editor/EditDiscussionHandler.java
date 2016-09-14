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
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.annotator.DiscussionAnnotation;
import com.cwctravel.plugins.shelvesetreview.compare.CompareShelvesetItemInput;
import com.cwctravel.plugins.shelvesetreview.filesystem.TFSFileStore;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.util.CompareUtil;
import com.cwctravel.plugins.shelvesetreview.util.EditorUtil;

public class EditDiscussionHandler extends AbstractHandler {
	private DiscussionAnnotation discussionAnnotation;
	private boolean isEditorClicked;
	private boolean isCompareEditorClicked;
	private boolean isRulerClicked;

	private ShelvesetDiscussionItem shelvesetDiscussionItem;
	private ShelvesetFileItem shelvesetFileItem;

	public void setEnabled(Object evaluationContext) {
		shelvesetDiscussionItem = null;
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
						if (firstElement instanceof ShelvesetDiscussionItem) {
							shelvesetDiscussionItem = (ShelvesetDiscussionItem) firstElement;
						}
					}
				}
			}

		}

	}

	public boolean isEnabled() {
		discussionAnnotation = null;
		shelvesetFileItem = null;
		boolean result = false;
		if (isEditorClicked || isRulerClicked) {
			IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IEditorPart editorPart = activeWorkbenchWindow.getActivePage().getActiveEditor();
			ITextEditor textEditor = EditorUtil.getTextEditor(editorPart);
			if (textEditor != null) {
				IEditorInput editorInput = editorPart.getEditorInput();
				if (editorInput instanceof FileStoreEditorInput) {
					FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput) editorInput;
					try {
						IFileStore fileStore = EFS.getStore(fileStoreEditorInput.getURI());
						if (fileStore instanceof TFSFileStore) {
							if (isEditorClicked) {
								TextSelection selection = (TextSelection) textEditor.getSelectionProvider().getSelection();
								if (selection != null) {
									discussionAnnotation = EditorUtil.getDiscussionAnnotationAtOffset(editorPart, selection.getOffset(),
											selection.getLength(), false);
									result = discussionAnnotation != null;
								}
							} else {
								IVerticalRulerInfo rulerInfo = (IVerticalRulerInfo) textEditor.getAdapter(IVerticalRulerInfo.class);
								if (rulerInfo != null) {
									int rulerLineNumber = rulerInfo.getLineOfLastMouseButtonActivity();
									discussionAnnotation = EditorUtil.getDiscussionAnnotationAtLine(textEditor, rulerLineNumber);
									result = discussionAnnotation != null;
								}
							}
						}

					} catch (CoreException e) {
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
					CompareShelvesetItemInput compareShelvesetItemInput = (CompareShelvesetItemInput) editorInput;
					TextViewer textViewer = compareShelvesetItemInput.getTextViewer(CompareUtil.FOCUSED_LEG);
					if (textViewer != null) {
						TextSelection selection = (TextSelection) textViewer.getSelectionProvider().getSelection();
						if (selection != null) {
							discussionAnnotation = EditorUtil.getDiscussionAnnotationAtOffset(
									compareShelvesetItemInput.getAnnotationModel(CompareUtil.FOCUSED_LEG), selection.getOffset(),
									selection.getLength(), false);
							result = discussionAnnotation != null;
							if (result) {
								shelvesetFileItem = compareShelvesetItemInput.getShelvesetFileItem(CompareUtil.FOCUSED_LEG);
							}
						}
					}
				}
			}
		} else if (shelvesetDiscussionItem != null) {
			ShelvesetDiscussionItem parentDiscussion = shelvesetDiscussionItem.getParentDiscussion();
			if (shelvesetDiscussionItem != null && parentDiscussion == null) {
				result = true;
			}
		}
		return result;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (discussionAnnotation != null) {
			if (shelvesetFileItem != null) {
				EditorUtil.showDiscussionDialog(shelvesetFileItem.getParent(), shelvesetFileItem.getPath(), discussionAnnotation.getStartLine(),
						discussionAnnotation.getStartColumn(), discussionAnnotation.getEndLine(), discussionAnnotation.getEndColumn());
			} else {
				IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IEditorPart editorPart = activeWorkbenchWindow.getActivePage().getActiveEditor();
				EditorUtil.showDiscussionDialog(editorPart, discussionAnnotation.getStartLine(), discussionAnnotation.getStartColumn(),
						discussionAnnotation.getEndLine(), discussionAnnotation.getEndColumn());
			}
		} else {
			ShelvesetDiscussionItem parentDiscussion = shelvesetDiscussionItem.getParentDiscussion();
			if (shelvesetDiscussionItem != null && parentDiscussion == null) {
				ShelvesetFileItem shelvesetFileItem = shelvesetDiscussionItem.getParentFile();
				if (shelvesetFileItem != null) {
					EditorUtil.showDiscussionDialog(shelvesetDiscussionItem.getParent(), shelvesetFileItem.getPath(),
							shelvesetDiscussionItem.getStartLine(), shelvesetDiscussionItem.getStartColumn(), shelvesetDiscussionItem.getEndLine(),
							shelvesetDiscussionItem.getEndColumn());
				} else {
					EditorUtil.showDiscussionDialog(shelvesetDiscussionItem.getParent(), null, -1, -1, -1, -1);
				}
			}
		}
		return null;
	}
}
