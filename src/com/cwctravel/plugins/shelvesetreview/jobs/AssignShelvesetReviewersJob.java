package com.cwctravel.plugins.shelvesetreview.jobs;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.cwctravel.plugins.shelvesetreview.jobs.ui.RefreshShelvesetsJob;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ReviewerInfo;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class AssignShelvesetReviewersJob extends Job {
	private ShelvesetItem shelvesetItem;
	private List<ReviewerInfo> reviewerInfos;

	public AssignShelvesetReviewersJob(ShelvesetItem shelvesetItem, List<ReviewerInfo> reviewerInfos) {
		super("Assign Shelveset Reviewers");
		this.shelvesetItem = shelvesetItem;
		this.reviewerInfos = reviewerInfos;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask("Assign Reviewers", 0);
		shelvesetItem.assignReviewers(reviewerInfos);
		monitor.done();

		new RefreshShelvesetsJob(shelvesetItem).schedule();

		return Status.OK_STATUS;
	}

}
