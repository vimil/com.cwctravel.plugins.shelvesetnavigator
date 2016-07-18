package com.cwctravel.plugins.shelvesetreview.handlers.navigator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.cwctravel.plugins.shelvesetreview.dialogs.DiscussionCommentDialog;
import com.cwctravel.plugins.shelvesetreview.exceptions.ApproveException;
import com.cwctravel.plugins.shelvesetreview.jobs.ui.RefreshShelvesetNavigatorJob;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.EditorUtil;

public class UnapproveShelvesetHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			if (treeSelection.size() == 1) {
				ShelvesetItem shelvesetItem = (ShelvesetItem) treeSelection.getFirstElement();
				try {
					String revokeApprovalComment = "Approval was revoked for Shelveset " + shelvesetItem.getName();
					if (shelvesetItem.getWorkItemContainer() != null) {
						DiscussionCommentDialog discussionCommentDialog = EditorUtil.showDiscussionCommentDialog("Revoke Approval Comment",
								revokeApprovalComment);
						if (discussionCommentDialog.getReturnCode() == Dialog.OK) {
							revokeApprovalComment = discussionCommentDialog.getComment();
							shelvesetItem.unapprove(revokeApprovalComment);
						}
					} else {
						shelvesetItem.unapprove(revokeApprovalComment);
					}
					RefreshShelvesetNavigatorJob refreshShelvesetNavigatorJob = new RefreshShelvesetNavigatorJob();
					refreshShelvesetNavigatorJob.setShelvesetItem(shelvesetItem);
					refreshShelvesetNavigatorJob.schedule();
				} catch (ApproveException aE) {
					Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
					MessageBox dialog = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					dialog.setText("Error Unapproving Shelveset");
					dialog.setMessage(aE.getMessage());
				}
			}
		}

		return null;
	}

}
