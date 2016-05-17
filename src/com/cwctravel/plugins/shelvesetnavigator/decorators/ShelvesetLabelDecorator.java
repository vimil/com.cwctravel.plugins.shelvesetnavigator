package com.cwctravel.plugins.shelvesetnavigator.decorators;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetnavigator.model.ShelvesetItem;
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
			} else if (changeType.contains(ChangeType.EDIT)) {
				decoration.addPrefix(">");
			}
		} else if (element instanceof ShelvesetItem) {
			ShelvesetItem shelvesetItem = (ShelvesetItem) element;
			decoration.addSuffix("[" + shelvesetItem.getOwnerName() + "]");
		}
	}

}
