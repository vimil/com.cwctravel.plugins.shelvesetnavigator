package com.cwctravel.plugins.shelvesetreview.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetDiscussionItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFolderItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetWorkItem;
import com.cwctravel.plugins.shelvesetreview.util.DateUtil;
import com.cwctravel.plugins.shelvesetreview.util.IconManager;
import com.microsoft.tfs.core.clients.versioncontrol.soapextensions.ChangeType;

public class ShelvesetLabelDecorator implements ILightweightLabelDecorator {

	@Override
	public void addListener(ILabelProviderListener listener) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof ShelvesetFileItem) {
			ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) element;
			ChangeType changeType = shelvesetFileItem.getChangeType();
			if (changeType.contains(ChangeType.ADD)) {
				decoration.addPrefix("+");
			} else if (changeType.contains(ChangeType.DELETE)) {
				decoration.addPrefix("-");
			} else if (changeType.contains(ChangeType.EDIT)) {
				decoration.addPrefix(">");
			}

			if (shelvesetFileItem.hasDiscussions()) {
				ImageDescriptor discussionOverlayImageDescriptor = ShelvesetReviewPlugin.getDefault().getImageRegistry()
						.getDescriptor(IconManager.DISCUSSION_OVR_ICON_ID);
				decoration.addOverlay(discussionOverlayImageDescriptor, IDecoration.TOP_LEFT);
			}

		} else if (element instanceof ShelvesetFolderItem) {
			ShelvesetFolderItem shelvesetFolderItem = (ShelvesetFolderItem) element;
			if (shelvesetFolderItem.hasDiscussions()) {
				ImageDescriptor discussionOverlayImageDescriptor = ShelvesetReviewPlugin.getDefault().getImageRegistry()
						.getDescriptor(IconManager.DISCUSSION_OVR_ICON_ID);
				decoration.addOverlay(discussionOverlayImageDescriptor, IDecoration.TOP_LEFT);
			}
		} else if (element instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) element;
			if (!shelvesetItem.isInactive() && shelvesetItem.isCurrentUserOwner() && shelvesetItem.getReviewers(false).isEmpty()) {
				decoration.addSuffix("[not reviewed]");
			}

			String buildId = shelvesetItem.getBuildId();
			if (buildId != null && !buildId.isEmpty()) {
				ImageDescriptor buildSuccessfulImageDescriptor = ShelvesetReviewPlugin.getDefault().getImageRegistry()
						.getDescriptor(IconManager.BUILD_SUCCESSFUL_ICON_ID);
				decoration.addOverlay(buildSuccessfulImageDescriptor, IDecoration.BOTTOM_LEFT);
			}

			if (shelvesetItem.isInactive()) {
				decoration.addSuffix(" [" + DateUtil.ageAsPrettyString(shelvesetItem.getCreationDate()) + "]");
			}

			if (shelvesetItem.isApproved()) {
				ImageDescriptor approvedImageDescriptor = ShelvesetReviewPlugin.getDefault().getImageRegistry()
						.getDescriptor(IconManager.APPROVED_OVR_ICON_ID);
				decoration.addOverlay(approvedImageDescriptor);
			}

		} else if (element instanceof ShelvesetDiscussionItem) {
			ShelvesetDiscussionItem shelvesetDiscussionItem = (ShelvesetDiscussionItem) element;
			String authorDisplayName = shelvesetDiscussionItem.getAuthorDisplayName();
			if (authorDisplayName != null) {
				decoration.addSuffix(" [" + authorDisplayName + "]");
			}
		} else if (element instanceof ShelvesetWorkItem) {
			ShelvesetWorkItem shelvesetWorkItem = (ShelvesetWorkItem) element;
			decoration.addPrefix("[" + shelvesetWorkItem.getWorkItemID() + "] ");
		}
	}
}
