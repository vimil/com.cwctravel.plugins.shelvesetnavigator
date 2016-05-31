package com.cwctravel.plugins.shelvesetreview.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class ReviewDiscussionView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.cwctravel.plugins.shelvesetreview.views.ReviewDiscussionView";

	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	class City {
		Street[] streets = new Street[2];

		public City() {
			for (int i = 0; i < streets.length; i++)
				streets[i] = new Street(this, i);
		}

		public Street[] getStreets() {
			return streets;
		}

		public String toString() {
			return "Küchenhausen";
		}
	}

	class Street {
		City city;
		House[] houses = new House[2];
		int indx;

		public Street(City city, int index) {
			this.city = city;
			indx = index + 1;
			for (int i = 0; i < houses.length; i++)
				houses[i] = new House(this, i);
		}

		public House[] getHouses() {
			return houses;
		}

		public String toString() {
			return "Topfdeckelstraße " + indx;
		}
	}

	class House {
		Street street;
		int indx;

		public House(Street street, int i) {
			this.street = street;
			indx = i + 1;
		}

		public String toString() {
			return "Haus " + indx;
		}

		public String getPerson() {
			if (street.toString().equals("Topfdeckelstraße 1")) {
				if (indx == 1)
					return "Hugo Hüpfer";
				return "Sabine Springer";
			}
			if (indx == 1)
				return "Leo Löffel";
			return "Marta Messer";
		}

		public String getSex() {
			if (indx == 1)
				return "m";
			return "w";
		}
	}

	class AddressContentProvider implements ITreeContentProvider {
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof List)
				return ((List<?>) parentElement).toArray();
			if (parentElement instanceof City)
				return ((City) parentElement).getStreets();
			if (parentElement instanceof Street)
				return ((Street) parentElement).getHouses();
			return new Object[0];
		}

		public Object getParent(Object element) {
			if (element instanceof Street)
				return ((Street) element).city;
			if (element instanceof House)
				return ((House) element).street;
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof List)
				return ((List<?>) element).size() > 0;
			if (element instanceof City)
				return ((City) element).getStreets().length > 0;
			if (element instanceof Street)
				return ((Street) element).getHouses().length > 0;
			return false;
		}

		public Object[] getElements(Object cities) {
			// cities ist das, was oben in setInput(..) gesetzt wurde.
			return getChildren(cities);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	class TableLabelProvider implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			switch (columnIndex) {
				case 0:
					return element.toString();
				case 1:
					if (element instanceof House)
						return ((House) element).getPerson();
				case 2:
					if (element instanceof House)
						return ((House) element).getSex();
			}
			return null;
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
	}

	/**
	 * The constructor.
	 */
	public ReviewDiscussionView() {
	}

	/*
	 * private ShelvesetItem getSelectedShelvesetItem() { ShelvesetItem result =
	 * null; IWorkbenchWindow[] workbenchWIndows =
	 * PlatformUI.getWorkbench().getWorkbenchWindows(); if (workbenchWIndows !=
	 * null) { for (IWorkbenchWindow workbenchWIndow : workbenchWIndows) {
	 * IWorkbenchPage[] workbenchPages = workbenchWIndow.getPages(); if
	 * (workbenchPages != null) { for (IWorkbenchPage workbenchPage :
	 * workbenchPages) { IViewReference[] viewReferences =
	 * workbenchPage.getViewReferences(); if (viewReferences != null) { for
	 * (IViewReference viewReference : viewReferences) { IViewPart viewPart =
	 * viewReference.getView(false); if (viewPart instanceof ShelvesetNavigator)
	 * { ShelvesetNavigator shelvesetNavigator = (ShelvesetNavigator) viewPart;
	 * ITreeSelection treeSelection =
	 * shelvesetNavigator.getCommonViewer().getStructuredSelection(); if
	 * (treeSelection != null) { Object firstElement =
	 * treeSelection.getFirstElement(); if (firstElement instanceof
	 * ShelvesetItem) { result = (ShelvesetItem) firstElement; } } } } } } } } }
	 * return result; }
	 */

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		Tree reviewDiscussionTree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		reviewDiscussionTree.setHeaderVisible(true);

		viewer = new TreeViewer(reviewDiscussionTree);

		TreeColumn column1 = new TreeColumn(reviewDiscussionTree, SWT.LEFT);
		reviewDiscussionTree.setLinesVisible(true);
		column1.setAlignment(SWT.LEFT);
		column1.setText("Land/Stadt");
		column1.setWidth(160);
		TreeColumn column2 = new TreeColumn(reviewDiscussionTree, SWT.RIGHT);
		column2.setAlignment(SWT.LEFT);
		column2.setText("Person");
		column2.setWidth(100);
		TreeColumn column3 = new TreeColumn(reviewDiscussionTree, SWT.RIGHT);
		column3.setAlignment(SWT.LEFT);
		column3.setText("m/w");
		column3.setWidth(35);

		List<City> cities = new ArrayList<City>();
		cities.add(new City());

		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new AddressContentProvider());
		viewer.setLabelProvider(new TableLabelProvider());
		viewer.setInput(cities);
		viewer.expandAll();

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.cwctravel.plugins.shelvesetreview.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		// registerListeners();

	}

	/*
	 * public void dispose() { ShelvesetReviewPlugin.getDefault().
	 * removeShelvesetItemDiscussionRefreshListener(this); }
	 * 
	 * private void registerListeners() { ShelvesetReviewPlugin.getDefault().
	 * addShelvesetItemDiscussionRefreshListener(this);
	 * 
	 * }
	 */

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ReviewDiscussionView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				// ShelvesetItem shelvesetItem = getSelectedShelvesetItem();

			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Review Comments", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

}
