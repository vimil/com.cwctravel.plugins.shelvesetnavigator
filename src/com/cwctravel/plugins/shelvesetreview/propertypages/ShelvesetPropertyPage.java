package com.cwctravel.plugins.shelvesetreview.propertypages;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.dialogs.PropertyPage;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ReviewerInfo;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class ShelvesetPropertyPage extends PropertyPage {
	private ShelvesetItem shelvesetItem;

	private Composite createDefaultComposite(Composite parent) {
		noDefaultAndApplyButton();

		Composite composite = new Composite(parent, SWT.NULL);
		FormLayout layout = new FormLayout();
		composite.setLayout(layout);

		return composite;
	}

	@Override
	protected Control createContents(Composite parent) {
		shelvesetItem = ((IAdaptable) getElement()).getAdapter(ShelvesetItem.class);
		Composite composite = createDefaultComposite(parent);
		addMainSection(composite);

		return composite;
	}

	private void addMainSection(Composite parent) {
		Label shelvesetNameTitleLabel = new Label(parent, SWT.NONE);
		shelvesetNameTitleLabel.setText("Name: ");
		FormData fdShelvesetNameTitleLabel = new FormData(convertWidthInCharsToPixels(20),
				convertHeightInCharsToPixels(1));
		fdShelvesetNameTitleLabel.top = new FormAttachment(null, 3, SWT.BOTTOM);
		fdShelvesetNameTitleLabel.left = new FormAttachment(0, 10);
		shelvesetNameTitleLabel.setLayoutData(fdShelvesetNameTitleLabel);

		Label shelvesetNameLabel = new Label(parent, SWT.NONE);
		shelvesetNameLabel.setText(shelvesetItem.getName());
		FormData fdShelvesetNameLabel = new FormData(convertWidthInCharsToPixels(20), convertHeightInCharsToPixels(1));
		fdShelvesetNameLabel.top = new FormAttachment(null, 3, SWT.BOTTOM);
		fdShelvesetNameLabel.left = new FormAttachment(shelvesetNameTitleLabel, 0, SWT.RIGHT);
		shelvesetNameLabel.setLayoutData(fdShelvesetNameLabel);

		Label shelvesetOwnerNameTitleLabel = new Label(parent, SWT.NONE);
		shelvesetOwnerNameTitleLabel.setText("Owner Name: ");
		FormData fdShelvesetOwnerNameTitleLabel = new FormData(convertWidthInCharsToPixels(20),
				convertHeightInCharsToPixels(1));
		fdShelvesetOwnerNameTitleLabel.top = new FormAttachment(shelvesetNameTitleLabel, 3, SWT.BOTTOM);
		fdShelvesetOwnerNameTitleLabel.left = new FormAttachment(0, 10);
		shelvesetOwnerNameTitleLabel.setLayoutData(fdShelvesetOwnerNameTitleLabel);

		Label shelvesetOwnerNameLabel = new Label(parent, SWT.NONE);
		shelvesetOwnerNameLabel.setText(shelvesetItem.getOwnerName());
		FormData fdShelvesetOwnerNameLabel = new FormData(convertWidthInCharsToPixels(50),
				convertHeightInCharsToPixels(1));
		fdShelvesetOwnerNameLabel.top = new FormAttachment(shelvesetNameLabel, 3, SWT.BOTTOM);
		fdShelvesetOwnerNameLabel.left = new FormAttachment(shelvesetOwnerNameTitleLabel, 0, SWT.RIGHT);
		shelvesetOwnerNameLabel.setLayoutData(fdShelvesetOwnerNameLabel);

		String comment = shelvesetItem.getComment();
		if (comment == null) {
			comment = "<No Comments>";
		}

		Label shelvesetCommentsTitleLabel = new Label(parent, SWT.NONE);
		shelvesetCommentsTitleLabel.setText("Comments: ");
		FormData fdShelvesetCommentsTitleLabel = new FormData(convertWidthInCharsToPixels(20),
				convertHeightInCharsToPixels(1));
		fdShelvesetCommentsTitleLabel.top = new FormAttachment(shelvesetOwnerNameTitleLabel, 3, SWT.BOTTOM);
		fdShelvesetCommentsTitleLabel.left = new FormAttachment(0, 10);
		shelvesetCommentsTitleLabel.setLayoutData(fdShelvesetCommentsTitleLabel);

		Label shelvesetCommentsLabel = new Label(parent, SWT.NONE);

		shelvesetCommentsLabel.setText(comment);
		FormData fdShelvesetCommentsLabel = new FormData(convertWidthInCharsToPixels(50),
				convertHeightInCharsToPixels(1));
		fdShelvesetCommentsLabel.top = new FormAttachment(shelvesetOwnerNameLabel, 3, SWT.BOTTOM);
		fdShelvesetCommentsLabel.left = new FormAttachment(shelvesetCommentsTitleLabel, 0, SWT.RIGHT);
		shelvesetCommentsLabel.setLayoutData(fdShelvesetCommentsLabel);

		String buildId = shelvesetItem.getBuildId();
		if (buildId == null) {
			buildId = "<Shelveset was not built>";
		}

		Label shelvesetBuildIdTitleLabel = new Label(parent, SWT.NONE);
		shelvesetBuildIdTitleLabel.setText("Build Id: ");
		FormData fdShelvesetBuildIdTitleLabel = new FormData(convertWidthInCharsToPixels(20),
				convertHeightInCharsToPixels(1));
		fdShelvesetBuildIdTitleLabel.top = new FormAttachment(shelvesetCommentsTitleLabel, 3, SWT.BOTTOM);
		fdShelvesetBuildIdTitleLabel.left = new FormAttachment(0, 10);
		shelvesetBuildIdTitleLabel.setLayoutData(fdShelvesetBuildIdTitleLabel);

		Label shelvesetBuildIdLabel = new Label(parent, SWT.NONE);
		shelvesetBuildIdLabel.setText(buildId);
		FormData fdShelvesetBuildIdLabel = new FormData(convertWidthInCharsToPixels(50),
				convertHeightInCharsToPixels(1));
		fdShelvesetBuildIdLabel.top = new FormAttachment(shelvesetCommentsLabel, 3, SWT.BOTTOM);
		fdShelvesetBuildIdLabel.left = new FormAttachment(shelvesetBuildIdTitleLabel, 0, SWT.RIGHT);
		shelvesetBuildIdLabel.setLayoutData(fdShelvesetBuildIdLabel);

		String changesetNumber = shelvesetItem.getChangesetNumber();
		if (changesetNumber == null) {
			changesetNumber = "<Shelveset was not checked-in to TFS>";
		}

		Label shelvesetChangesetNumberTitleLabel = new Label(parent, SWT.NONE);
		shelvesetChangesetNumberTitleLabel.setText("Changeset Number: ");
		FormData fdShelvesetChangesetNumberTitleLabel = new FormData(convertWidthInCharsToPixels(20),
				convertHeightInCharsToPixels(1));
		fdShelvesetChangesetNumberTitleLabel.top = new FormAttachment(shelvesetBuildIdTitleLabel, 3, SWT.BOTTOM);
		fdShelvesetChangesetNumberTitleLabel.left = new FormAttachment(0, 10);
		shelvesetChangesetNumberTitleLabel.setLayoutData(fdShelvesetChangesetNumberTitleLabel);

		Label shelvesetChangesetNumberLabel = new Label(parent, SWT.NONE);
		shelvesetChangesetNumberLabel.setText(changesetNumber);
		FormData fdShelvesetChangesetNumberLabel = new FormData(convertWidthInCharsToPixels(50),
				convertHeightInCharsToPixels(1));
		fdShelvesetChangesetNumberLabel.top = new FormAttachment(shelvesetBuildIdLabel, 3, SWT.BOTTOM);
		fdShelvesetChangesetNumberLabel.left = new FormAttachment(shelvesetChangesetNumberTitleLabel, 0, SWT.RIGHT);
		shelvesetChangesetNumberLabel.setLayoutData(fdShelvesetChangesetNumberLabel);

		String shelvesetStatus = shelvesetItem.isInactive() ? "Discarded" : "Active";

		Label shelvesetStatusTitleLabel = new Label(parent, SWT.NONE);
		shelvesetStatusTitleLabel.setText("Status: ");
		FormData fdShelvesetStatusTitleLabel = new FormData(convertWidthInCharsToPixels(20),
				convertHeightInCharsToPixels(1));
		fdShelvesetStatusTitleLabel.top = new FormAttachment(shelvesetChangesetNumberTitleLabel, 3, SWT.BOTTOM);
		fdShelvesetStatusTitleLabel.left = new FormAttachment(0, 10);
		shelvesetStatusTitleLabel.setLayoutData(fdShelvesetStatusTitleLabel);

		Label shelvesetStatusLabel = new Label(parent, SWT.NONE);
		shelvesetStatusLabel.setText(shelvesetStatus);
		FormData fdShelvesetStatusLabel = new FormData(convertWidthInCharsToPixels(50),
				convertHeightInCharsToPixels(1));
		fdShelvesetStatusLabel.top = new FormAttachment(shelvesetChangesetNumberLabel, 3, SWT.BOTTOM);
		fdShelvesetStatusLabel.left = new FormAttachment(shelvesetStatusTitleLabel, 0, SWT.RIGHT);
		shelvesetStatusLabel.setLayoutData(fdShelvesetStatusLabel);

		Label shelvesetInfoSeparator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		FormData fdShelvesetInfoSeparator = new FormData(10, 10);
		fdShelvesetInfoSeparator.left = new FormAttachment(0, 5);
		fdShelvesetInfoSeparator.right = new FormAttachment(100, -5);
		fdShelvesetInfoSeparator.top = new FormAttachment(shelvesetStatusLabel, 10, SWT.BOTTOM);
		shelvesetInfoSeparator.setLayoutData(fdShelvesetInfoSeparator);

		TableViewer reviewersViewer = createReviewersViewer(parent);

		Table reviewersTable = reviewersViewer.getTable();
		FormData fdReviewersTable = new FormData(750, 40);
		fdReviewersTable.top = new FormAttachment(shelvesetInfoSeparator, 3, SWT.BOTTOM);
		fdReviewersTable.left = new FormAttachment(0, 5);
		fdReviewersTable.right = new FormAttachment(100, -5);
		fdReviewersTable.bottom = new FormAttachment(100, -35);
		reviewersTable.setLayoutData(fdReviewersTable);

	}

	private TableViewer createReviewersViewer(Composite parent) {
		TableViewer reviewersViewer = new TableViewer(parent, SWT.FULL_SELECTION);
		ReviewerContentProvider reviewerContentProvider = new ReviewerContentProvider(shelvesetItem.getReviewers());
		reviewersViewer.setContentProvider(reviewerContentProvider);

		final Table reviewersTable = reviewersViewer.getTable();
		reviewersTable.setHeaderVisible(true);
		reviewersTable.setLinesVisible(true);

		TableViewerColumn reviewersViewerColumn1 = new TableViewerColumn(reviewersViewer, SWT.NONE);
		reviewersViewerColumn1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element != null) {
					ReviewerInfo reviewerInfo = (ReviewerInfo) element;
					return reviewerInfo.getReviewerId();
				}
				return null;
			}
		});

		TableColumn reviewersTableColumn1 = reviewersViewerColumn1.getColumn();
		reviewersTableColumn1.setText("Reviewer Id");
		reviewersTableColumn1.setWidth(680);
		reviewersTableColumn1.setResizable(false);

		TableViewerColumn reviewersViewerColumn2 = new TableViewerColumn(reviewersViewer, SWT.NONE);
		reviewersViewerColumn2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element != null) {
					ReviewerInfo reviewerInfo = (ReviewerInfo) element;
					return reviewerInfo.isApproved() ? "Yes" : "No";
				}
				return null;
			}
		});

		TableColumn reviewersTableColumn2 = reviewersViewerColumn2.getColumn();
		reviewersTableColumn2.setText("Approved");
		reviewersTableColumn2.setWidth(70);
		reviewersTableColumn2.setResizable(false);

		reviewersViewer.setInput(reviewerContentProvider.getReviewers());
		return reviewersViewer;
	}

	@Override
	public boolean performOk() {
		return true;
	}
}
