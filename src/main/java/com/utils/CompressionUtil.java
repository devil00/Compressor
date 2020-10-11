package com.utils;

import java.io.File;

import com.compression.exceptions.CompressionException;

public class CompressionUtil {

	public static long getTotalSizeInMB(String filePath) throws CompressionException {
		final File file = new File(filePath);
		if (file.exists()) {
			final long bytes = file.length();
			return bytes > 0 ? bytes / (1048 * 1048) : 0;
		}
		throw new CompressionException(String.format("File with path: %s does not exist.", filePath));
	}

}
