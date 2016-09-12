package com.cwctravel.plugins.shelvesetreview.navigator.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;

public class BaseItemContainer extends PlatformObject {
	private final ShelvesetGroupItemContainer shelvesetGroupItemContainer;
	private final CodeReviewItemContainer codeReviewItemContainer;

	public BaseItemContainer() {
		shelvesetGroupItemContainer = new ShelvesetGroupItemContainer();
		codeReviewItemContainer = new CodeReviewItemContainer();
	}

	public ShelvesetGroupItemContainer getShelvesetGroupItemContainer() {
		return shelvesetGroupItemContainer;
	}

	public CodeReviewItemContainer getCodeReviewItemContainer() {
		return codeReviewItemContainer;
	}

	public void refresh(boolean softRefresh, IProgressMonitor monitor) {
		shelvesetGroupItemContainer.refresh(softRefresh, monitor);
		codeReviewItemContainer.refresh(shelvesetGroupItemContainer.getUserShelvesetItemsMap(), monitor);

	}

}
