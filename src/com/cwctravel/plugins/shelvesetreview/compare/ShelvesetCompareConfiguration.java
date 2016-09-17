package com.cwctravel.plugins.shelvesetreview.compare;

import org.eclipse.compare.CompareConfiguration;

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public class ShelvesetCompareConfiguration extends CompareConfiguration {

	public ImageHelper getImageHelper() {
		return ShelvesetReviewPlugin.getDefault().getImageHelper();
	}

	public void dispose() {
		super.dispose();
	}
}
