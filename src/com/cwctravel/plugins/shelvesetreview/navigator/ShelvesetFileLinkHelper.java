package com.cwctravel.plugins.shelvesetreview.navigator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.navigator.ILinkHelper;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.filesystem.TFSFileStore;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetResourceItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetWorkItem;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.client.common.ui.wit.form.WorkItemEditorInput;

public class ShelvesetFileLinkHelper implements ILinkHelper {

	@Override
	public IStructuredSelection findSelection(IEditorInput editorInput) {
		IStructuredSelection result = null;
		if (editorInput instanceof IURIEditorInput) {
			IURIEditorInput uriEditorInput = (IURIEditorInput) editorInput;
			URI uri = uriEditorInput.getURI();
			try {
				IFileStore fileStore = EFS.getStore(uri);
				if (fileStore instanceof TFSFileStore) {
					TFSFileStore tfsFileStore = (TFSFileStore) fileStore;

					ShelvesetGroupItemContainer shelvesetGroupItemContainer = ShelvesetReviewPlugin.getDefault().getShelvesetGroupItemContainer();

					ShelvesetItem shelvesetItem = shelvesetGroupItemContainer.findShelvesetItem(tfsFileStore.getShelvesetName(),
							tfsFileStore.getShelvesetOwnerName());
					if (shelvesetItem != null) {
						ShelvesetFileItem shelvesetFileItem = shelvesetItem.findFile(tfsFileStore.getPath());

						List<Object> treePathSegmentsList = new ArrayList<Object>();
						ShelvesetResourceItem current = shelvesetFileItem;
						while (current != null) {
							treePathSegmentsList.add(0, current);
							current = current.getParentFolder();
						}
						treePathSegmentsList.add(0, shelvesetItem);

						if (shelvesetFileItem != null) {
							IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
							if (editorPart.getEditorInput() == editorInput && editorPart instanceof ITextEditor) {
								ITextEditor textEditor = (ITextEditor) editorPart;
								IDocument document = textEditor.getDocumentProvider().getDocument(uriEditorInput);
								if (document != null) {
									TextSelection selection = (TextSelection) textEditor.getSelectionProvider().getSelection();
									try {
										int startLine = selection.getStartLine();
										int startCol = selection.getOffset() - document.getLineOffset(startLine);
										int endLine = selection.getEndLine();
										int endCol = selection.getOffset() + selection.getLength() - document.getLineOffset(endLine);
										ShelvesetDiscussionItem shelvesetDiscussionItem = shelvesetFileItem.findDiscussionItem(startLine + 1,
												startCol + 1, endLine + 1, endCol + 1);
										if (shelvesetDiscussionItem != null) {
											treePathSegmentsList.add(shelvesetDiscussionItem);
										}
									} catch (BadLocationException e) {
									}
								}
							}
						}

						result = new TreeSelection(new TreePath(treePathSegmentsList.toArray(new Object[0])));
					}

				}
			} catch (CoreException e) {
				ShelvesetReviewPlugin.log(IStatus.ERROR, e.getMessage(), e);
			}
		} else if (editorInput instanceof WorkItemEditorInput) {

		}
		return result;
	}

	@Override
	public void activateEditor(IWorkbenchPage page, IStructuredSelection selection) {
		if (selection.size() == 1) {
			Object firstElement = selection.getFirstElement();
			if (firstElement instanceof ShelvesetFileItem) {
				ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) firstElement;
				try {
					FileStoreEditorInput fileStoreEditorInput = new FileStoreEditorInput(EFS.getStore(shelvesetFileItem.getURI()));
					IEditorPart editor = page.findEditor(fileStoreEditorInput);
					if (editor != null) {
						page.bringToTop(editor);
					}
				} catch (CoreException e) {
					ShelvesetReviewPlugin.log(IStatus.ERROR, e.getMessage(), e);
				}

			} else if (firstElement instanceof ShelvesetWorkItem) {
				ShelvesetWorkItem shelvesetWorkItem = (ShelvesetWorkItem) firstElement;
				WorkItemEditorInput workItemEditorInput = new WorkItemEditorInput(TFSUtil.getTFSServer(), shelvesetWorkItem.getWorkItem());
				IEditorPart editor = page.findEditor(workItemEditorInput);
				if (editor != null) {
					page.bringToTop(editor);
				}
			}
		}
	}

}
