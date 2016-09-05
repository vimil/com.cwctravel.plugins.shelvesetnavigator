package com.cwctravel.plugins.shelvesetreview.dialogs;

import java.io.IOException;

import org.eclipse.core.runtime.Status;
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

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.DiscussionService;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionCreateRequestInfo;
import com.cwctravel.plugins.shelvesetreview.rest.discussion.threads.dto.DiscussionReplyRequestInfo;
import com.cwctravel.plugins.shelvesetreview.util.IdentityUtil;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;

public class DiscussionCommentDialog extends Dialog {
	private static final int CREATE_MODE = 0;
	private static final int REPLY_MODE = 1;
	private static final int NOACTION_MODE = 2;

	private final ShelvesetItem shelvesetItem;
	private final String path;
	private final int startLine;
	private final int startColumn;
	private final int endLine;
	private final int endColumn;
	private final int threadId;
	private final int mode;

	private Text txtComment;
	private String comment;
	private String title;

	public DiscussionCommentDialog(String title, String defaultComment, Shell shell) {
		super(shell);
		this.mode = NOACTION_MODE;
		this.title = title;
		this.comment = defaultComment;
		shelvesetItem = null;
		path = null;
		startLine = -1;
		startColumn = -1;
		endLine = -1;
		endColumn = -1;
		threadId = -1;
	}

	public DiscussionCommentDialog(ShelvesetItem shelvesetItem, int threadId, Shell shell) {
		super(shell);
		this.mode = REPLY_MODE;
		this.title = "New Discussion";
		this.shelvesetItem = shelvesetItem;
		this.threadId = threadId;
		this.path = null;
		this.startLine = -1;
		this.startColumn = -1;
		this.endLine = -1;
		this.endColumn = -1;

	}

	public DiscussionCommentDialog(ShelvesetItem shelvesetItem, String path, int startLine, int startColumn, int endLine, int endColumn,
			Shell shell) {
		super(shell);
		this.mode = CREATE_MODE;
		this.title = "New Discussion";
		this.shelvesetItem = shelvesetItem;
		this.path = path;
		this.startLine = startLine;
		this.startColumn = startColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
		this.threadId = -1;
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
		if (comment != null) {
			txtComment.setText(comment);
		}

		FormData fdTxtComment = new FormData(390, 170);
		fdTxtComment.top = new FormAttachment(lblComment, 5, SWT.BOTTOM);
		fdTxtComment.left = new FormAttachment(0, 5);
		fdTxtComment.right = new FormAttachment(100, -5);
		fdTxtComment.bottom = new FormAttachment(100, -5);
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
		shell.setText(title);
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
		try {
			if (mode == CREATE_MODE) {
				DiscussionCreateRequestInfo discussionCreateRequestInfo = new DiscussionCreateRequestInfo();
				discussionCreateRequestInfo.setAuthorId(IdentityUtil.getCurrentUserId());
				discussionCreateRequestInfo.setComment(comment);
				discussionCreateRequestInfo.setShelvesetName(shelvesetItem.getName());
				discussionCreateRequestInfo.setShelvesetOwnerName(shelvesetItem.getOwnerName());
				discussionCreateRequestInfo.setPath(path);
				discussionCreateRequestInfo.setStartLine(startLine);
				discussionCreateRequestInfo.setStartColumn(startColumn);
				discussionCreateRequestInfo.setEndLine(endLine);
				discussionCreateRequestInfo.setEndColumn(endColumn);

				DiscussionService.createDiscussion(TFSUtil.getTFSConnection(), discussionCreateRequestInfo);

				shelvesetItem.scheduleRefresh();
			} else if (mode == REPLY_MODE) {
				DiscussionReplyRequestInfo discussionReplyRequestInfo = new DiscussionReplyRequestInfo();
				discussionReplyRequestInfo.setAuthorId(IdentityUtil.getCurrentUserId());
				discussionReplyRequestInfo.setThreadId(threadId);
				discussionReplyRequestInfo.setComment(comment);

				DiscussionService.replyDiscussion(TFSUtil.getTFSConnection(), discussionReplyRequestInfo);
				shelvesetItem.scheduleRefresh();
			}
		} catch (IOException iE) {
			ShelvesetReviewPlugin.log(Status.ERROR, iE.getMessage(), iE);
		}

		super.okPressed();

	}

	public String getComment() {
		return comment;
	}

}
