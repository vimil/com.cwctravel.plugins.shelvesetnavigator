package com.cwctravel.plugins.shelvesetreview.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.cwctravel.plugins.shelvesetreview.contentProviders.ReviewerContentProvider;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ReviewerInfo;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;

public class AssignReviewersDialog extends TitleAreaDialog {
	private final class ErrorMessageClearer implements FocusListener {
		@Override
		public void focusLost(FocusEvent e) {
			setErrorMessage(null);
		}

		@Override
		public void focusGained(FocusEvent e) {
		}
	}

	private long lastUserIdCheckTime;

	private ShelvesetItem shelvesetItem;

	private Text txtReviewerId;

	private List<ReviewerInfo> reviewers;

	private TableViewer reviewersViewer;

	public AssignReviewersDialog(ShelvesetItem shelvesetItem, Shell parentShell) {
		super(parentShell);
		this.shelvesetItem = shelvesetItem;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Assign Reviewers");
		setMessage("Assign Reviewers for Shelveset " + shelvesetItem.getName(), IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		FormLayout layout = new FormLayout();
		container.setLayout(layout);

		addMainSection(container);

		return area;
	}

	private void addMainSection(Composite container) {
		Label lblReviewerId = new Label(container, SWT.NONE);
		lblReviewerId.setText("Reviewer Id:");

		txtReviewerId = new Text(container, SWT.BORDER);
		Button btnReviewerIdAdd = new Button(container, SWT.PUSH);

		FormData fdReviewerIdTitle = new FormData(convertWidthInCharsToPixels(15), convertHeightInCharsToPixels(1));
		fdReviewerIdTitle.top = new FormAttachment(txtReviewerId, 0, SWT.CENTER);
		fdReviewerIdTitle.left = new FormAttachment(0, 10);
		lblReviewerId.setLayoutData(fdReviewerIdTitle);

		FormData fdReviewerId = new FormData(convertWidthInCharsToPixels(60), convertHeightInCharsToPixels(1));
		fdReviewerId.top = new FormAttachment(null, 5, SWT.BOTTOM);
		fdReviewerId.left = new FormAttachment(lblReviewerId, 0, SWT.RIGHT);
		fdReviewerId.right = new FormAttachment(btnReviewerIdAdd, -3, SWT.LEFT);

		txtReviewerId.setLayoutData(fdReviewerId);

		txtReviewerId.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastUserIdCheckTime > 1000) {
					validateUserId(txtReviewerId.getText());
					lastUserIdCheckTime = currentTime;
				}
			}
		});

		txtReviewerId.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				validateUserId(txtReviewerId.getText());

			}

			@Override
			public void focusGained(FocusEvent e) {
				validateUserId(txtReviewerId.getText());
			}
		});

		btnReviewerIdAdd.setText("Add");
		FormData fdReviewerAdd = new FormData(convertWidthInCharsToPixels(15), 25);
		fdReviewerAdd.right = new FormAttachment(100, -5);
		fdReviewerAdd.top = new FormAttachment(txtReviewerId, 0, SWT.CENTER);
		btnReviewerIdAdd.setLayoutData(fdReviewerAdd);
		btnReviewerIdAdd.setEnabled(true);

		btnReviewerIdAdd.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				String reviewerId = txtReviewerId.getText();
				if (validateUserId(reviewerId)) {
					ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer
							.getContentProvider();
					reviewerContentProvider.addReviewer(reviewerId);
					reviewersViewer.refresh();
				}
			}
		});

		ErrorMessageClearer errorMessageClearer = new ErrorMessageClearer();
		btnReviewerIdAdd.addFocusListener(errorMessageClearer);

		Label reviewerInfoSeparator = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		FormData fdReviewerInfoSeparator = new FormData(10, 10);
		fdReviewerInfoSeparator.left = new FormAttachment(0, 5);
		fdReviewerInfoSeparator.right = new FormAttachment(100, -5);
		fdReviewerInfoSeparator.top = new FormAttachment(btnReviewerIdAdd, 10, SWT.BOTTOM);
		reviewerInfoSeparator.setLayoutData(fdReviewerInfoSeparator);

		reviewersViewer = createReviewersViewer(container);

		Table reviewersTable = reviewersViewer.getTable();
		FormData fdReviewersTable = new FormData(750, 90);
		fdReviewersTable.top = new FormAttachment(reviewerInfoSeparator, 3, SWT.BOTTOM);
		fdReviewersTable.left = new FormAttachment(0, 5);
		fdReviewersTable.right = new FormAttachment(100, -5);
		fdReviewersTable.bottom = new FormAttachment(100, -35);
		reviewersTable.setLayoutData(fdReviewersTable);

		Button btnReviewerIdRemove = new Button(container, SWT.PUSH);
		btnReviewerIdRemove.setText("Remove");
		FormData fdReviewerRemove = new FormData(convertWidthInCharsToPixels(15), 25);
		fdReviewerRemove.right = new FormAttachment(100, -5);
		fdReviewerRemove.top = new FormAttachment(reviewersTable, 10, SWT.BOTTOM);
		btnReviewerIdRemove.setLayoutData(fdReviewerRemove);
		btnReviewerIdRemove.setEnabled(false);

		btnReviewerIdRemove.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				List<Integer> checkedRowIndices = getCheckedItemIndices(reviewersTable);
				if (checkedRowIndices != null && checkedRowIndices.size() > 0
						&& validateReviewersToRemove(checkedRowIndices)) {
					ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer
							.getContentProvider();
					reviewerContentProvider.removeElementsAt(checkedRowIndices);
					reviewersViewer.refresh();
				}
			}
		});

		btnReviewerIdRemove.addFocusListener(errorMessageClearer);

		Listener reviewersTableOnSelectEventListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				btnReviewerIdRemove.setEnabled(isAnyItemChecked(reviewersTable));
			}
		};

		reviewersTable.addListener(SWT.Selection, reviewersTableOnSelectEventListener);
	}

	private TableViewer createReviewersViewer(Composite parent) {
		TableViewer reviewersViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.CHECK);
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
		reviewersTableColumn1.setWidth(630);
		reviewersTableColumn1.setResizable(false);

		TableViewerColumn reviewersViewerColumn2 = new TableViewerColumn(reviewersViewer, SWT.NONE);
		reviewersViewerColumn2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element != null) {
					ReviewerInfo reviewerInfo = (ReviewerInfo) element;
					return reviewerInfo.isApproved() ? "Approved" : "Pending Approval";
				}
				return null;
			}
		});

		TableColumn reviewersTableColumn2 = reviewersViewerColumn2.getColumn();
		reviewersTableColumn2.setText("Approval Status");
		reviewersTableColumn2.setWidth(120);
		reviewersTableColumn2.setResizable(false);

		reviewersViewer.setInput(reviewerContentProvider.getReviewers());
		return reviewersViewer;
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

	private void saveInput() {
		ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer
				.getContentProvider();
		reviewers = reviewerContentProvider.getReviewers();

	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public List<ReviewerInfo> getReviewers() {
		return reviewers;
	}

	private boolean validateUserId(String reviewerId) {
		boolean result = false;
		if (TFSUtil.findUserId(reviewerId) != null) {
			if (!TFSUtil.userIdsSame(reviewerId, TFSUtil.getCurrentUserId())) {
				ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer
						.getContentProvider();
				if (!reviewerContentProvider.reviewerIdExists(reviewerId)) {
					result = true;
					setErrorMessage(null);
				} else {
					setErrorMessage("Reviewer has already been assigned");
				}
			} else {
				setErrorMessage("Cannot assign yourself as a reviewer");
			}
		} else {
			setErrorMessage("Not a valid TFS user: " + reviewerId);
		}
		return result;
	}

	private boolean validateReviewersToRemove(List<Integer> selectedRowIndices) {
		boolean result = false;
		if (selectedRowIndices != null && selectedRowIndices.size() > 0) {
			result = true;
			for (int selectedRowIndex : selectedRowIndices) {
				ReviewerInfo reviewerInfo = (ReviewerInfo) reviewersViewer.getElementAt(selectedRowIndex);
				if (reviewerInfo.getSource() != ReviewerInfo.SOURCE_SHELVESET) {
					setErrorMessage("The reviewer cannot be removed: " + reviewerInfo.getReviewerId());
					result = false;
					break;
				}
			}
		}

		return result;
	}

	private boolean isAnyItemChecked(final Table table) {
		boolean isAnyItemChecked = false;
		TableItem[] items = table.getItems();
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].getChecked()) {
					isAnyItemChecked = true;
					break;
				}
			}
		}
		return isAnyItemChecked;
	}

	private List<Integer> getCheckedItemIndices(final Table table) {
		List<Integer> result = new ArrayList<Integer>();
		TableItem[] items = table.getItems();
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].getChecked()) {
					result.add(i);
				}
			}
		}
		return result;
	}
}
