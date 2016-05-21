package com.cwctravel.plugins.shelvesetreview;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.cwctravel.plugins.shelvesetreview.jobs.ShelvesetGroupItemsRefreshJob;
import com.cwctravel.plugins.shelvesetreview.navigator.ShelvesetNavigatorRefresher;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItemContainer;
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

	private static ShelvesetReviewPlugin plugin;

	private ShelvesetGroupItemContainer shelvesetGroupItemContainer;

	public ShelvesetReviewPlugin() {
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
		Bundle bundle = Platform.getBundle(PLUGIN_ID);

		ImageDescriptor shelvesetIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/shelveset.png"), null));
		registry.put(SHELVESET_ICON_ID, shelvesetIconImage);

		ImageDescriptor inactiveShelvesetIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/inactive-shelveset.png"), null));
		registry.put(INACTIVE_SHELVESET_ICON_ID, inactiveShelvesetIconImage);

		ImageDescriptor userGroupIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/user-group.png"), null));
		registry.put(USER_GROUP_ICON_ID, userGroupIconImage);

		ImageDescriptor reviewGroupIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/review-group.png"), null));
		registry.put(REVIEW_GROUP_ICON_ID, reviewGroupIconImage);

		ImageDescriptor inactiveGroupIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/inactive-group.png"), null));
		registry.put(INACTIVE_GROUP_ICON_ID, inactiveGroupIconImage);

		ImageDescriptor userIconImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/user.png"), null));
		registry.put(USER_ICON_ID, userIconImage);
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		shelvesetGroupItemContainer = new ShelvesetGroupItemContainer();
		RepositoryManager repositoryManager = TFSEclipseClientPlugin.getDefault().getRepositoryManager();
		repositoryManager.addListener(new ShelvesetNavigatorRefresher());
		AutoConnector autoConnector = AutoConnectorProvider.getAutoConnector();
		autoConnector.start();
		if (TFSUtil.getVersionControlClient() != null) {
			refreshShelvesetGroupItems(true);
		}
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static ShelvesetReviewPlugin getDefault() {
		return plugin;
	}

	public static void log(int severity, String message, Throwable t) {
		getDefault().getLog().log(new Status(severity, PLUGIN_ID, message, t));
	}

	public ShelvesetGroupItemContainer getShelvesetGroupItemContainer() {
		return shelvesetGroupItemContainer;
	}

	public void refreshShelvesetGroupItems(boolean refreshNavigator) {
		new ShelvesetGroupItemsRefreshJob(refreshNavigator).schedule();
	}
}
