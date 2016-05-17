package com.cwctravel.plugins.shelvesetnavigator.compare;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.eclipse.compare.BufferedContent;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import com.cwctravel.plugins.shelvesetnavigator.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.specs.DownloadSpec;

public class CompareItem extends BufferedContent implements ITypedElement {
	private String downloadURL;
	private String name;

	public CompareItem(String name, String downloadURL) {
		this.name = name;
		this.downloadURL = downloadURL;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public String getType() {
		String result = ITypedElement.TEXT_TYPE;
		int lastIndexOfDot = name.lastIndexOf('.');
		if (lastIndexOfDot > 0) {
			result = name.substring(lastIndexOfDot);
		}
		return result;
	}

	@Override
	protected InputStream createStream() throws CoreException {
		DownloadSpec downloadSpec = new DownloadSpec(downloadURL);
		ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
		TFSUtil.getVersionControlClient().downloadFileToStream(downloadSpec, bAOS, true);
		return new ByteArrayInputStream(bAOS.toByteArray());
	}

}
