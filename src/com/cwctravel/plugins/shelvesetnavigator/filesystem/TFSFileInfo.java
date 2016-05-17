package com.cwctravel.plugins.shelvesetnavigator.filesystem;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;

public class TFSFileInfo implements IFileInfo {
	private String name;

	public TFSFileInfo(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public int getError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getAttribute(int attribute) {
		if(EFS.ATTRIBUTE_READ_ONLY == attribute) {
			return true;
		}
		return false;
	}

	@Override
	public String getStringAttribute(int attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getLastModified() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public void setAttribute(int attribute, boolean value) {

	}

	@Override
	public void setLastModified(long time) {

	}

}
