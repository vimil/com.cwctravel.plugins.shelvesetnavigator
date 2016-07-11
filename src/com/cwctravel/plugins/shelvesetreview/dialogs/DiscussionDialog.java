package com.cwctravel.plugins.shelvesetreview.dialogs;

import java.io.IOException;
import java.util.EventObject;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.nebula.jface.gridviewer.GridTreeViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.jface.gridviewer.GridViewerEditor;
import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.contentProviders.DiscussionContentProvider;
import com.cwctravel.plugins.shelvesetreview.events.ShelvesetItemRefreshEvent;
import com.cwctravel.plugins.shelvesetreview.listeners.IShelvesetItemRefreshListener;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.DiscussionService;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionCommentDeleteRequestInfo;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;

public class DiscussionDialog extends TitleAreaDialog implements IShelvesetItemRefreshListener {
	private ShelvesetItem input;
	private int threadId;

	private String path;
	private int startLine;
	private int startColumn;
	private int endLine;
	private int endColumn;

	private GridTreeViewer discussionViewer;
	private Button editButton;
	private Button newDiscussionButton;
	private Button replyButton;
	private Button deleteButton;

	public DiscussionDialog(ShelvesetItem input, String path, int startLine, Shell parentShell) {
		this(input, -1, path, startLine, -1, startLine, -1, parentShell);
	}

	public DiscussionDialog(ShelvesetItem input, String path, int startLine, int startColumn, Shell parentShell) {
		this(input, -1, path, startLine, startColumn, startLine, startColumn, parentShell);
	}

	public DiscussionDialog(ShelvesetItem input, int threadId, Shell parentShell) {
		this(input, threadId, null, -1, -1, -1, -1, parentShell);
	}

	public DiscussionDialog(ShelvesetItem input, String path, int startLine, int startColumn, int endLine, int endColumn, Shell parentShell) {
		this(input, -1, path, startLine, startColumn, endLine, endColumn, parentShell);
	}

	protected DiscussionDialog(ShelvesetItem input, int threadId, String path, int startLine, int startColumn, int endLine, int endColumn,
			Shell parentShell) {
		super(parentShell);
		this.input = input;
		this.threadId = threadId;
		this.path = path;
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
		ShelvesetReviewPlugin.getDefault().addShelvesetItemRefreshListener(this);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add/Update Discussions");

		Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
		cancelButton.setText("Close");

		Button okButton = getButton(IDialogConstants.OK_ID);
		okButton.setVisible(false);

	}

	public boolean close() {
		boolean returnValue = super.close();
		if (returnValue) {
			ShelvesetReviewPlugin.getDefault().removeShelvesetItemRefreshListener(this);
		}
		return returnValue;
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
		createDiscussionViewer(container);

		Grid discussionTree = discussionViewer.getGrid();
		FormData fdDiscussionTree = new FormData(900, 400);
		fdDiscussionTree.top = new FormAttachment(null, 5, SWT.BOTTOM);
		fdDiscussionTree.left = new FormAttachment(0, 5);
		fdDiscussionTree.right = new FormAttachment(100, -5);
		discussionTree.setLayoutData(fdDiscussionTree);

		editButton = createEditButton(container);

		FormData fdEditButton = new FormData(convertWidthInCharsToPixels(18), 25);
		fdEditButton.top = new FormAttachment(discussionTree, 5, SWT.BOTTOM);
		fdEditButton.left = new FormAttachment(0, 5);
		editButton.setLayoutData(fdEditButton);

		createNewDiscussionButton(container);

		FormData fdNewDiscussionButton = new FormData(convertWidthInCharsToPixels(20), 25);
		fdNewDiscussionButton.top = new FormAttachment(discussionTree, 5, SWT.BOTTOM);
		fdNewDiscussionButton.left = new FormAttachment(editButton, 10, SWT.RIGHT);
		newDiscussionButton.setLayoutData(fdNewDiscussionButton);

		createReplyButton(container);
		FormData fdReplyButton = new FormData(convertWidthInCharsToPixels(18), 25);
		fdReplyButton.top = new FormAttachment(discussionTree, 5, SWT.BOTTOM);
		fdReplyButton.left = new FormAttachment(newDiscussionButton, 10, SWT.RIGHT);
		replyButton.setLayoutData(fdReplyButton);
		replyButton.setEnabled(false);

		createDeleteButton(container);
		FormData fdDeleteButton = new FormData(convertWidthInCharsToPixels(18), 25);
		fdDeleteButton.top = new FormAttachment(discussionTree, 5, SWT.BOTTOM);
		fdDeleteButton.left = new FormAttachment(replyButton, 10, SWT.RIGHT);
		deleteButton.setLayoutData(fdDeleteButton);
		deleteButton.setEnabled(false);
	}

	private void createDeleteButton(Composite container) {
		deleteButton = new Button(container, SWT.PUSH);
		deleteButton.setText("Delete");

		deleteButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				TreeSelection treeSelection = (TreeSelection) discussionViewer.getSelection();
				if (treeSelection != null) {
					ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) treeSelection.getFirstElement();
					if (shelvesetDiscussionItem != null && shelvesetDiscussionItem.canDelete()) {
						Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						if (MessageDialog.openConfirm(shell, "Confirm", "Are you sure you want to delete this comment")) {
							DiscussionCommentDeleteRequestInfo discussionCommentDeleteRequestInfo = new DiscussionCommentDeleteRequestInfo();
							discussionCommentDeleteRequestInfo.setThreadId(shelvesetDiscussionItem.getThreadId());
							discussionCommentDeleteRequestInfo.setCommentId(shelvesetDiscussionItem.getId());
							try {
								DiscussionService.deleteDiscussionComment(TFSUtil.getTFSConnection(), discussionCommentDeleteRequestInfo);
								input.scheduleRefresh();
							} catch (IOException iE) {
								ShelvesetReviewPlugin.log(Status.ERROR, iE.getMessage(), iE);
							}
						}
					}
				}
			}
		});
	}

	private void createReplyButton(Composite container) {
		replyButton = new Button(container, SWT.PUSH);
		replyButton.setText("Reply...");

		replyButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				TreeSelection treeSelection = (TreeSelection) discussionViewer.getSelection();
				if (treeSelection != null) {
					ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) treeSelection.getFirstElement();
					if (shelvesetDiscussionItem != null && shelvesetDiscussionItem.canReply()) {
						Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						DiscussionCommentDialog discussionCommentDialog = new DiscussionCommentDialog(input, shelvesetDiscussionItem.getThreadId(),
								shell);
						discussionCommentDialog.create();
						discussionCommentDialog.open();
					}
				}
			}
		});
	}

	private void createNewDiscussionButton(Composite container) {
		newDiscussionButton = new Button(container, SWT.PUSH);
		newDiscussionButton.setText("New Discussion...");

		newDiscussionButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				DiscussionCommentDialog discussionCommentDialog = new DiscussionCommentDialog(input, path, startLine, startColumn, endLine, endColumn,
						shell);
				discussionCommentDialog.create();
				discussionCommentDialog.open();
			}

		});
	}

	private Button createEditButton(Composite container) {
		Button editButton = new Button(container, SWT.PUSH);
		editButton.setText("Edit");
		editButton.setEnabled(false);
		editButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				TreeSelection treeSelection = (TreeSelection) discussionViewer.getSelection();
				if (treeSelection != null) {
					ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) treeSelection.getFirstElement();
					if (shelvesetDiscussionItem != null && shelvesetDiscussionItem.canEdit()) {
						discussionViewer.editElement(shelvesetDiscussionItem, 0);
					}
				}
			}

		});

		return editButton;
	}

	private GridTreeViewer createDiscussionViewer(Composite parent) {
		Grid discussionGrid = new Grid(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		discussionGrid.setHeaderVisible(true);

		discussionViewer = new GridTreeViewer(discussionGrid);

		GridViewerColumn gridViewerCommentColumn = new GridViewerColumn(discussionViewer, SWT.NONE);
		GridColumn gridCommentColumn = gridViewerCommentColumn.getColumn();
		gridCommentColumn.setWidth(895);
		gridCommentColumn.setText("Comment");
		gridCommentColumn.setTree(true);
		gridCommentColumn.setCellRenderer(new StyledDiscussionLabelRenderer(discussionGrid));

		discussionGrid.setAutoHeight(true);
		discussionGrid.setHeaderVisible(false);
		discussionGrid.setLinesVisible(false);

		DiscussionContentProvider discussionContentProvider = threadId >= 0 ? new DiscussionContentProvider(threadId)
				: new DiscussionContentProvider(path, startLine, startColumn, endLine, endColumn);
		discussionViewer.setContentProvider(discussionContentProvider);
		discussionViewer.setLabelProvider(new DiscussionLabelProvider());
		discussionViewer.setCellEditors(new CellEditor[] { new TextCellEditor(discussionGrid, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL) });
		discussionViewer.setCellModifier(new ICellModifier() {

			@Override
			public void modify(Object element, String property, Object value) {
				GridItem gridItem = (GridItem) element;
				Object data = gridItem.getData();
				if ("comment".equals(property) && data instanceof ShelvesetDiscussionItem) {
					ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) data;
					if (shelvesetDiscussionItem.canEdit()) {
						try {
							if (shelvesetDiscussionItem.updateComment((String) value)) {
								shelvesetDiscussionItem.getParent().scheduleRefresh();
							}
						} catch (IOException e) {
							ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
						}
					}
				}

			}

			@Override
			public Object getValue(Object element, String property) {
				String result = "";
				if ("comment".equals(property) && element instanceof ShelvesetDiscussionItem) {
					ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) element;
					result = shelvesetDiscussionItem.getComment();
				}
				return result;
			}

			@Override
			public boolean canModify(Object element, String property) {
				boolean result = false;
				if ("comment".equals(property) && element instanceof ShelvesetDiscussionItem) {
					ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) element;
					result = shelvesetDiscussionItem.canEdit();
				}
				return result;
			}
		});
		discussionViewer.setColumnProperties(new String[] { "comment" });
		discussionViewer.setInput(input);
		discussionViewer.expandAll();

		ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(discussionViewer) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				// Enable editor only with mouse double click
				if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION) {
					EventObject source = event.sourceEvent;
					if (source instanceof MouseEvent && ((MouseEvent) source).button == 3)
						return false;

					return true;
				}

				return event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};

		GridViewerEditor.create(discussionViewer, activationSupport, ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);

		discussionViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection treeSelection = (TreeSelection) discussionViewer.getSelection();
				if (treeSelection != null) {
					ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) treeSelection.getFirstElement();
					if (shelvesetDiscussionItem != null && shelvesetDiscussionItem.canEdit()) {
						editButton.setEnabled(true);
					} else {
						editButton.setEnabled(false);
					}

					if (shelvesetDiscussionItem != null && shelvesetDiscussionItem.canReply()) {
						replyButton.setEnabled(true);
					} else {
						replyButton.setEnabled(false);
					}

					if (shelvesetDiscussionItem != null && shelvesetDiscussionItem.canDelete()) {
						deleteButton.setEnabled(true);
					} else {
						deleteButton.setEnabled(false);
					}
				}
			}
		});

		return discussionViewer;
	}

	@Override
	public void onShelvesetItemRefreshed(ShelvesetItemRefreshEvent event) {
		discussionViewer.refresh();
		discussionViewer.expandAll();
	}
}
