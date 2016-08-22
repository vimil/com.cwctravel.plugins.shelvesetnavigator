package com.cwctravel.plugins.shelvesetreview.compare;

import org.eclipse.compare.CompareConfiguration;

import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;

public class ShelvesetCompareConfiguration extends CompareConfiguration {
	private final ImageHelper imageHelper;

	public ShelvesetCompareConfiguration() {
		imageHelper = new ImageHelper();
	}

	public ImageHelper getImageHelper() {
		return imageHelper;
	}

	public void dispose() {
		super.dispose();
		imageHelper.dispose();
	}
}
