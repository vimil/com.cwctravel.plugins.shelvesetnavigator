package com.cwctravel.plugins.shelvesetreview.navigator.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.PlatformObject;

public class CodeReviewItemContainer extends PlatformObject {
	private final List<CodeReviewGroupItem> codeReviewGroupItems;

	public CodeReviewItemContainer() {
		codeReviewGroupItems = new ArrayList<>();
		CodeReviewGroupItem userCodeReviewGroupItem = new CodeReviewGroupItem(this, CodeReviewGroupItem.GROUP_TYPE_CURRENT_USER_CODEREVIEWS);
		CodeReviewGroupItem openCodeReviewGroupItem = new CodeReviewGroupItem(this, CodeReviewGroupItem.GROUP_TYPE_OPEN_CODEREVIEWS);
		CodeReviewGroupItem acceptedCodeReviewGroupItem = new CodeReviewGroupItem(this, CodeReviewGroupItem.GROUP_TYPE_ACCEPTED_CODEREVIEWS);

		codeReviewGroupItems.add(userCodeReviewGroupItem);
		codeReviewGroupItems.add(openCodeReviewGroupItem);
		codeReviewGroupItems.add(acceptedCodeReviewGroupItem);
	}
}
