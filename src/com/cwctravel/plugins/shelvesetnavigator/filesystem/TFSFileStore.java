package com.cwctravel.plugins.shelvesetnavigator.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.cwctravel.plugins.shelvesetnavigator.ShelvesetNavigatorPlugin;
import com.cwctravel.plugins.shelvesetnavigator.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.specs.DownloadSpec;

public class TFSFileStore implements IFileStore {
	private IFileSystem fileSystem;

	private URI uri;
	private String path;
	private String name;
	private String hash;
	private String downloadURL;

	public TFSFileStore(IFileSystem fileSystem, URI uri) {
		this.fileSystem = fileSystem;
		this.uri = uri;
		String[] pathAndDownloadURL = TFSUtil.getPathAndDownloadURL(uri);
		path = pathAndDownloadURL[0];
		downloadURL = pathAndDownloadURL[1];
		hash = pathAndDownloadURL[2];
		String[] pathParts = path.split("/");
		name = pathParts[pathParts.length - 1];

	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public IFileInfo[] childInfos(int options, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] childNames(int options, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IFileStore[] childStores(int options, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void copy(IFileStore destination, int options, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(int options, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public IFileInfo fetchInfo() {
		return new TFSFileInfo(path);
	}

	@Override
	public IFileInfo fetchInfo(int options, IProgressMonitor monitor) throws CoreException {
		return new TFSFileInfo(path);
	}

	@Override
	public IFileStore getChild(IPath path) {
		return null;
	}

	@Override
	public IFileStore getFileStore(IPath path) {
		return null;
	}

	@Override
	public IFileStore getChild(String name) {
		return null;
	}

	@Override
	public IFileSystem getFileSystem() {
		return fileSystem;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IFileStore getParent() {
		return null;
	}

	@Override
	public boolean isParentOf(IFileStore other) {
		return false;
	}

	@Override
	public IFileStore mkdir(int options, IProgressMonitor monitor) throws CoreException {
		return null;
	}

	@Override
	public void move(IFileStore destination, int options, IProgressMonitor monitor) throws CoreException {

	}

	@Override
	public InputStream openInputStream(int options, IProgressMonitor monitor) throws CoreException {
		File downloadedFile = new File(new File(System.getProperty("java.io.tmpdir"), hash), name);
		if (!downloadedFile.isFile()) {
			DownloadSpec downloadSpec = new DownloadSpec(downloadURL);
			TFSUtil.getVersionControlClient().downloadFile(downloadSpec, downloadedFile, true);
		}
		try {
			return new FileInputStream(downloadedFile);
		} catch (FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, ShelvesetNavigatorPlugin.PLUGIN_ID, e.getMessage(), e));
		}
	}

	@Override
	public OutputStream openOutputStream(int options, IProgressMonitor monitor) throws CoreException {
		return null;
	}

	@Override
	public void putInfo(IFileInfo info, int options, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public File toLocalFile(int options, IProgressMonitor monitor) throws CoreException {
		File downloadedFile = new File(new File(System.getProperty("java.io.tmpdir"), hash), name);
		if (!downloadedFile.isFile()) {
			DownloadSpec downloadSpec = new DownloadSpec(downloadURL);
			TFSUtil.getVersionControlClient().downloadFile(downloadSpec, downloadedFile, true);
		}
		return downloadedFile;
	}

	@Override
	public URI toURI() {
		return uri;
	}

	public String toString() {
		return path;
	}
}
