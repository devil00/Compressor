package com.decompression;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

import com.utils.CompressionMethod;
import com.utils.Configuration;
import com.utils.Constants;
import com.utils.FileNameFilterUtil;

public class ZipDecompression extends FileDecompression {

	private final static Logger logger = Logger.getLogger(ZipDecompression.class);

	@Override
	protected void doDecompress(Configuration config) {
		final File inputDir = new File(config.getInputPath());
		if (!inputDir.isDirectory()) {
			unzip(config.getOutputPath(), config.getInputPath());
		} else {
			final File[] zipFiles = inputDir.listFiles(new FileNameFilterUtil(CompressionMethod.ZIP.getMethodCode()));
			Arrays.stream(zipFiles).parallel().forEach(zipFile -> this.decompressPool.submit(() -> {
				unzip(config.getOutputPath(), zipFile);
			}));

			this.decompressPool.shutdown();
			try {
				this.decompressPool.awaitTermination(Constants.THREADS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void unzip(String targetDir, String zipFilename) {
		final Path targetDirPath = Paths.get(targetDir);
		logger.debug("unzip target: " + targetDir + " " + zipFilename);
		try (ZipFile zipFile = new ZipFile(zipFilename)) {
			zipFile.stream().parallel().forEach(e -> unzipEntry(zipFile, e, targetDirPath));
		} catch (final IOException e) {
			throw new RuntimeException("Error opening zip file '" + zipFilename + "': " + e, e);
		}
	}

	public void unzip(String targetDir, File zipFile) {
		unzip(targetDir, zipFile.getAbsolutePath());
	}

	private void unzipEntry(ZipFile zipFile, ZipEntry entry, Path targetDir) {
		try {
			final Path targetPath = targetDir.resolve(Paths.get(entry.getName()));
			logger.debug("Unzip path:  " + targetPath);
			if (Files.isDirectory(targetPath)) {
				Files.createDirectories(targetPath);
			} else {
				Files.createDirectories(targetPath.getParent());
				try (InputStream in = zipFile.getInputStream(entry)) {
					Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		} catch (final IOException e) {
			throw new RuntimeException("Error processing zip entry '" + entry.getName() + "': " + e, e);
		}
	}

}
