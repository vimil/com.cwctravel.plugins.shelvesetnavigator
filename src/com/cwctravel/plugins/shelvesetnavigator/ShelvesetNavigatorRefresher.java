package com.cwctravel.plugins.shelvesetnavigator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import com.microsoft.tfs.client.common.repository.RepositoryManagerEvent;
import com.microsoft.tfs.client.common.repository.RepositoryManagerListener;

final class ShelvesetNavigatorRefresher implements RepositoryManagerListener {
	@Override
	public void onRepositoryRemoved(RepositoryManagerEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRepositoryAdded(RepositoryManagerEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDefaultRepositoryChanged(RepositoryManagerEvent event) {
		ShelvesetNavigatorPlugin.getDefault().refreshShelvesetItems();
		UIJob job = new UIJob("Refreshing Shelvesets") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				IWorkbenchWindow[] workbenchWIndows = PlatformUI.getWorkbench().getWorkbenchWindows();
				if (workbenchWIndows != null) {
					for (IWorkbenchWindow workbenchWIndow : workbenchWIndows) {
						IWorkbenchPage[] workbenchPages = workbenchWIndow.getPages();
						if (workbenchPages != null) {
							for (IWorkbenchPage workbenchPage : workbenchPages) {
								IViewReference[] viewReferences = workbenchPage.getViewReferences();
								if (viewReferences != null) {
									for (IViewReference viewReference : viewReferences) {
										IViewPart viewPart = viewReference.getView(false);
										if (viewPart instanceof ShelvesetNavigator) {
											ShelvesetNavigator shelvesetNavigator = (ShelvesetNavigator) viewPart;
											shelvesetNavigator.getCommonViewer().refresh(true);
										}
									}
								}
							}
						}
					}
				}
				return Status.OK_STATUS;
			}
		};

		job.schedule();

	}
}