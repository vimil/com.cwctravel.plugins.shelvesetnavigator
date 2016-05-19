package com.cwctravel.plugins.shelvesetnavigator.jobs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import com.cwctravel.plugins.shelvesetnavigator.ShelvesetNavigator;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetnavigator.util.ShelvesetUtil;
import com.cwctravel.plugins.shelvesetnavigator.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.VersionControlClient;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.ItemType;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingChange;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.PendingSet;

public class ShelvesetFileItemsRefreshJob extends Job {
	private ShelvesetItem shelvesetItem;
	private boolean expand;

	public ShelvesetFileItemsRefreshJob(ShelvesetItem shelvesetItem, boolean expand) {
		super("Updating Shelveset");
		this.shelvesetItem = shelvesetItem;
		this.expand = expand;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Updating Shelveset", 0);
		List<ShelvesetFileItem> shelvesetFileItems = new ArrayList<ShelvesetFileItem>();

		VersionControlClient vC = TFSUtil.getVersionControlClient();
		PendingSet[] pendingSets = vC.queryShelvedChanges(this.shelvesetItem.getName(),
				this.shelvesetItem.getOwnerName(), null, true, null);

		if (pendingSets != null) {
			for (PendingSet pendingSet : pendingSets) {
				PendingChange[] pendingChanges = pendingSet.getPendingChanges();
				if (pendingChanges != null) {
					for (PendingChange pendingChange : pendingChanges) {
						ItemType itemType = pendingChange.getItemType();
						if (itemType == ItemType.FILE) {
							ShelvesetFileItem shelvesetResourceItem = new ShelvesetFileItem(this.shelvesetItem,
									pendingSet, pendingChange);
							shelvesetFileItems.add(shelvesetResourceItem);
						}

					}
				}
			}
		}

		shelvesetItem.setChildren(ShelvesetUtil.groupShelvesetFileItems(this.shelvesetItem, shelvesetFileItems));

		monitor.done();

		new UIJob("Refreshing Shelveset") {
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
											if (expand) {
												shelvesetNavigator.getCommonViewer().expandToLevel(
														ShelvesetFileItemsRefreshJob.this.shelvesetItem, 1);
											} else {
												shelvesetNavigator.getCommonViewer()
														.refresh(ShelvesetFileItemsRefreshJob.this.shelvesetItem, true);
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

		}.schedule();
		this.shelvesetItem.setChildrenRefreshed(true);
		return Status.OK_STATUS;
	}
}