package com.cwctravel.plugins.shelvesetreview;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.AnnotationPreference;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.cwctravel.plugins.shelvesetreview.annotator.DiscussionAnnotator;
import com.cwctravel.plugins.shelvesetreview.events.ShelvesetContainerRefreshEvent;
import com.cwctravel.plugins.shelvesetreview.events.ShelvesetItemRefreshEvent;
import com.cwctravel.plugins.shelvesetreview.identity.IdentityManager;
import com.cwctravel.plugins.shelvesetreview.jobs.ShelvesetGroupItemsRefreshJob;
import com.cwctravel.plugins.shelvesetreview.listeners.IShelvesetContainerRefreshListener;
import com.cwctravel.plugins.shelvesetreview.listeners.IShelvesetItemRefreshListener;
import com.cwctravel.plugins.shelvesetreview.navigator.model.BaseItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.CodeReviewGroupItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetGroupItemContainer;
import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;
import com.cwctravel.plugins.shelvesetreview.util.CompareUtil;
import com.cwctravel.plugins.shelvesetreview.util.IconManager;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.client.common.autoconnect.AutoConnector;
import com.microsoft.tfs.client.common.autoconnect.AutoConnectorProvider;
import com.microsoft.tfs.client.common.repository.RepositoryManager;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;
import com.microsoft.tfs.client.eclipse.TFSEclipseClientPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class ShelvesetReviewPlugin extends AbstractUIPlugin {
	// The plug-in ID
	public static final String PLUGIN_ID = "com.cwctravel.plugins.shelvesetreview";

	private static ShelvesetReviewPlugin plugin;

	private ListenerList shelvesetContainerRefreshListeners;
	private ListenerList shelvesetItemRefreshListeners;

	private BaseItemContainer baseItemContainer;

	private AnnotationPreference discussionAnnotationPreference;

	private IdentityManager identityManager;

	private ImageHelper imageHelper;

	public ShelvesetReviewPlugin() {
		plugin = this;
		shelvesetContainerRefreshListeners = new ListenerList();
		shelvesetItemRefreshListeners = new ListenerList();
		identityManager = new IdentityManager();
		imageHelper = new ImageHelper();
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);
		Bundle bundle = Platform.getBundle(PLUGIN_ID);

		IconManager.loadIcons(registry, bundle);

	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		baseItemContainer = new BaseItemContainer();
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

		discussionAnnotationPreference = CompareUtil.getDiscussionAnnotationPreference();

	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		imageHelper.dispose();
	}

	public static ShelvesetReviewPlugin getDefault() {
		return plugin;
	}

	public static void log(int severity, String message, Throwable t) {
		getDefault().getLog().log(new Status(severity, PLUGIN_ID, message, t));
	}

	public BaseItemContainer getBaseItemContainer() {
		return baseItemContainer;
	}

	public ShelvesetGroupItemContainer getShelvesetGroupItemContainer() {
		return baseItemContainer.getShelvesetGroupItemContainer();
	}

	public CodeReviewGroupItemContainer getCodeReviewItemContainer() {
		return baseItemContainer.getCodeReviewGroupItemContainer();
	}

	public void scheduleRefreshShelvesetGroupItems() {
		new ShelvesetGroupItemsRefreshJob().schedule();
	}

	public void refresh(boolean softRefresh, IProgressMonitor monitor) {
		getBaseItemContainer().refresh(softRefresh, monitor);
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

	public AnnotationPreference getDiscussionAnnotationPreference() {
		return discussionAnnotationPreference;
	}

	public IdentityManager getIdentityManager() {
		return identityManager;
	}

	public ImageHelper getImageHelper() {
		return imageHelper;
	}

}
