package com.cwctravel.plugins.shelvesetnavigator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItemContainer;
import com.microsoft.tfs.client.common.autoconnect.AutoConnectorProvider;
import com.microsoft.tfs.client.eclipse.TFSEclipseClientPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class ShelvesetNavigatorPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.cwctravel.plugins.shelvesetnavigator";

	public static final String SHELVESET_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetnavigator.icons.shelveset";

	private static ShelvesetNavigatorPlugin plugin;

	private ShelvesetItemContainer shelvesetItemContainer;

	public ShelvesetNavigatorPlugin() {
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
		Bundle bundle = Platform.getBundle(PLUGIN_ID);

		ImageDescriptor tomcatContextImage = ImageDescriptor
				.createFromURL(FileLocator.find(bundle, new Path("icons/shelveset.png"), null));
		registry.put(SHELVESET_ICON_ID, tomcatContextImage);
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		shelvesetItemContainer = new ShelvesetItemContainer();
		TFSEclipseClientPlugin.getDefault().getRepositoryManager().addListener(new ShelvesetNavigatorRefresher());
		AutoConnectorProvider.getAutoConnector().start();
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static ShelvesetNavigatorPlugin getDefault() {
		return plugin;
	}

	public static void log(int severity, String message, Throwable t) {
		getDefault().getLog().log(new Status(severity, PLUGIN_ID, message, t));
	}

	public ShelvesetItemContainer getShelvesetItemContainer() {
		return shelvesetItemContainer;
	}

	public void refreshShelvesetItems() {
		shelvesetItemContainer.refreshShelvesetItems();
	}
}
