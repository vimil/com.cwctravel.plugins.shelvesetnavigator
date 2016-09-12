package com.cwctravel.plugins.shelvesetreview.jobs;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.cwctravel.plugins.shelvesetreview.jobs.ui.RefreshShelvesetsJob;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ReviewerInfo;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetWorkItem;

public class CreateCodeReviewRequestJob extends Job {
	private ShelvesetItem shelvesetItem;
	private ShelvesetWorkItem shelvesetWorkItem;
	private List<ReviewerInfo> reviewerInfos;

	public CreateCodeReviewRequestJob(ShelvesetItem shelvesetItem, ShelvesetWorkItem shelvesetWorkItem, List<ReviewerInfo> reviewerInfos) {
		super("Create CodeReview Request");
		this.shelvesetItem = shelvesetItem;
		this.shelvesetWorkItem = shelvesetWorkItem;
		this.reviewerInfos = reviewerInfos;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Create CodeReview Request", 0);
		shelvesetItem.createCodeReviewRequest(shelvesetWorkItem, reviewerInfos);
		monitor.done();

		new RefreshShelvesetsJob(shelvesetItem).schedule();

		return Status.OK_STATUS;
	}

}
