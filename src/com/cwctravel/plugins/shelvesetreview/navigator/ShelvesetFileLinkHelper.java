package com.cwctravel.plugins.shelvesetreview.navigator;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.navigator.ILinkHelper;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.filesystem.TFSFileStore;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetResourceItem;

public class ShelvesetFileLinkHelper implements ILinkHelper {

	@Override
	public IStructuredSelection findSelection(IEditorInput editorInput) {
		IStructuredSelection result = null;
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
					List<Object> treePathSgementsList = new ArrayList<Object>();
					ShelvesetResourceItem current = shelvesetFileItem;
					while (current != null) {
						treePathSgementsList.add(0, current);
						current = current.getParentFolder();
					}
					treePathSgementsList.add(0, shelvesetItem);
					result = new TreeSelection(new TreePath(treePathSgementsList.toArray(new Object[0])));
				}

			}
		} catch (CoreException e) {
			ShelvesetReviewPlugin.log(IStatus.ERROR, e.getMessage(), e);
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

			}
		}
	}

}
