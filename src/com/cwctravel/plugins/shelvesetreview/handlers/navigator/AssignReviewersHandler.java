package com.cwctravel.plugins.shelvesetreview.handlers.navigator;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.cwctravel.plugins.shelvesetreview.dialogs.AssignReviewersDialog;
import com.cwctravel.plugins.shelvesetreview.jobs.AssignShelvesetReviewersJob;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ReviewerInfo;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class AssignReviewersHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			if (treeSelection.size() == 1) {
				ShelvesetItem shelvesetItem = (ShelvesetItem) treeSelection.getFirstElement();
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				AssignReviewersDialog assignReviewersDialog = new AssignReviewersDialog(shelvesetItem, shell);
				assignReviewersDialog.create();
				if (assignReviewersDialog.open() == Window.OK) {
					List<ReviewerInfo> reviewers = assignReviewersDialog.getReviewers();
					new AssignShelvesetReviewersJob(shelvesetItem, reviewers).schedule();
				}
			}
		}

		return null;
	}

}
