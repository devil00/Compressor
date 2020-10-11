package com.utils;

import java.util.HashMap;
import java.util.Map;

public enum CompressionMethod {
	ZIP("zip");

	private String code;
	private static final Map<String, CompressionMethod> lookup = new HashMap<String, CompressionMethod>();

	static {
		for (final CompressionMethod cm : CompressionMethod.values()) {
			lookup.put(cm.getMethodCode(), cm);
		}
	}

	private CompressionMethod(String code) {
		this.code = code;
	}

	public String getMethodCode() {
		return code;
	}

	public static CompressionMethod getCompressionMethodByCompressionCode(String code) {
		return lookup.get(code);
	}
}
