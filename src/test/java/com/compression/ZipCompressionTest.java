package com.compression;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.app.CompressionContext;
import com.compression.exceptions.CompressionException;
import com.utils.CompressionMethod;
import com.utils.Configuration;

public class ZipCompressionTest {

	@Test
	public void testcompressFilesSuccess() throws CompressionException {
		final Configuration config = createConfig();
		final CompressionContext context = new CompressionContext(CompressionMethod.ZIP);
		context.compress(config);
		final File file = new File(config.getOutputPath());
		assertTrue(file.exists());
		assertTrue(file.length() > 0);
	}

	@Test
	public void testcDecompressFilesSuccess() {
		final Configuration config = createConfig();
		config.setInputPath("/Users/mayursw/Downloads/out");
		final CompressionContext context = new CompressionContext(CompressionMethod.ZIP);
		context.decompress(config);
		final File out = new File(config.getOutputPath());
		final File in = new File(config.getInputPath());
		assertTrue(out.exists());
	}

	@Test
	public void testcompressDirSuccess() {
		final Configuration config = createConfig();
		final CompressionContext context = new CompressionContext(CompressionMethod.ZIP);
		final File file = new File(config.getOutputPath());
		assertTrue(file.exists());

		final File[] inFiles = new File(config.getInputPath()).listFiles();
		final Set<File> actualDirCompressed = new HashSet<File>();

		for (final File fl : inFiles) {
			if (fl.isDirectory()) {
				actualDirCompressed.add(fl);
			}
		}

		config.setInputPath("/Users/mayursw/Downloads/out");
		context.decompress(config);

		final Set<File> expectedDirCompressed = new HashSet<File>();

		for (final File fl : inFiles) {
			if (fl.isDirectory()) {
				expectedDirCompressed.add(fl);
			}
		}

		expectedDirCompressed.removeAll(expectedDirCompressed);

		assertTrue(expectedDirCompressed.size() == 0);

	}

	@Test(expected = CompressionException.class)
	public void testCompressThrowsException_WhenNoFiles() throws CompressionException {
		final Configuration config = createConfig();
		config.setInputPath("/Users/mayursw/Downloads/test3");
		final CompressionContext context = new CompressionContext(CompressionMethod.ZIP);
		context.compress(config);
	}

	private Configuration createConfig() {
		final Configuration config = new Configuration();
		config.setInputPath("/Users/mayursw/Downloads/test");
		config.setOutputPath("/Users/mayursw/Downloads/out");
		config.setCompressionSizeInMB(1024);
		return config;
	}

}
