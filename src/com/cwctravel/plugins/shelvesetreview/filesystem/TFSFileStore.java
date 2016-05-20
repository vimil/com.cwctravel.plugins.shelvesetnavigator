package com.cwctravel.plugins.shelvesetreview.filesystem;

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

import com.cwctravel.plugins.shelvesetreview.ShelvesetReviewPlugin;
import com.cwctravel.plugins.shelvesetreview.util.TFSUtil;
import com.microsoft.tfs.core.clients.versioncontrol.specs.DownloadSpec;

public class TFSFileStore implements IFileStore {
	private IFileSystem fileSystem;

	private final URI uri;
	private final String path;
	private final String name;
	private final String hash;
	private final String shelvesetName;
	private final String shelvesetOwnerName;
	private final String downloadURL;

	public TFSFileStore(IFileSystem fileSystem, URI uri) {
		this.fileSystem = fileSystem;
		this.uri = uri;
		String[] uriParts = TFSUtil.decodeURI(uri);
		path = uriParts[0];
		downloadURL = uriParts[1];
		hash = uriParts[2];
		shelvesetName = uriParts[3];
		shelvesetOwnerName = uriParts[4];

		String[] pathParts = path.split("/");
		name = pathParts[pathParts.length - 1];

	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
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
		if(!downloadedFile.isFile()) {
			DownloadSpec downloadSpec = new DownloadSpec(downloadURL);
			TFSUtil.getVersionControlClient().downloadFile(downloadSpec, downloadedFile, true);
		}
		try {
			return new FileInputStream(downloadedFile);
		}
		catch(FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, ShelvesetReviewPlugin.PLUGIN_ID, e.getMessage(), e));
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
		if(!downloadedFile.isFile()) {
			DownloadSpec downloadSpec = new DownloadSpec(downloadURL);
			TFSUtil.getVersionControlClient().downloadFile(downloadSpec, downloadedFile, true);
		}
		return downloadedFile;
	}

	public String getPath() {
		return path;
	}

	public String getShelvesetName() {
		return shelvesetName;
	}

	public String getShelvesetOwnerName() {
		return shelvesetOwnerName;
	}

	@Override
	public URI toURI() {
		return uri;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getPath().hashCode();
		result = prime * result + getShelvesetName().hashCode();
		result = prime * result + getShelvesetOwnerName().hashCode();

		return result;
	}

	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		TFSFileStore other = (TFSFileStore)obj;
		if(other.getPath().equals(getPath()) && other.getShelvesetName().equals(getShelvesetName()) && other.getShelvesetOwnerName().equals(getShelvesetOwnerName())) {
			return true;
		}
		return false;
	}

	public String toString() {
		return path;
	}
}
