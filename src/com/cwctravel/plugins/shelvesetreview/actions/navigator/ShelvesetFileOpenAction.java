package com.cwctravel.plugins.shelvesetreview.actions.navigator;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;

public class ShelvesetFileOpenAction extends Action implements ISelectionChangedListener {
	private TreeViewer treeViewer;
	private ISelectionProvider provider;
	private ShelvesetFileItem shelvesetFileItem;
	private ShelvesetDiscussionItem shelvesetDiscussionItem;

	public ShelvesetFileOpenAction(IWorkbenchPartSite workbenchPartSite, ISelectionProvider provider, TreeViewer treeViewer) {
		setText("Open");
		this.treeViewer = treeViewer;
		this.provider = provider;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
	}

	public boolean isEnabled() {
		boolean result = false;
		ISelection selection = provider.getSelection();
		if (!selection.isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.size() == 1) {
				Object element = structuredSelection.getFirstElement();
				if (element instanceof ShelvesetFileItem) {
					shelvesetFileItem = (ShelvesetFileItem) element;
					result = true;
				} else if (element instanceof ShelvesetDiscussionItem) {
					shelvesetDiscussionItem = (ShelvesetDiscussionItem) element;
					result = true;
				}
			}
		}
		return result;
	}

	public void run() {
		try {
			if (shelvesetDiscussionItem != null) {
				shelvesetFileItem = shelvesetDiscussionItem.getParentFile();
			}
			if (shelvesetFileItem != null) {
				IFileStore fileStore = EFS.getStore(shelvesetFileItem.getURI());
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IEditorPart editorPart = IDE.openEditorOnFileStore(page, fileStore);
				if (shelvesetDiscussionItem != null) {
					if (editorPart instanceof ITextEditor) {
						ITextEditor textEditor = (ITextEditor) editorPart;
						IEditorInput editorInput = editorPart.getEditorInput();
						if (editorInput instanceof FileStoreEditorInput) {
							IDocumentProvider documentProvider = textEditor.getDocumentProvider();
							IDocument document = documentProvider.getDocument(editorInput);
							try {
								int startLine = shelvesetDiscussionItem.getStartLine();
								int startColumn = shelvesetDiscussionItem.getStartColumn();
								int endLine = shelvesetDiscussionItem.getEndLine();
								int endColumn = shelvesetDiscussionItem.getEndColumn();
								if (startLine > 0 && endLine >= startLine && startColumn >= 0 && endColumn >= 0) {
									int startOffset = document.getLineOffset(startLine - 1) + startColumn - 1;

									int endOffset = document.getLineOffset(endLine - 1) + endColumn - 1;
									textEditor.selectAndReveal(startOffset, endOffset - startOffset);
								}
							} catch (BadLocationException e) {
								ShelvesetReviewPlugin.log(IStatus.ERROR, e.getMessage(), e);
							}
						}
					}
				}
			}
		} catch (CoreException e) {
			ShelvesetReviewPlugin.log(IStatus.ERROR, e.getMessage(), e);
		}
	}

}
