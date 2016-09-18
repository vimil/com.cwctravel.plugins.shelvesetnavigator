package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.progress.UIJob;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;

public class BaseItemContainer extends PlatformObject implements IItemContainer<Object, Object> {
	private final ShelvesetGroupItemContainer shelvesetGroupItemContainer;
	private final CodeReviewGroupItemContainer codeReviewGroupItemContainer;

	public BaseItemContainer() {
		shelvesetGroupItemContainer = new ShelvesetGroupItemContainer();
		codeReviewGroupItemContainer = new CodeReviewGroupItemContainer();
	}

	public ShelvesetGroupItemContainer getShelvesetGroupItemContainer() {
		return shelvesetGroupItemContainer;
	}

	public CodeReviewGroupItemContainer getCodeReviewGroupItemContainer() {
		return codeReviewGroupItemContainer;
	}

	public void refresh(boolean softRefresh, IProgressMonitor monitor) {
		shelvesetGroupItemContainer.refresh(softRefresh, monitor);
		codeReviewGroupItemContainer.refresh(shelvesetGroupItemContainer.getUserShelvesetItemsMap(), monitor);

		new UIJob("Shelveset Container Refresh") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				ShelvesetReviewPlugin.getDefault().fireShelvesetContainerRefreshed();
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	@Override
	public List<Object> getChildren() {
		List<Object> result = new ArrayList<Object>();
		ShelvesetGroupItemContainer shelvesetGroupItemContainer = getShelvesetGroupItemContainer();
		List<ShelvesetGroupItem> shelvesetGroupItems = shelvesetGroupItemContainer.getShelvesetGroupItems();
		result.addAll(shelvesetGroupItems);
		result.add(getCodeReviewGroupItemContainer());
		return result;
	}

	@Override
	public Object getItemParent() {
		return null;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public int itemCompareTo(IItemContainer<?, ?> itemContainer) {
		return 0;
	}

}
