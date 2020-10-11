package com.compression;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

import com.compression.exceptions.CompressionException;
import com.utils.CompressionUtil;
import com.utils.Constants;

/**
 *
 * Multi-threaded zip compression.
 *
 * @author mayursw
 *
 */

public class ZipCompression extends FileCompression {
	private final static Logger logger = Logger.getLogger(ZipCompression.class);

	private final ExecutorService zipPool;

	public ZipCompression() {
		zipPool = Executors.newWorkStealingPool();
	}

	@Override
	protected void doCompress(Set<String> filePaths, String outputDir, long compressionSize) {
		final Set<Set<String>> filePathsSet = createZipSplitsBySize(filePaths, compressionSize);

		final AtomicInteger splits = new AtomicInteger();

		for (final Set<String> paths : filePathsSet) {
			/*
			 * zipPool.submit(() -> { System.out.println("count: " + paths); zipFiles(paths,
			 * "result_" + splits.getAndIncrement(), outputDir); });
			 */
			logger.debug("Compressing paths : " + filePaths);
			final ZipThread zThread = new ZipThread(paths, outputDir, "result_" + splits.getAndIncrement());
			zipPool.submit(zThread);
		}

		zipPool.shutdown();

		try {
			zipPool.awaitTermination(Constants.THREADS_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

	}

	private Set<Set<String>> createZipSplitsBySize(Set<String> filePaths, long compressionSize) {
		final Set<Set<String>> filePathsSet = new HashSet<Set<String>>();

		long currentFileSize = 0;

		Set<String> filePathsSplit = new HashSet<String>();

		for (final String filePath : filePaths) {
			try {
				currentFileSize += CompressionUtil.getTotalSizeInMB(filePath);
			} catch (final CompressionException e) {
			}

			if (currentFileSize >= compressionSize) {
				logger.debug("Created split for filespath: " + filePathsSplit);
				filePathsSet.add(filePathsSplit);
				filePathsSplit = new HashSet<String>();
				currentFileSize = 0;
			}

			filePathsSplit.add(filePath);
		}

		filePathsSet.add(filePathsSplit);

		return filePathsSet;
	}

	private void zip(Set<String> filePaths, String outputFilePath) {
		try {
			final FileOutputStream fos = new FileOutputStream(outputFilePath);
			final ZipOutputStream zos = new ZipOutputStream(fos);

			for (final String filePath : filePaths) {
				final File file = new File(filePath);
				if (file.isDirectory()) {
					logger.debug("Zipping directory: " + file.getAbsolutePath());
					zipDirectory(file, file.getName(), zos);
				} else {
					zipFile(file, zos);
				}
			}
			zos.close();
		} catch (final FileNotFoundException ex) {
			System.out.println("A file does not exist: " + ex);
		} catch (final IOException ex) {
			System.out.println("I/O error: " + ex);
		}
	}

	/**
	 * Adds a directory to the current zip output stream
	 *
	 * @param folder       the directory to be added
	 * @param parentFolder the path of parent directory
	 * @param zos          the current zip output stream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void zipDirectory(File folder, String parentFolder, ZipOutputStream zos)
			throws FileNotFoundException, IOException {
		for (final File file : folder.listFiles()) {
			if (file.isDirectory()) {
				zipDirectory(file, parentFolder + "/" + file.getName(), zos);
				continue;
			}
			zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
			final byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
			zos.write(bytes, 0, bytes.length);
			zos.closeEntry();

		}
	}

	/**
	 * Adds a file to the current zip output stream
	 *
	 * @param file the file to be added
	 * @param zos  the current zip output stream
	 * @throws FileNotFoundException
	 * @throws IOException,          FileNotFoundException
	 */
	private void zipFile(File file, ZipOutputStream zos) throws FileNotFoundException, IOException {
		zos.putNextEntry(new ZipEntry(file.getName()));
		final byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		zos.write(bytes, 0, bytes.length);
		zos.closeEntry();

	}

	/**
	 * This runnable create a zip file for the group of filepaths.
	 * 
	 * @author mayursw
	 *
	 */
	class ZipThread implements Runnable {
		private final Set<String> paths;
		private final String outDir;
		private final String outFile;

		public ZipThread(Set<String> paths, String outDir, String outFileName) {
			this.paths = paths;
			this.outDir = outDir;
			this.outFile = outFileName;
		}

		@Override
		public void run() {
			final File outD = new File(outDir);
			if (!outD.exists()) {
				outD.mkdir();
			}
			final String outFilePath = Paths.get(outDir, outFile + ".zip").toString();
			System.out.println("Out: " + outFilePath);

			zip(paths, outFilePath);
		}

	}

}
