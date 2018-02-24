package com.model.entity;

public class MPhoto {
	private String filename;
	private String filepath;
	private boolean flag;
	
	public MPhoto(String filepath) {
		super();
		this.filepath = filepath;
	}
	public MPhoto(String filename,String filepath) {
		super();
		this.filename = filename;
		this.filepath = filepath;
	}
	public MPhoto(String filepath, boolean flag) {
		super();
		this.filepath = filepath;
		this.flag = flag;
	}

	public MPhoto(String filename, String filepath, boolean flag) {
		super();
		this.filename = filename;
		this.filepath = filepath;
		this.flag = flag;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getFilepath() {
		return filepath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
}
