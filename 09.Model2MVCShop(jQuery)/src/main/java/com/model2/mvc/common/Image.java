package com.model2.mvc.common;

public class Image {
	
	private String fileKey;
	private String fileName;

	public Image() {
		// TODO Auto-generated constructor stub
	}

	public String getFileKey() {
		return fileKey; 
	}

	public void setFileKey(String fileKey) {
		this.fileKey = fileKey;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "Image [fileKey=" + fileKey + ", fileName=" + fileName + "]";
	}

}
