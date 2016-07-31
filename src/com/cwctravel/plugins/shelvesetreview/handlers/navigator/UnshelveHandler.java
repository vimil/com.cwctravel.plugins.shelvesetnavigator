package com.cwctravel.plugins.shelvesetreview.handlers.navigator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.client.common.ui.TFSCommonUIClientPlugin;
import com.microsoft.tfs.client.common.ui.dialogs.vc.ShelvesetDetailsDialog;
import com.microsoft.tfs.client.common.ui.prefs.UIPreferenceConstants;
import com.microsoft.tfs.client.common.ui.tasks.vc.AbstractUnshelveTask;

public class UnshelveHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof TreeSelection) {
			TreeSelection treeSelection = (TreeSelection) selection;
			if (treeSelection.size() == 1) {
				ShelvesetItem shelvesetItem = (ShelvesetItem) treeSelection.getFirstElement();
				shelvesetItem.scheduleRefresh(true);

				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				ShelvesetDetailsDialog detailsDialog = new ShelvesetDetailsDialog(shell, shelvesetItem.getShelveset(), TFSUtil.getRepository(), true);
				detailsDialog.setPreserveShelveset(true);
				detailsDialog.setRestoreData(true);

				if (detailsDialog.open() == IDialogConstants.OK_ID) {
					AbstractUnshelveTask unshelveTask = new AbstractUnshelveTask(shell, TFSUtil.getRepository()) {

						@Override
						public IStatus run() {
							boolean autoResolveConflicts = TFSCommonUIClientPlugin.getDefault().getPreferenceStore()
									.getBoolean(UIPreferenceConstants.AUTO_RESOLVE_CONFLICTS);
							return unshelve(shelvesetItem.getShelveset(), TFSUtil.getItemSpecs(detailsDialog.getCheckedChangeItems()), false,
									detailsDialog.isRestoreData(), autoResolveConflicts);
						}
					};

					unshelveTask.run();
				}
			}
		}
		return null;
	}

}
