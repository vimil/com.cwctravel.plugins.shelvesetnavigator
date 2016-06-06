package com.cwctravel.plugins.shelvesetreview.dialogs;

import java.io.IOException;
import java.util.EventObject;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
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
import org.eclipse.swt.widgets.Shell;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.contentProviders.DiscussionContentProvider;
import com.cwctravel.plugins.shelvesetreview.events.ShelvesetItemRefreshEvent;
import com.cwctravel.plugins.shelvesetreview.listeners.IShelvesetItemRefreshListener;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;

public class DiscussionDialog extends TitleAreaDialog implements IShelvesetItemRefreshListener {
	private ShelvesetItem input;
	private String path;
	private int lineNumber;
	private int columnNumber;

	private GridTreeViewer discussionViewer;

	public DiscussionDialog(ShelvesetItem input, String path, int lineNumber, int columnNumber, Shell parentShell) {
		super(parentShell);
		this.input = input;
		this.path = path;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
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
	}

	private GridTreeViewer createDiscussionViewer(Composite parent) {
		Grid discussionGrid = new Grid(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		discussionGrid.setHeaderVisible(true);

		discussionViewer = new GridTreeViewer(discussionGrid);

		GridViewerColumn gridViewerCommentColumn = new GridViewerColumn(discussionViewer, SWT.NONE);
		GridColumn gridCommentColumn = gridViewerCommentColumn.getColumn();
		gridCommentColumn.setWidth(900);
		gridCommentColumn.setText("Comment");
		gridCommentColumn.setTree(true);
		gridCommentColumn.setCellRenderer(new StyledDiscussionLabelRenderer());

		discussionGrid.setAutoHeight(true);
		discussionGrid.setHeaderVisible(false);
		discussionGrid.setLinesVisible(false);
		discussionViewer.setContentProvider(new DiscussionContentProvider(path, lineNumber, columnNumber));
		discussionViewer.setLabelProvider(new DiscussionLabelProvider());
		discussionViewer.setCellEditors(new CellEditor[] { new TextCellEditor(discussionGrid, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL) });
		discussionViewer.setCellModifier(new ICellModifier() {

			@Override
			public void modify(Object element, String property, Object value) {
				GridItem gridItem = (GridItem) element;
				Object data = gridItem.getData();
				if ("comment".equals(property) && data instanceof ShelvesetDiscussionItem) {
					ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) data;
					boolean isCurrentUserAuthor = TFSUtil.userIdsSame(shelvesetDiscussionItem.getAuthorName(), TFSUtil.getCurrentUserId());
					if (isCurrentUserAuthor) {
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
					result = TFSUtil.userIdsSame(shelvesetDiscussionItem.getAuthorName(), TFSUtil.getCurrentUserId());
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

				return false;
			}
		};

		GridViewerEditor.create(discussionViewer, activationSupport, ColumnViewerEditor.TABBING_HORIZONTAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);

		return discussionViewer;
	}

	@Override
	public void onShelvesetItemRefreshed(ShelvesetItemRefreshEvent event) {
		discussionViewer.refresh();
	}
}
