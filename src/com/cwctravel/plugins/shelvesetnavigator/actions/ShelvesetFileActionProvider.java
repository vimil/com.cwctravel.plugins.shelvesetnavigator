package com.cwctravel.plugins.shelvesetnavigator.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

public class ShelvesetFileActionProvider extends CommonActionProvider {
	private ShelvesetFileOpenAction openAction;
	private ShelvesetFileCompareWithLatestAction compareWithLatestAction;

	public void init(ICommonActionExtensionSite aSite) {
		ICommonViewerSite viewSite = aSite.getViewSite();
		if (viewSite instanceof ICommonViewerWorkbenchSite) {
			ICommonViewerWorkbenchSite workbenchSite = (ICommonViewerWorkbenchSite) viewSite;
			if ((aSite.getStructuredViewer() instanceof TreeViewer)) {
				openAction = new ShelvesetFileOpenAction(workbenchSite.getSite(), workbenchSite.getSelectionProvider(),
						(TreeViewer) aSite.getStructuredViewer());
				compareWithLatestAction = new ShelvesetFileCompareWithLatestAction(workbenchSite.getSite(),
						workbenchSite.getSelectionProvider(), (TreeViewer) aSite.getStructuredViewer());
			}
		}
	}

	public void fillActionBars(IActionBars actionBars) {
		if (openAction.isEnabled()) {
			actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, openAction);
		}
	}

	public void fillContextMenu(IMenuManager menu) {
		if (openAction.isEnabled()) {
			menu.appendToGroup(ICommonMenuConstants.GROUP_OPEN, openAction);
		}

		if (compareWithLatestAction.isEnabled()) {
			menu.appendToGroup(ICommonMenuConstants.GROUP_ADDITIONS, compareWithLatestAction);
		}
	}
}
