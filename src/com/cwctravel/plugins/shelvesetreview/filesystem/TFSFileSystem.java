package com.cwctravel.plugins.shelvesetreview.filesystem;

import java.net.URI;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.provider.FileSystem;

public class TFSFileSystem extends FileSystem {

	@Override
	public IFileStore getStore(URI uri) {
		return new TFSFileStore(this, uri);
	}

}
