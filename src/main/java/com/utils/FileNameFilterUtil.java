package com.utils;

import java.io.File;
import java.io.FilenameFilter;

public class FileNameFilterUtil implements FilenameFilter {
	private final String filterByFileType;

	public FileNameFilterUtil(String filterByFileType) {
		this.filterByFileType = filterByFileType;
	}

	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(filterByFileType);
	}

}
