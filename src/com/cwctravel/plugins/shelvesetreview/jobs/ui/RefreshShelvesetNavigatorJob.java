package com.cwctravel.plugins.shelvesetreview.jobs.ui;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import com.cwctravel.plugins.shelvesetreview.navigator.ShelvesetNavigator;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class RefreshShelvesetNavigatorJob extends UIJob {
	private ShelvesetItem shelvesetItem;
	private boolean expand;

	public RefreshShelvesetNavigatorJob() {
		super("Refreshing Shelveset Navigator");
	}

	public ShelvesetItem getShelvesetItem() {
		return shelvesetItem;
	}

	public void setShelvesetItem(ShelvesetItem shelvesetItem) {
		this.shelvesetItem = shelvesetItem;
	}

	public boolean isExpand() {
		return expand;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

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
									if (shelvesetItem != null) {
										if (expand) {
											shelvesetNavigator.getCommonViewer().expandToLevel(shelvesetItem, 1);
										} else {
											shelvesetNavigator.getCommonViewer().refresh(shelvesetItem, true);
										}
									} else {
										shelvesetNavigator.getCommonViewer().refresh(true);
									}
								}
							}
						}
					}
				}
			}
		}
		return Status.OK_STATUS;
	}
}