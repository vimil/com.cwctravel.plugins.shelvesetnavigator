package com.cwctravel.plugins.shelvesetreview;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.cwctravel.plugins.shelvesetreview.annotator.DiscussionAnnotator;
import com.cwctravel.plugins.shelvesetreview.events.ShelvesetContainerRefreshEvent;
import com.cwctravel.plugins.shelvesetreview.events.ShelvesetItemRefreshEvent;
import com.cwctravel.plugins.shelvesetreview.jobs.ShelvesetGroupItemsRefreshJob;
import com.cwctravel.plugins.shelvesetreview.listeners.IShelvesetContainerRefreshListener;
import com.cwctravel.plugins.shelvesetreview.listeners.IShelvesetItemRefreshListener;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.client.common.autoconnect.AutoConnector;
import com.microsoft.tfs.client.common.autoconnect.AutoConnectorProvider;
import com.microsoft.tfs.client.common.repository.RepositoryManager;
import com.microsoft.tfs.client.eclipse.TFSEclipseClientPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class ShelvesetReviewPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.cwctravel.plugins.shelvesetreview";

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

	private static ShelvesetReviewPlugin plugin;

	private ListenerList shelvesetContainerRefreshListeners;
	private ListenerList shelvesetItemRefreshListeners;

	private ShelvesetGroupItemContainer shelvesetGroupItemContainer;

	public ShelvesetReviewPlugin() {
		shelvesetContainerRefreshListeners = new ListenerList();
		shelvesetItemRefreshListeners = new ListenerList();
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
		Bundle bundle = Platform.getBundle(PLUGIN_ID);

		ImageDescriptor shelvesetIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/shelveset.png"), null));
		registry.put(SHELVESET_ICON_ID, shelvesetIconImage);

		ImageDescriptor inactiveShelvesetIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/inactive-shelveset.png"),
				null));
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

		ImageDescriptor unassignedUserCategoryImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(
				"icons/unassigned-user-category.png"), null));
		registry.put(UNASSIGNED_SHELVESET_USER_CATEGORY_ICON_ID, unassignedUserCategoryImage);

		ImageDescriptor pendingReviewUserCategoryImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path(
				"icons/pendingreview-user-category.png"), null));
		registry.put(PENDING_REVIEW_USER_CATEGORY_ICON_ID, pendingReviewUserCategoryImage);

		ImageDescriptor buildSuccessfulIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle,
				new Path("icons/build-successful-icon.png"), null));
		registry.put(BUILD_SUCCESSFUL_ICON_ID, buildSuccessfulIconImage);

		ImageDescriptor discussionIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/discussion-icon.png"), null));
		registry.put(DISCUSSION_ICON_ID, discussionIconImage);

		ImageDescriptor discussionOverlayIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle,
				new Path("icons/discussion-ovr-icon.png"), null));
		registry.put(DISCUSSION_OVR_ICON_ID, discussionOverlayIconImage);

		ImageDescriptor approvedOverlayIconImage = ImageDescriptor.createFromURL(FileLocator.find(bundle, new Path("icons/approved-ovr-icon.png"),
				null));
		registry.put(APPROVED_OVR_ICON_ID, approvedOverlayIconImage);
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		shelvesetGroupItemContainer = new ShelvesetGroupItemContainer();
		RepositoryManager repositoryManager = TFSEclipseClientPlugin.getDefault().getRepositoryManager();
		repositoryManager.addListener(new ShelvesetRepositoryManagerListener());

		AutoConnector autoConnector = AutoConnectorProvider.getAutoConnector();
		autoConnector.start();

		if (TFSUtil.getVersionControlClient() != null) {
			scheduleRefreshShelvesetGroupItems();
		}

		DiscussionAnnotator reviewCommentAnnnotator = new DiscussionAnnotator();

		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.getActiveWorkbenchWindow().getPartService().addPartListener(reviewCommentAnnnotator);
		workbench.addWindowListener(reviewCommentAnnnotator);
		repositoryManager.addListener(reviewCommentAnnnotator);
		addShelvesetItemRefreshListener(reviewCommentAnnnotator);

		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static ShelvesetReviewPlugin getDefault() {
		return plugin;
	}

	public static Image getImage(String key) {
		return getDefault().getImageRegistry().get(key);
	}

	public static void log(int severity, String message, Throwable t) {
		getDefault().getLog().log(new Status(severity, PLUGIN_ID, message, t));
	}

	public ShelvesetGroupItemContainer getShelvesetGroupItemContainer() {
		return shelvesetGroupItemContainer;
	}

	public void scheduleRefreshShelvesetGroupItems() {
		new ShelvesetGroupItemsRefreshJob().schedule();
	}

	public void refreshShelvesetGroupItems(boolean softRefresh, IProgressMonitor monitor) {
		getShelvesetGroupItemContainer().refresh(softRefresh, monitor);
	}

	public void addShelvesetItemRefreshListener(IShelvesetItemRefreshListener listener) {
		if (listener != null) {
			shelvesetItemRefreshListeners.add(listener);
		}
	}

	public void removeShelvesetItemRefreshListener(IShelvesetItemRefreshListener listener) {
		if (listener != null) {
			shelvesetItemRefreshListeners.remove(listener);
		}
	}

	public void addShelvesetContainerRefreshListener(IShelvesetContainerRefreshListener listener) {
		if (listener != null) {
			shelvesetContainerRefreshListeners.add(listener);
		}
	}

	public void removeShelvesetContainerRefreshListener(IShelvesetContainerRefreshListener listener) {
		if (listener != null) {
			shelvesetContainerRefreshListeners.remove(listener);
		}
	}

	public void fireShelvesetContainerRefreshed() {
		for (Object objListener : shelvesetContainerRefreshListeners.getListeners()) {
			if (objListener instanceof IShelvesetContainerRefreshListener) {
				IShelvesetContainerRefreshListener listener = (IShelvesetContainerRefreshListener) objListener;
				listener.onShelvesetContainerRefreshed(new ShelvesetContainerRefreshEvent());
			}
		}
	}

	public void fireShelvesetItemRefreshed(ShelvesetItem shelvesetItem) {
		for (Object objListener : shelvesetItemRefreshListeners.getListeners()) {
			if (objListener instanceof IShelvesetItemRefreshListener) {
				IShelvesetItemRefreshListener listener = (IShelvesetItemRefreshListener) objListener;
				listener.onShelvesetItemRefreshed(new ShelvesetItemRefreshEvent(shelvesetItem));
			}
		}
	}

}
