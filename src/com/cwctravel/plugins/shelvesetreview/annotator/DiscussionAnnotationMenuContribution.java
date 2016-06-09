package com.cwctravel.plugins.shelvesetreview.annotator;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.util.EditorUtil;

public class DiscussionAnnotationMenuContribution extends ContributionItem {
	private ITextEditor editor;
	private IVerticalRulerInfo rulerInfo;

	public DiscussionAnnotationMenuContribution(ITextEditor editor) {
		this.editor = editor;
		this.rulerInfo = getRulerInfo();
	}

	private IVerticalRulerInfo getRulerInfo() {
		return (IVerticalRulerInfo) editor.getAdapter(IVerticalRulerInfo.class);
	}

	@Override
	// Create a menu item for each marker on the line clicked on
	public void fill(Menu menu, int index) {
		int lineOfLastMouseButtonActivity = rulerInfo.getLineOfLastMouseButtonActivity();
		if (lineOfLastMouseButtonActivity >= 0) {
			DiscussionAnnotation discussionAnnotation = EditorUtil.getDiscussionAnnotationAtLine(editor, lineOfLastMouseButtonActivity);
			if (discussionAnnotation != null) {
				MenuItem menuItem = new MenuItem(menu, SWT.CHECK, index);
				menuItem.setText("Edit Discussion(s)...");
				menuItem.setImage(ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.DISCUSSION_ICON_ID));
				menuItem.addSelectionListener(createEditDiscussionSelectionListener(discussionAnnotation));
			} else {
				IRegion region = EditorUtil.getLineInfo(editor, lineOfLastMouseButtonActivity);
				if (region != null) {
					MenuItem menuItem = new MenuItem(menu, SWT.CHECK, index);
					menuItem.setText("Add Comment...");
					menuItem.setImage(ShelvesetReviewPlugin.getImage(ShelvesetReviewPlugin.DISCUSSION_ICON_ID));
					menuItem.addSelectionListener(createAddCommentSelectionListener(lineOfLastMouseButtonActivity, region));
				}
			}
		}
	}

	private SelectionListener createAddCommentSelectionListener(int lineNumber, IRegion region) {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				EditorUtil.showDiscussionCommentDialog(editor, lineNumber + 1, 1, lineNumber + 1, region.getLength());
			}
		};
	}

	// Action to be performed when clicking on the menu item is defined here
	private SelectionAdapter createEditDiscussionSelectionListener(DiscussionAnnotation discussionAnnotation) {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				EditorUtil.showDiscussionDialog(editor, discussionAnnotation.getStartLine(), discussionAnnotation.getStartColumn(),
						discussionAnnotation.getEndLine(), discussionAnnotation.getEndColumn());
			}
		};
	}
}
