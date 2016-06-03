package com.cwctravel.plugins.shelvesetreview.annotator;

import java.util.Iterator;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.filesystem.TFSFileStore;

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

	// Get all My Markers for this source file
	private DiscussionAnnotation getClickedDiscussionAnnotation() {
		DiscussionAnnotation result = null;
		try {
			IEditorInput editorInput = editor.getEditorInput();
			if (editorInput instanceof FileStoreEditorInput) {
				FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput) editorInput;
				IFileStore fileStore = EFS.getStore(fileStoreEditorInput.getURI());
				if (editor instanceof ITextEditor && fileStore instanceof TFSFileStore) {
					ITextEditor textEditor = (ITextEditor) editor;
					IDocumentProvider documentProvider = textEditor.getDocumentProvider();
					IDocument document = documentProvider.getDocument(editorInput);
					IAnnotationModel annotationModel = documentProvider.getAnnotationModel(editorInput);
					int lineOfLastMouseButtonActivity = rulerInfo.getLineOfLastMouseButtonActivity();

					Iterator<?> itr = annotationModel.getAnnotationIterator();
					while (itr.hasNext()) {
						Annotation annotation = (Annotation) itr.next();
						if (annotation instanceof DiscussionAnnotation) {
							Position position = annotationModel.getPosition(annotation);
							if (position != null) {
								int offset = position.getOffset();
								int lineNumber = document.getLineOfOffset(offset);

								if (lineOfLastMouseButtonActivity == lineNumber) {
									result = (DiscussionAnnotation) annotation;
									break;
								}
							}
						}
					}
				}
			}
		} catch (CoreException | BadLocationException e) {
			ShelvesetReviewPlugin.log(Status.ERROR, e.getMessage(), e);
		}
		return result;
	}

	@Override
	// Create a menu item for each marker on the line clicked on
	public void fill(Menu menu, int index) {
		DiscussionAnnotation discussionAnnotation = getClickedDiscussionAnnotation();
		if (discussionAnnotation != null) {
			MenuItem menuItem = new MenuItem(menu, SWT.CHECK, index);
			menuItem.setText("Edit Discussion(s)...");
			menuItem.addSelectionListener(createDynamicSelectionListener(discussionAnnotation));
		}
	}

	// Action to be performed when clicking on the menu item is defined here
	private SelectionAdapter createDynamicSelectionListener(DiscussionAnnotation discussionAnnotation) {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Clicked");
			}
		};
	}
}
