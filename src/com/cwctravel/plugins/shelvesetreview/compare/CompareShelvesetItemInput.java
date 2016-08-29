package com.cwctravel.plugins.shelvesetreview.compare;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareViewerSwitchingPane;
import org.eclipse.compare.Splitter;
import org.eclipse.compare.contentmergeviewer.IMergeViewerContentProvider;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.StructureDiffViewer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.DefaultMarkerAnnotationAccess;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.constants.ShelvesetReviewConstants;
import com.cwctravel.plugins.shelvesetreview.events.ShelvesetItemRefreshEvent;
import com.cwctravel.plugins.shelvesetreview.listeners.IShelvesetItemRefreshListener;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.AnnotationUtil;
import com.cwctravel.plugins.shelvesetreview.util.CompareUtil;
import com.cwctravel.plugins.shelvesetreview.util.ReflectionUtil;
import com.cwctravel.plugins.shelvesetreview.util.TypeUtil;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public class CompareShelvesetItemInput extends CompareEditorInput implements ITextListener, IShelvesetItemRefreshListener {
	private static final int VERTICAL_RULER_WIDTH = 12;

	private ShelvesetItem leftShelvesetItem;
	private ShelvesetItem rightShelvesetItem;

	private ShelvesetFileItem leftShelvesetFileItem;
	private ShelvesetFileItem rightShelvesetFileItem;

	private TextMergeViewer textMergeViewer;
	private AnnotationModel leftAnnotationModel;
	private AnnotationModel rightAnnotationModel;
	private IDocument leftDocument;
	private IDocument rightDocument;

	public CompareShelvesetItemInput(ShelvesetItem item1, ShelvesetItem item2) {
		super(new ShelvesetCompareConfiguration());
		init(item1, item2);
	}

	private void init(ShelvesetItem item1, ShelvesetItem item2) {
		this.leftShelvesetItem = item1;
		this.rightShelvesetItem = item2;
		setTitle("Compare Shelvesets");
		CompareConfiguration compareConfiguration = getCompareConfiguration();
		compareConfiguration.setLeftEditable(false);
		compareConfiguration.setRightEditable(false);
		compareConfiguration.setLeftLabel(leftShelvesetItem.getName());
		compareConfiguration.setRightLabel(rightShelvesetItem.getName());
		compareConfiguration.setDefaultLabelProvider(new ShelvesetCompareLabelProvider());

		leftAnnotationModel = new AnnotationModel();
		rightAnnotationModel = new AnnotationModel();
		ShelvesetReviewPlugin.getDefault().addShelvesetItemRefreshListener(this);
	}

	protected ImageHelper getImageHelper() {
		ShelvesetCompareConfiguration compareConfiguration = (ShelvesetCompareConfiguration) getCompareConfiguration();
		return compareConfiguration.getImageHelper();
	}

	@Override
	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		CompareShelvesetItem c1 = new CompareShelvesetItem(leftShelvesetItem, getImageHelper());
		CompareShelvesetItem c2 = new CompareShelvesetItem(rightShelvesetItem, getImageHelper());

		DiffNode diffNode = new DiffNode(c1, c2);
		return diffNode;
	}

	public Viewer findStructureViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
		CompareConfiguration compareConfiguration = getCompareConfiguration();
		StructureDiffViewer sdv = new StructureDiffViewer(parent, compareConfiguration);
		sdv.setStructureCreator(new ShelvesetStructureCreator(getTitle(compareConfiguration, input)));
		return sdv;
	}

	private String getTitle(CompareConfiguration compareConfiguration, ICompareInput input) {
		return compareConfiguration.getLeftLabel(input) + " -> " + compareConfiguration.getRightLabel(input);
	}

	public Viewer findContentViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
		Viewer result = super.findContentViewer(oldViewer, input, parent);
		textMergeViewer = (TextMergeViewer) result;
		return result;
	}

	protected CompareViewerSwitchingPane createContentViewerSwitchingPane(Splitter parent, int style, CompareEditorInput cei) {
		return new CompareShelvesetItemContentViewerSwitchingPane(parent, style, cei);
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

	protected void handleDispose() {
		super.handleDispose();

		leftShelvesetItem = null;
		rightShelvesetItem = null;

		leftShelvesetFileItem = null;
		rightShelvesetFileItem = null;

		textMergeViewer = null;
		leftAnnotationModel = null;
		rightAnnotationModel = null;
		leftDocument = null;
		rightDocument = null;
		ShelvesetReviewPlugin.getDefault().removeShelvesetItemRefreshListener(this);
	}

	@SuppressWarnings("unchecked")
	public void installListeners() {
		if (textMergeViewer != null) {
			List<SourceViewerDecorationSupport> sourceViewerDecorationSupportList = (List<SourceViewerDecorationSupport>) ReflectionUtil
					.getFieldValue(textMergeViewer, "fSourceViewerDecorationSupport", false);
			for (SourceViewerDecorationSupport sourceViewerDecorationSupport : sourceViewerDecorationSupportList) {
				sourceViewerDecorationSupport.setAnnotationPreference(ShelvesetReviewPlugin.getDefault().getDiscussionAnnotationPreference());
				IPreferenceStore preferenceStore = (IPreferenceStore) ReflectionUtil.getFieldValue(sourceViewerDecorationSupport, "fPreferenceStore",
						false);
				sourceViewerDecorationSupport.install(preferenceStore);
			}

			IMergeViewerContentProvider mergeViewerContentProvider = (IMergeViewerContentProvider) textMergeViewer.getContentProvider();
			if (mergeViewerContentProvider != null) {
				SourceViewer leftSourceViewer = (SourceViewer) CompareUtil.getTextViewer(textMergeViewer, CompareUtil.LEFT_LEG);
				if (leftSourceViewer != null) {
					CompareShelvesetFileItem leftCompareShelvesetFileItem = (CompareShelvesetFileItem) mergeViewerContentProvider
							.getLeftContent(textMergeViewer.getInput());
					leftShelvesetFileItem = (ShelvesetFileItem) leftCompareShelvesetFileItem.getShelvesetResourceItem();
					leftAnnotationModel.removeAllAnnotations();
					leftDocument = leftSourceViewer.getDocument();
					Control leftViewerControl = leftSourceViewer.getControl();
					Boolean rulerColumnAdded = TypeUtil.optBoolean((Boolean) leftViewerControl.getData("rulerColumnAdded"), false);
					if (!rulerColumnAdded) {
						AnnotationRulerColumn leftRulerColumn = new AnnotationRulerColumn(VERTICAL_RULER_WIDTH, new DefaultMarkerAnnotationAccess());
						leftRulerColumn.addAnnotationType(ShelvesetReviewConstants.ANNOTATION_TYPE_DISCUSSION_MARKER);
						leftSourceViewer.addVerticalRulerColumn(leftRulerColumn);
						leftViewerControl.setData("rulerColumnAdded", true);
					}
					leftSourceViewer.setDocument(leftDocument, leftAnnotationModel);
					leftSourceViewer.showAnnotations(true);
					updateLeftShelvesetFileItemAnnotations();
				}

				SourceViewer rightSourceViewer = (SourceViewer) CompareUtil.getTextViewer(textMergeViewer, CompareUtil.RIGHT_LEG);
				if (rightSourceViewer != null) {
					CompareShelvesetFileItem rightCompareShelvesetFileItem = (CompareShelvesetFileItem) mergeViewerContentProvider
							.getRightContent(textMergeViewer.getInput());
					rightShelvesetFileItem = (ShelvesetFileItem) rightCompareShelvesetFileItem.getShelvesetResourceItem();
					rightAnnotationModel.removeAllAnnotations();
					rightDocument = rightSourceViewer.getDocument();
					Control rightViewerControl = rightSourceViewer.getControl();
					Boolean rulerColumnAdded = TypeUtil.optBoolean((Boolean) rightViewerControl.getData("rulerColumnAdded"), false);
					if (!rulerColumnAdded) {
						AnnotationRulerColumn rightRulerColumn = new AnnotationRulerColumn(VERTICAL_RULER_WIDTH, new DefaultMarkerAnnotationAccess());
						rightRulerColumn.addAnnotationType(ShelvesetReviewConstants.ANNOTATION_TYPE_DISCUSSION_MARKER);
						rightSourceViewer.addVerticalRulerColumn(rightRulerColumn);
						rightViewerControl.setData("rulerColumnAdded", true);
					}
					rightSourceViewer.setDocument(rightDocument, rightAnnotationModel);
					rightSourceViewer.showAnnotations(true);
					updateRightShelvesetFileItemAnnotations();
				}
			}

		}
	}

	private void updateLeftShelvesetFileItemAnnotations() {
		if (leftShelvesetFileItem != null) {
			new Job("Updating Review Comments") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					AnnotationUtil.annotateDocument(leftDocument, leftAnnotationModel, leftShelvesetFileItem.getShelvesetName(),
							leftShelvesetFileItem.getShelvesetOwnerName(), leftShelvesetFileItem.getPath(), monitor);
					return Status.OK_STATUS;
				}
			}.schedule();
		}
	}

	private void updateRightShelvesetFileItemAnnotations() {
		if (rightShelvesetFileItem != null) {
			new Job("Updating Review Comments") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					AnnotationUtil.annotateDocument(rightDocument, rightAnnotationModel, rightShelvesetFileItem.getShelvesetName(),
							rightShelvesetFileItem.getShelvesetOwnerName(), rightShelvesetFileItem.getPath(), monitor);
					return Status.OK_STATUS;
				}
			}.schedule();
		}
	}

	@Override
	public void onShelvesetItemRefreshed(ShelvesetItemRefreshEvent event) {
		ShelvesetItem shelvesetItem = event.getShelvesetItem();
		if (shelvesetItem.equals(leftShelvesetItem)) {
			updateLeftShelvesetFileItemAnnotations();
		} else if (shelvesetItem.equals(rightShelvesetItem)) {
			updateRightShelvesetFileItemAnnotations();
		}
	}
}
