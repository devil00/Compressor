package com.app;

import com.compression.FileCompression;
import com.compression.ZipCompression;
import com.compression.exceptions.CompressionException;
import com.decompression.FileDecompression;
import com.decompression.ZipDecompression;
import com.utils.CompressionMethod;
import com.utils.Configuration;

public class CompressionContext {
	private final FileCompression compressInstance;
	private final FileDecompression decompressInstance;

	public CompressionContext(CompressionMethod compMethod) {
		compressInstance = createCompressionInstance(compMethod);
		decompressInstance = createDeCompressionInstance(compMethod);

	}

	private FileCompression createCompressionInstance(CompressionMethod method) {
		switch (method) {
		case ZIP:
			return new ZipCompression();
		default:
			throw new UnsupportedOperationException("Not supported");
		}
	}

	private FileDecompression createDeCompressionInstance(CompressionMethod method) {
		switch (method) {
		case ZIP:
			return new ZipDecompression();
		default:
			throw new UnsupportedOperationException("Not supported");
		}
	}

	public void compress(Configuration config) throws CompressionException {
		this.compressInstance.compress(config);
	}

	public void decompress(Configuration config) {
		this.decompressInstance.decompress(config);
	}

}
