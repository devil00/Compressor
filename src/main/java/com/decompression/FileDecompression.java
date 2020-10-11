package com.decompression;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.utils.Configuration;
import com.validation.Validator;

/**
 * Abstract class to provide support for commonly used methods for different
 * decompression techniques.
 *
 * @author mayursw
 *
 */

public abstract class FileDecompression {

	protected final ExecutorService decompressPool;

	private final static Logger logger = Logger.getLogger(FileDecompression.class);

	public FileDecompression() {
		decompressPool = Executors.newWorkStealingPool();
	}

	public void decompress(Configuration config) {
		logger.info("Validating configuration...");
		Validator.validateConfig(config);
		logger.info("Configuration looks good");

		logger.info("Starting decompression...");
		doDecompress(config);
		logger.info("Finished decompression.");

	}

	protected abstract void doDecompress(Configuration config);

}
