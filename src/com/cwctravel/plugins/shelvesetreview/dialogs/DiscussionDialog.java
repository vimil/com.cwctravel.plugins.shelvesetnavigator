package com.cwctravel.plugins.shelvesetreview.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.cwctravel.plugins.shelvesetreview.contentProviders.DiscussionContentProvider;

public class DiscussionDialog extends TitleAreaDialog {
	private Object input;
	private int lineNumber;
	private int columnNumber;

	private TreeViewer discussionViewer;

	public DiscussionDialog(Object input, int lineNumber, int columnNumber, Shell parentShell) {
		super(parentShell);
		this.input = input;
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Add/Update Discussions");
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
		discussionViewer = createDiscussionViewer(container);

		Tree discussionTree = discussionViewer.getTree();
		FormData fdDiscussionTree = new FormData(750, 90);
		fdDiscussionTree.top = new FormAttachment(null, 5, SWT.BOTTOM);
		fdDiscussionTree.left = new FormAttachment(0, 5);
		fdDiscussionTree.right = new FormAttachment(100, -5);
		fdDiscussionTree.bottom = new FormAttachment(100, -35);
		discussionTree.setLayoutData(fdDiscussionTree);
	}

	private TreeViewer createDiscussionViewer(Composite parent) {
		Tree reviewDiscussionTree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		reviewDiscussionTree.setHeaderVisible(true);

		discussionViewer = new TreeViewer(reviewDiscussionTree);

		TreeColumn column1 = new TreeColumn(reviewDiscussionTree, SWT.LEFT);
		reviewDiscussionTree.setLinesVisible(true);
		column1.setAlignment(SWT.LEFT);
		column1.setText("Comment");
		column1.setWidth(160);
		TreeColumn column2 = new TreeColumn(reviewDiscussionTree, SWT.RIGHT);
		column2.setAlignment(SWT.LEFT);
		column2.setText("Author");
		column2.setWidth(100);
		TreeColumn column3 = new TreeColumn(reviewDiscussionTree, SWT.RIGHT);
		column3.setAlignment(SWT.LEFT);
		column3.setText("Last Updated Date");
		column3.setWidth(35);

		discussionViewer.setContentProvider(new DiscussionContentProvider(input, lineNumber, columnNumber));
		discussionViewer.setLabelProvider(new DiscussionLabelProvider());
		discussionViewer.setInput(input);
		discussionViewer.expandAll();
		return discussionViewer;
	}
}
