package com.cwctravel.plugins.shelvesetreview.propertypages;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PropertyPage;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetItem;

public class ShelvesetPropertyPage extends PropertyPage {
	private ShelvesetItem shelvesetItem;

	private Composite createDefaultComposite(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		FormLayout layout = new FormLayout();
		composite.setLayout(layout);

		return composite;
	}

	@Override
	protected Control createContents(Composite parent) {
		shelvesetItem = ((IAdaptable) getElement()).getAdapter(ShelvesetItem.class);
		Composite composite = createDefaultComposite(parent);
		return composite;
	}

	@Override
	public boolean performOk() {
		return true;
	}
}
