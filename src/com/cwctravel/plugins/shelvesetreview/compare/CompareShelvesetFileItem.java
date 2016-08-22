package com.cwctravel.plugins.shelvesetreview.compare;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.core.runtime.CoreException;

import com.cwctravel.plugins.shelvesetreview.navigator.model.ShelvesetFileItem;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.client.common.ui.framework.image.ImageHelper;
import com.microsoft.tfs.core.clients.versioncontrol.specs.DownloadSpec;
import com.microsoft.tfs.core.util.CodePageMapping;

public class CompareShelvesetFileItem extends CompareShelvesetResourceItem implements IEncodedStreamContentAccessor {

	public CompareShelvesetFileItem(ShelvesetFileItem shelvesetFileItem, ImageHelper imageHelper) {
		super(shelvesetFileItem, imageHelper);
	}

	@Override
	public String getType() {
		String result = ITypedElement.TEXT_TYPE;
		String name = getName();
		int lastIndexOfDot = name.lastIndexOf('.');
		if (lastIndexOfDot > 0) {
			result = name.substring(lastIndexOfDot);
		}
		return result;
	}

	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof CompareShelvesetFileItem) {
			CompareShelvesetFileItem otherCompareShelvesetFileItem = (CompareShelvesetFileItem) other;
			result = getName().equals(otherCompareShelvesetFileItem.getName());
		}
		return result;
	}

	public int hashCode() {
		return getName().hashCode();
	}

	@Override
	public InputStream getContents() throws CoreException {
		byte[] contentBytes = getContentBytes();
		return new ByteArrayInputStream(contentBytes);
	}

	private byte[] getContentBytes() {
		String downloadURL = ((ShelvesetFileItem) getShelvesetResourceItem()).getShelvedDownloadURL();
		DownloadSpec downloadSpec = new DownloadSpec(downloadURL);
		ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
		TFSUtil.getVersionControlClient().downloadFileToStream(downloadSpec, bAOS, true);
		return bAOS.toByteArray();
	}

	public String getContentsAsString() throws CoreException, UnsupportedEncodingException {
		byte[] contentBytes = getContentBytes();
		return new String(contentBytes, getCharset());
	}

	@Override
	public String getCharset() throws CoreException {
		ShelvesetFileItem shelvesetFileItem = (ShelvesetFileItem) getShelvesetResourceItem();
		final int codePage = shelvesetFileItem.getEncoding();
		return CodePageMapping.getEncoding(codePage, false, false);
	}

}
