package com.validation;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

import com.utils.Configuration;

public class Validator {

	public static boolean validateConfig(Configuration config) {
		return isExists(config.getInputPath());
	}

	private static boolean isExists(String path) {
		return Files.exists(Paths.get(path), LinkOption.NOFOLLOW_LINKS);
	}

}
