package com.cwctravel.plugins.shelvesetreview.util;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;

public class IconManager {
	public static final String SHELVESET_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.shelveset";
	public static final String INACTIVE_SHELVESET_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.inactiveshelveset";

	public static final String USER_GROUP_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.usergroup";
	public static final String REVIEW_GROUP_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.reviewgroup";
	public static final String INACTIVE_GROUP_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.inactivegroup";
	public static final String USER_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.user";
	public static final String MIXED_USER_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.user.mixed";
	public static final String UNASSIGNED_SHELVESET_USER_CATEGORY_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.usercategory.unassigned";
	public static final String PENDING_REVIEW_USER_CATEGORY_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.usercategory.pendingReview";
	public static final String BUILD_SUCCESSFUL_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.buildsuccessful";
	public static final String DISCUSSION_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.discussion";
	public static final String DISCUSSION_OVR_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.ovr.discussion";
	public static final String APPROVED_OVR_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.ovr.approved";
	public static final String WORKITEMS_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.workitems";
	public static final String WORKITEM_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.workitem";
	public static final String CODEREVIEW_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.codereview";
	public static final String CODEREVIEW_USER_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.codereview.user";
	public static final String CODEREVIEW_OPEN_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.codereview.open";
	public static final String CODEREVIEW_ACCEPTED_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetreview.navigator.icons.codereview.accepted";

	public static void loadIcons(ImageRegistry registry, Bundle bundle) {
		ImageDescriptor shelvesetIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/shelveset.png"), null));
		registry.put(SHELVESET_ICON_ID, shelvesetIconImage);

		ImageDescriptor inactiveShelvesetIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/inactive-shelveset.png"), null));
		registry.put(INACTIVE_SHELVESET_ICON_ID, inactiveShelvesetIconImage);

		ImageDescriptor userGroupIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/user-group.png"), null));
		registry.put(USER_GROUP_ICON_ID, userGroupIconImage);

		ImageDescriptor reviewGroupIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/review-group.png"), null));
		registry.put(REVIEW_GROUP_ICON_ID, reviewGroupIconImage);

		ImageDescriptor inactiveGroupIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/inactive-group.png"), null));
		registry.put(INACTIVE_GROUP_ICON_ID, inactiveGroupIconImage);

		ImageDescriptor userIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/user.png"), null));
		registry.put(USER_ICON_ID, userIconImage);

		ImageDescriptor mixedUserIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/mixed-user.png"), null));
		registry.put(MIXED_USER_ICON_ID, mixedUserIconImage);

		ImageDescriptor unassignedUserCategoryImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/unassigned-user-category.png"), null));
		registry.put(UNASSIGNED_SHELVESET_USER_CATEGORY_ICON_ID, unassignedUserCategoryImage);

		ImageDescriptor pendingReviewUserCategoryImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/pendingreview-user-category.png"), null));
		registry.put(PENDING_REVIEW_USER_CATEGORY_ICON_ID, pendingReviewUserCategoryImage);

		ImageDescriptor buildSuccessfulIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/build-successful-icon.png"), null));
		registry.put(BUILD_SUCCESSFUL_ICON_ID, buildSuccessfulIconImage);

		ImageDescriptor discussionIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/discussion-icon.png"), null));
		registry.put(DISCUSSION_ICON_ID, discussionIconImage);

		ImageDescriptor discussionOverlayIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/discussion-ovr-icon.png"), null));
		registry.put(DISCUSSION_OVR_ICON_ID, discussionOverlayIconImage);

		ImageDescriptor approvedOverlayIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/approved-ovr-icon.png"), null));
		registry.put(APPROVED_OVR_ICON_ID, approvedOverlayIconImage);

		ImageDescriptor workitemsIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/workitems.png"), null));
		registry.put(WORKITEMS_ICON_ID, workitemsIconImage);

		ImageDescriptor workitemIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/workitem.png"), null));
		registry.put(WORKITEM_ICON_ID, workitemIconImage);

		ImageDescriptor codeReviewIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/codereview-icon.png"), null));
		registry.put(CODEREVIEW_ICON_ID, codeReviewIconImage);

		ImageDescriptor codeReviewUserIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/codereview-my-icon.png"), null));
		registry.put(CODEREVIEW_USER_ICON_ID, codeReviewUserIconImage);

		ImageDescriptor codeReviewOpenIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/codereview-open-icon.png"), null));
		registry.put(CODEREVIEW_OPEN_ICON_ID, codeReviewOpenIconImage);

		ImageDescriptor codeReviewAcceptedIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/codereview-accepted-icon.png"), null));
		registry.put(CODEREVIEW_ACCEPTED_ICON_ID, codeReviewAcceptedIconImage);
	}

	public static Image getIcon(String key) {
		return ShelvesetReviewPlugin.getDefault().getImageRegistry().get(key);
	}
}
