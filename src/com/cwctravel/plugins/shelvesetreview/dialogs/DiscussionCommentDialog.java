package com.cwctravel.plugins.shelvesetreview.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DiscussionCommentDialog extends Dialog {
	private Text txtComment;
	private String comment;

	public DiscussionCommentDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		FormLayout layout = new FormLayout();
		container.setLayout(layout);

		Label lblComment = new Label(container, SWT.NONE);
		lblComment.setText("Comment:");
		FormData fdLblComment = new FormData(convertWidthInCharsToPixels(15), 20);
		fdLblComment.top = new FormAttachment(null, 5, SWT.BOTTOM);
		fdLblComment.left = new FormAttachment(0, 5);
		lblComment.setLayoutData(fdLblComment);

		txtComment = new Text(container, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		FormData fdTxtComment = new FormData(390, 170);
		fdTxtComment.top = new FormAttachment(lblComment, 5, SWT.BOTTOM);
		fdTxtComment.left = new FormAttachment(0, 5);
		txtComment.setLayoutData(fdTxtComment);

		txtComment.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				Text textWidget = (Text) e.getSource();
				comment = textWidget.getText();
			}
		});

		return container;
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("New Discussion");
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	@Override
	protected void okPressed() {
		super.okPressed();
	}

	public String getComment() {
		return comment;
	}

}
