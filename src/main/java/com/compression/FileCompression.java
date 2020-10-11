package com.compression;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.compression.exceptions.CompressionException;
import com.utils.Configuration;
import com.utils.Constants;
import com.validation.Validator;

/**
 * Encapsulates methods required for compression of directory. This is the
 * central abstract class that needs to be extended to support different
 * compression techniques *
 *
 * @author Mayur Swami
 */

public abstract class FileCompression {
	private final static Logger logger = Logger.getLogger(FileCompression.class);

	protected final ExecutorService threadPool;

	public FileCompression() {
		threadPool = Executors.newWorkStealingPool(Constants.POOL_THREADS);
	}

	protected abstract void doCompress(Set<String> filePaths, String outputDir, long compressionSize);

	public void compress(Configuration config) throws CompressionException {
		System.out.println("Validating configuration...");
		logger.info("Validating configuration...");
		final boolean result = Validator.validateConfig(config);

		if (!result) {
			throw new CompressionException("Invalid compression configuration");
		}

		logger.info("Configuration looks good");

		logger.info("Starting Compression...");
		final Set<String> filePaths = ConcurrentHashMap.newKeySet();
		extractFilesToSet(config.getInputPath(), filePaths);

		if (filePaths.size() == 0) {
			throw new CompressionException("No files found to compress.");
		}

		doCompress(filePaths, config.getOutputPath(), config.getCompressionSize());

		logger.info("Finished compression.");
		shutDownPool();

	}

	private void extractFilesToSet(String inputPath, final Set<String> filePaths) {
		final File inputFolder = new File(inputPath);
		final String[] fileNames = inputFolder.list();

		if (fileNames == null) {
			return;
		}

		for (final String filename : fileNames) {
			final String filePath = inputFolder.toPath().resolve(filename).toString();
			threadPool.execute(() -> {
				filePaths.add(filePath);
			});
		}

	}

	/**
	 * Cleanup activities related to thread-pool shutdown.
	 */
	private void shutDownPool() {
		this.threadPool.shutdown();

		try {
			this.threadPool.awaitTermination(Constants.THREADS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			logger.error("Error shutting down threadpool to extract filepaths", e);
		}
	}

}
