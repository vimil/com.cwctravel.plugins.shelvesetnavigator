package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetreview.util.IconManager;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.Shelveset;

public class CodeReviewGroupItemContainer extends PlatformObject implements IItemContainer<Object, CodeReviewGroupItem> {
	private final List<CodeReviewGroupItem> codeReviewGroupItems;

	public CodeReviewGroupItemContainer() {
		codeReviewGroupItems = new ArrayList<>();
		CodeReviewGroupItem userCodeReviewGroupItem = new CodeReviewGroupItem(this, CodeReviewGroupItem.GROUP_TYPE_CURRENT_USER_CODEREVIEWS);
		CodeReviewGroupItem openCodeReviewGroupItem = new CodeReviewGroupItem(this, CodeReviewGroupItem.GROUP_TYPE_OPEN_CODEREVIEWS);
		CodeReviewGroupItem acceptedCodeReviewGroupItem = new CodeReviewGroupItem(this, CodeReviewGroupItem.GROUP_TYPE_ACCEPTED_CODEREVIEWS);

		codeReviewGroupItems.add(userCodeReviewGroupItem);
		codeReviewGroupItems.add(openCodeReviewGroupItem);
		codeReviewGroupItems.add(acceptedCodeReviewGroupItem);
	}

	public List<CodeReviewGroupItem> getCodeReviewGroupItems() {
		return codeReviewGroupItems;
	}

	public void refresh(Map<String, List<Shelveset>> userShelvesetItemsMap, IProgressMonitor monitor) {

	}

	@Override
	public Object getItemParent() {
		return null;
	}

	@Override
	public List<CodeReviewGroupItem> getChildren() {
		return getCodeReviewGroupItems();
	}

	@Override
	public Image getImage() {
		Image image = IconManager.getIcon(IconManager.CODEREVIEW_ICON_ID);
		return image;
	}

	public String getText() {
		return "Code Reviews";
	}

	@Override
	public int itemCompareTo(IItemContainer<?, ?> itemContainer) {
		if (itemContainer instanceof ShelvesetGroupItem) {
			return 1;
		}
		return 0;
	}
}
