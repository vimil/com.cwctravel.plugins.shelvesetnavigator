package com.cwctravel.plugins.shelvesetreview.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.cwctravel.plugins.shelvesetreview.asynch.RepeatingJob;
import com.cwctravel.plugins.shelvesetreview.contentProviders.ReviewerContentProvider;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ReviewerInfo;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetWorkItem;
import com.cwctravel.plugins.shelvesetreview.util.IdentityUtil;
import com.microsoft.tfs.core.clients.webservices.TeamFoundationIdentity;

public class RequestCodeReviewDialog extends Dialog {

	private final class ErrorMessageClearer implements FocusListener {
		@Override
		public void focusLost(FocusEvent e) {
			restoreDefaultMessage();
		}

		@Override
		public void focusGained(FocusEvent e) {
		}
	}

	private ShelvesetItem shelvesetItem;

	private ShelvesetWorkItem selectedWorkItem;

	private Text txtReviewerId;

	private ComboViewer workItemsComboViewer;

	private List<ReviewerInfo> reviewers;

	private TableViewer reviewersViewer;

	private Text messageText;

	private Label messageImageLabel;

	public RequestCodeReviewDialog(ShelvesetItem shelvesetItem, Shell parentShell) {
		super(parentShell);
		this.shelvesetItem = shelvesetItem;
	}

	@Override
	public void create() {
		super.create();
		restoreDefaultMessage();
	}

	private void restoreDefaultMessage() {
		setMessage("Create CodeReview Request for Shelveset " + shelvesetItem.getName(), IMessageProvider.INFORMATION);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Create CodeReview Request");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite messageComposite = new Composite(area, SWT.NONE);
		messageComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout messageLayout = new GridLayout();
		messageLayout.numColumns = 2;
		messageLayout.marginWidth = 0;
		messageLayout.marginHeight = 0;
		messageLayout.makeColumnsEqualWidth = false;
		messageComposite.setLayout(messageLayout);
		messageImageLabel = new Label(messageComposite, SWT.NONE);
		messageImageLabel.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO));
		messageImageLabel.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_CENTER));

		messageText = new Text(messageComposite, SWT.NONE);
		messageText.setEditable(false);

		GridData textData = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		messageText.setLayoutData(textData);

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
		FormData fdReviewerIdTitle = new FormData(convertWidthInCharsToPixels(15), convertHeightInCharsToPixels(1));
		fdReviewerIdTitle.top = new FormAttachment(txtReviewerId, 0, SWT.CENTER);
		fdReviewerIdTitle.left = new FormAttachment(0, 10);
		lblReviewerId.setLayoutData(fdReviewerIdTitle);

		Button btnReviewerIdAdd = new Button(container, SWT.PUSH);
		FormData fdReviewerId = new FormData(convertWidthInCharsToPixels(60), convertHeightInCharsToPixels(1));
		fdReviewerId.top = new FormAttachment(null, 5, SWT.BOTTOM);
		fdReviewerId.left = new FormAttachment(lblReviewerId, 0, SWT.RIGHT);
		fdReviewerId.right = new FormAttachment(btnReviewerIdAdd, -3, SWT.LEFT);

		txtReviewerId.setLayoutData(fdReviewerId);

		txtReviewerId.addModifyListener(new ModifyListener() {
			RepeatingJob userIdValidatingJob = new RepeatingJob();

			@Override
			public void modifyText(ModifyEvent e) {
				String text = txtReviewerId.getText();
				userIdValidatingJob.schedule(() -> {
					validateUserId(text, false);
				});
			}
		});

		txtReviewerId.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				validateUserId(txtReviewerId.getText(), false);

			}

			@Override
			public void focusGained(FocusEvent e) {
				validateUserId(txtReviewerId.getText(), false);
			}
		});

		btnReviewerIdAdd.setText("Add");
		FormData fdReviewerAdd = new FormData(convertWidthInCharsToPixels(15), 25);
		fdReviewerAdd.right = new FormAttachment(100, -5);
		fdReviewerAdd.top = new FormAttachment(txtReviewerId, 0, SWT.CENTER);
		btnReviewerIdAdd.setLayoutData(fdReviewerAdd);
		btnReviewerIdAdd.setEnabled(true);

		ErrorMessageClearer errorMessageClearer = new ErrorMessageClearer();
		btnReviewerIdAdd.addFocusListener(errorMessageClearer);

		Label lblWorkItemsComboTitle = new Label(container, SWT.NONE);
		lblWorkItemsComboTitle.setText("Select Work Item:");

		workItemsComboViewer = new ComboViewer(container, SWT.READ_ONLY);

		FormData fdWorkItemsComboTitle = new FormData(convertWidthInCharsToPixels(18), convertHeightInCharsToPixels(1));
		Combo workItemsCombo = workItemsComboViewer.getCombo();
		fdWorkItemsComboTitle.top = new FormAttachment(workItemsCombo, 0, SWT.CENTER);
		fdWorkItemsComboTitle.left = new FormAttachment(0, 10);
		lblWorkItemsComboTitle.setLayoutData(fdWorkItemsComboTitle);

		FormData fdWorkItemsCombo = new FormData(convertWidthInCharsToPixels(60), convertHeightInCharsToPixels(1));
		fdWorkItemsCombo.top = new FormAttachment(btnReviewerIdAdd, 10, SWT.BOTTOM);
		fdWorkItemsCombo.left = new FormAttachment(lblWorkItemsComboTitle, 0, SWT.RIGHT);
		fdWorkItemsCombo.right = new FormAttachment(100, -5);
		workItemsCombo.setLayoutData(fdWorkItemsCombo);

		populateWorkItemsCombo();

		Label reviewerInfoSeparator = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		FormData fdReviewerInfoSeparator = new FormData(10, 10);
		fdReviewerInfoSeparator.left = new FormAttachment(0, 5);
		fdReviewerInfoSeparator.right = new FormAttachment(100, -5);
		fdReviewerInfoSeparator.top = new FormAttachment(workItemsCombo, 10, SWT.BOTTOM);
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

		btnReviewerIdRemove.addFocusListener(errorMessageClearer);

		Listener reviewersTableOnSelectEventListener = new Listener() {

			@Override
			public void handleEvent(Event event) {
				btnReviewerIdRemove.setEnabled(isAnyItemChecked(reviewersTable));
			}
		};

		Button btnAddDefaultReviewerGroup = new Button(container, SWT.PUSH);
		btnAddDefaultReviewerGroup.setText("Add Default Reviewer Group");
		FormData fdAddDefaultReviewerGroup = new FormData(convertWidthInCharsToPixels(30), 25);
		fdAddDefaultReviewerGroup.right = new FormAttachment(btnReviewerIdRemove, -5);
		fdAddDefaultReviewerGroup.top = new FormAttachment(reviewersTable, 10, SWT.BOTTOM);
		btnAddDefaultReviewerGroup.setLayoutData(fdAddDefaultReviewerGroup);
		btnAddDefaultReviewerGroup.setEnabled(isDefaultReviewerGroupPresent() && !isDefaultReviewerGroupAssigned());

		btnAddDefaultReviewerGroup.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TeamFoundationIdentity defaultReviewersGroup = IdentityUtil.getDefaultReviewersGroup();
				if (defaultReviewersGroup != null) {
					ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer.getContentProvider();

					reviewerContentProvider.addReviewer(defaultReviewersGroup.getUniqueName());
					reviewersViewer.refresh();
					btnAddDefaultReviewerGroup.setEnabled(isDefaultReviewerGroupPresent() && !isDefaultReviewerGroupAssigned());
				}
			}
		});

		btnReviewerIdAdd.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				String reviewerId = txtReviewerId.getText();
				if (validateUserId(reviewerId, true)) {
					ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer.getContentProvider();
					reviewerContentProvider.addReviewer(reviewerId);
					reviewersViewer.refresh();
					btnAddDefaultReviewerGroup.setEnabled(isDefaultReviewerGroupPresent() && !isDefaultReviewerGroupAssigned());
				}
			}
		});

		btnReviewerIdRemove.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				List<Integer> checkedRowIndices = getCheckedItemIndices(reviewersTable);
				if (checkedRowIndices != null && checkedRowIndices.size() > 0) {
					ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer.getContentProvider();
					reviewerContentProvider.removeElementsAt(checkedRowIndices);
					reviewersViewer.refresh();
					btnAddDefaultReviewerGroup.setEnabled(isDefaultReviewerGroupPresent() && !isDefaultReviewerGroupAssigned());
				}
			}
		});

		reviewersTable.addListener(SWT.Selection, reviewersTableOnSelectEventListener);
	}

	private void populateWorkItemsCombo() {
		workItemsComboViewer.setContentProvider(ArrayContentProvider.getInstance());
		workItemsComboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ShelvesetWorkItem) {
					ShelvesetWorkItem current = (ShelvesetWorkItem) element;
					return current.getName();
				}
				return super.getText(element);
			}
		});

		List<ShelvesetWorkItem> shelvesetWorkItems = shelvesetItem.getWorkItemContainer().getWorkItems();
		workItemsComboViewer.setInput(shelvesetWorkItems.toArray(new ShelvesetWorkItem[0]));
		if (!shelvesetWorkItems.isEmpty()) {
			workItemsComboViewer.getCombo().select(0);
		}
	}

	private TableViewer createReviewersViewer(Composite parent) {
		TableViewer reviewersViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.CHECK);
		ReviewerContentProvider reviewerContentProvider = new ReviewerContentProvider(shelvesetItem.getReviewers(false));
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
					TeamFoundationIdentity identity = IdentityUtil.getIdentity(reviewerInfo.getReviewerId());
					if (identity != null) {
						return identity.getDisplayName();
					}
					return reviewerInfo.getReviewerId();
				}
				return null;
			}
		});

		TableColumn reviewersTableColumn1 = reviewersViewerColumn1.getColumn();
		reviewersTableColumn1.setText("Reviewer");
		reviewersTableColumn1.setWidth(530);
		reviewersTableColumn1.setResizable(false);

		TableViewerColumn reviewersViewerColumn2 = new TableViewerColumn(reviewersViewer, SWT.NONE);
		reviewersViewerColumn2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element != null) {
					ReviewerInfo reviewerInfo = (ReviewerInfo) element;
					String approverId = reviewerInfo.getApproverId();
					return (approverId != null) ? approverId : "<Pending Approval>";
				}
				return null;
			}
		});

		TableColumn reviewersTableColumn2 = reviewersViewerColumn2.getColumn();
		reviewersTableColumn2.setText("Approver");
		reviewersTableColumn2.setWidth(220);
		reviewersTableColumn2.setResizable(false);

		reviewersViewer.setInput(reviewerContentProvider.getReviewers());
		return reviewersViewer;
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

	private void saveInput() {
		ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer.getContentProvider();
		reviewers = reviewerContentProvider.getReviewers();
		StructuredSelection selection = (StructuredSelection) workItemsComboViewer.getSelection();
		selectedWorkItem = (ShelvesetWorkItem) selection.getFirstElement();
	}

	private boolean isDefaultReviewerGroupPresent() {
		return IdentityUtil.getDefaultReviewersGroup() != null;
	}

	private boolean isDefaultReviewerGroupAssigned() {
		boolean result = false;
		ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer.getContentProvider();
		List<ReviewerInfo> reviewers = reviewerContentProvider.getReviewers();
		if (reviewers != null) {
			for (ReviewerInfo reviewerInfo : reviewers) {
				String reviewerId = reviewerInfo.getReviewerId();
				if (IdentityUtil.isDefaultReviewerGroupId(reviewerId)) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	@Override
	protected void okPressed() {
		ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer.getContentProvider();
		if (!reviewerContentProvider.getReviewers().isEmpty()) {
			saveInput();
			super.okPressed();
		} else {
			setErrorMessage("Please assign reviewers before creating a codereview request");
		}
	}

	public List<ReviewerInfo> getReviewers() {
		return reviewers;
	}

	public ShelvesetWorkItem getSelectedWorkItem() {
		return selectedWorkItem;
	}

	private void execInUIThread(Runnable runnable) {
		if (Display.getCurrent() != null) {
			runnable.run();
		} else {
			Display display = Display.getDefault();
			if (!display.isDisposed()) {
				display.asyncExec(runnable);
			}
		}
	}

	private boolean validateUserId(String reviewerId, boolean checkIfEmpty) {
		boolean result = false;
		if (reviewerId == null || reviewerId.isEmpty()) {
			if (checkIfEmpty) {
				setErrorMessage("Please select a reviewer");
			} else {
				execInUIThread(this::restoreDefaultMessage);
			}
		} else if (IdentityUtil.getIdentity(reviewerId) != null) {
			if (!IdentityUtil.userNamesSame(reviewerId, IdentityUtil.getCurrentUserName())) {
				ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer.getContentProvider();
				if (!reviewerContentProvider.reviewerIdExists(reviewerId)) {
					result = true;
					execInUIThread(this::restoreDefaultMessage);
				} else {
					execInUIThread(() -> {
						setErrorMessage("Reviewer has already been assigned");
					});
				}
			} else {
				execInUIThread(() -> {
					setErrorMessage("Cannot assign yourself as a reviewer");
				});
			}
		} else {
			execInUIThread(() -> {
				setErrorMessage("Not a valid TFS user: " + reviewerId);
			});
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

	private void setErrorMessage(String message) {
		setMessage(message, IMessageProvider.ERROR);
	}

	private void setMessage(String message, int type) {
		Image newImage = null;
		switch (type) {
			case IMessageProvider.NONE:
				return;
			case IMessageProvider.INFORMATION:
				newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO);
				break;
			case IMessageProvider.WARNING:
				newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING);
				break;
			case IMessageProvider.ERROR:
				newImage = JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR);
				break;
		}

		messageImageLabel.setImage(newImage);
		messageText.setText(Dialog.shortenText(message, messageText));
		messageText.setToolTipText(message);
	}
}
