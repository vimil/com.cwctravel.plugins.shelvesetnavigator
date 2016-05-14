package com.cwctravel.plugins.shelvesetnavigator;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.microsoft.tfs.client.common.autoconnect.AutoConnectorProvider;
import com.microsoft.tfs.client.common.repository.RepositoryManagerEvent;
import com.microsoft.tfs.client.common.repository.RepositoryManagerListener;
import com.microsoft.tfs.client.eclipse.TFSEclipseClientPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class ShelvesetNavigatorPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.cwctravel.plugins.shelvesetnavigator";

	public static final String SHELVESET_ICON_ID = "com.cwctravel.eclipse.plugins.shelvesetnavigator.icons.shelveset";

	private static ShelvesetNavigatorPlugin plugin;

	/**
	 * The constructor
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		TFSEclipseClientPlugin.getDefault().getRepositoryManager().addListener(new RepositoryManagerListener() {

			@Override
			public void onRepositoryRemoved(RepositoryManagerEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onRepositoryAdded(RepositoryManagerEvent event) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDefaultRepositoryChanged(RepositoryManagerEvent event) {
				UIJob job = new UIJob("Refreshing Shelvesets") {
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) {
						IWorkbenchWindow[] workbenchWIndows = PlatformUI.getWorkbench().getWorkbenchWindows();
						if (workbenchWIndows != null) {
							for (IWorkbenchWindow workbenchWIndow : workbenchWIndows) {
								IWorkbenchPage[] workbenchPages = workbenchWIndow.getPages();
								if (workbenchPages != null) {
									for (IWorkbenchPage workbenchPage : workbenchPages) {
										IViewReference[] viewReferences = workbenchPage.getViewReferences();
										if (viewReferences != null) {
											for (IViewReference viewReference : viewReferences) {
												IViewPart viewPart = viewReference.getView(false);
												if (viewPart instanceof ShelvesetNavigator) {
													ShelvesetNavigator shelvesetNavigator = (ShelvesetNavigator) viewPart;
													shelvesetNavigator.refresh(true);
												}
											}
										}
									}
								}
							}
						}
						return Status.OK_STATUS;
					}
				};

				job.schedule();

			}
		});
		AutoConnectorProvider.getAutoConnector().start();
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ShelvesetNavigatorPlugin getDefault() {
		return plugin;
	}

}
