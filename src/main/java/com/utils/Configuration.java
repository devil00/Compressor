package com.utils;

public class Configuration {

	private String inputPath;
	private String outputPath;
	private long compressionSize;

	public Configuration(String inputPath, String outputPath) {
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.compressionSize = 0;
	}

	public Configuration(String inputPath, String outputPath, long compressionSize) {
		this.inputPath = inputPath;
		this.outputPath = outputPath;
		this.compressionSize = compressionSize;
	}

	public Configuration() {
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public long getCompressionSize() {
		return compressionSize;
	}

	public void setCompressionSizeInMB(int compressionSize) {
		this.compressionSize = compressionSize;
	}
}
