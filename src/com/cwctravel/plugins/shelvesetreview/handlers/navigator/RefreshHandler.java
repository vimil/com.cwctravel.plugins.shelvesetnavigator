package com.cwctravel.plugins.shelvesetreview.handlers.navigator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;

public class RefreshHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ShelvesetReviewPlugin.getDefault().refreshShelvesetGroupItems(true);
		return null;
	}

}
