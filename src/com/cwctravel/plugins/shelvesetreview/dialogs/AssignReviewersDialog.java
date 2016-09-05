package com.cwctravel.plugins.shelvesetreview.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
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
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.core.clients.webservices.TeamFoundationIdentity;

public class AssignReviewersDialog extends Dialog {

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

	private Text txtReviewerId;

	private List<ReviewerInfo> reviewers;

	private TableViewer reviewersViewer;

	private Text messageText;

	private Label messageImageLabel;

	public AssignReviewersDialog(ShelvesetItem shelvesetItem, Shell parentShell) {
		super(parentShell);
		this.shelvesetItem = shelvesetItem;
	}

	@Override
	public void create() {
		super.create();
		restoreDefaultMessage();
	}

	private void restoreDefaultMessage() {
		setMessage("Assign Reviewers for Shelveset " + shelvesetItem.getName(), IMessageProvider.INFORMATION);
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Assign Reviewers");
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
			RepeatingJob userIdValidatingJob = new RepeatingJob();

			@Override
			public void modifyText(ModifyEvent e) {
				String text = txtReviewerId.getText();
				userIdValidatingJob.schedule(() -> {
					validateUserId(text);
				});
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
					ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer.getContentProvider();
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
				if (checkedRowIndices != null && checkedRowIndices.size() > 0) {
					ReviewerContentProvider reviewerContentProvider = (ReviewerContentProvider) reviewersViewer.getContentProvider();
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
		ReviewerContentProvider reviewerContentProvider = new ReviewerContentProvider(shelvesetItem.getReviewers(true));
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
					TeamFoundationIdentity identity = TFSUtil.getIdentity(reviewerInfo.getReviewerId());
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

	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public List<ReviewerInfo> getReviewers() {
		return reviewers;
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

	private boolean validateUserId(String reviewerId) {
		boolean result = false;
		if (reviewerId == null || reviewerId.isEmpty()) {
			execInUIThread(this::restoreDefaultMessage);
		} else if (TFSUtil.getIdentity(reviewerId) != null) {
			if (!TFSUtil.userNamesSame(reviewerId, TFSUtil.getCurrentUserName())) {
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
