package org.zz.qstruts2.upload;

import java.io.File;

public class UploadBean {

	private File[] files;
	private String[] fileNames;
	private String[] contentTypes;

	public File[] getFiles() {
		return files;
	}

	public void setFiles(File[] files) {
		this.files = files;
	}

	public String[] getFileNames() {
		return fileNames;
	}

	public void setFileNames(String[] fileNames) {
		this.fileNames = fileNames;
	}

	public String[] getContentTypes() {
		return contentTypes;
	}

	public void setContentTypes(String[] contentTypes) {
		this.contentTypes = contentTypes;
	}

}
