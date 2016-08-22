package com.cwctravel.plugins.shelvesetreview.compare;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareViewerSwitchingPane;
import org.eclipse.compare.Splitter;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.StructureDiffViewer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.CompareUtil;
import com.cwctravel.plugins.shelvesetreview.util.ReflectionUtil;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public class CompareShelvesetItemInput extends CompareEditorInput implements ITextListener {
	private ShelvesetItem shelvesetItem1;
	private ShelvesetItem shelvesetItem2;

	private TextMergeViewer textMergeViewer;

	public CompareShelvesetItemInput(ShelvesetItem item1, ShelvesetItem item2) {
		super(new ShelvesetCompareConfiguration());
		init(item1, item2);
	}

	private void init(ShelvesetItem item1, ShelvesetItem item2) {
		this.shelvesetItem1 = item1;
		this.shelvesetItem2 = item2;
		setTitle("Compare Shelvesets");
		CompareConfiguration compareConfiguration = getCompareConfiguration();
		compareConfiguration.setLeftEditable(false);
		compareConfiguration.setRightEditable(false);
		compareConfiguration.setLeftLabel(shelvesetItem1.getName());
		compareConfiguration.setRightLabel(shelvesetItem2.getName());
	}

	protected ImageHelper getImageHelper() {
		ShelvesetCompareConfiguration compareConfiguration = (ShelvesetCompareConfiguration) getCompareConfiguration();
		return compareConfiguration.getImageHelper();
	}

	@Override
	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		CompareShelvesetItem c1 = new CompareShelvesetItem(shelvesetItem1, getImageHelper());
		CompareShelvesetItem c2 = new CompareShelvesetItem(shelvesetItem2, getImageHelper());

		DiffNode diffNode = new DiffNode(c1, c2);
		return diffNode;
	}

	public Viewer findStructureViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
		StructureDiffViewer sdv = new StructureDiffViewer(parent, getCompareConfiguration());
		sdv.setStructureCreator(new ShelvesetStructureCreator());
		return sdv;
	}

	public Viewer findContentViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
		Viewer result = super.findContentViewer(oldViewer, input, parent);
		textMergeViewer = (TextMergeViewer) result;

		return result;
	}

	protected CompareViewerSwitchingPane createContentViewerSwitchingPane(Splitter parent, int style, CompareEditorInput cei) {
		return new ShelvesetCompareContentViewerSwitchingPane(parent, style, cei);
	}

	public Control createContents(Composite parent) {
		Control result = super.createContents(parent);
		return result;
	}

	@Override
	public void textChanged(TextEvent event) {
		new UIJob("Updating Review Comments") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				// CompareUtil.annotate(textMergeViewer, monitor);
				return Status.OK_STATUS;
			}
		}.schedule();

	}

	public void installListeners() {
		if (textMergeViewer != null) {
			SourceViewer leftSourceViewer = (SourceViewer) CompareUtil.getTextViewer(textMergeViewer, CompareUtil.LEFT_LEG);
			AnnotationModel annotationModel = new AnnotationModel();
			leftSourceViewer.setDocument(leftSourceViewer.getDocument(), annotationModel);
			annotationModel.connect(leftSourceViewer.getDocument());
			Annotation a = new Annotation("com.cwctravel.plugins.shelvesetreview.discussionMarker", false, "hello");
			Position p = new Position(1, 10);
			annotationModel.addAnnotation(a, p);

			List<SourceViewerDecorationSupport> sourceViewerDecorationSupportList = (List<SourceViewerDecorationSupport>) ReflectionUtil
					.getFieldValue(textMergeViewer, "fSourceViewerDecorationSupport", false);
			for (SourceViewerDecorationSupport sourceViewerDecorationSupport : sourceViewerDecorationSupportList) {
				sourceViewerDecorationSupport.setAnnotationPreference(ShelvesetReviewPlugin.getDefault().getDiscussionAnnotationPreference());
				IPreferenceStore preferenceStore = (IPreferenceStore) ReflectionUtil.getFieldValue(sourceViewerDecorationSupport, "fPreferenceStore",
						false);
				sourceViewerDecorationSupport.install(preferenceStore);
			}
			System.out.println(sourceViewerDecorationSupportList);
			textMergeViewer.invalidateTextPresentation();
			// SourceViewer sourceViewer = (SourceViewer)
			// CompareUtil.getTextViewer(textMergeViewer, 0);
			// sourceViewer.getS
			// textViewer.addTextListener(this);
			// textMergeViewer.invalidateTextPresentation();
		}
	}
}
