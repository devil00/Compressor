package com.app;

import com.utils.CompressionMethod;
import com.utils.Configuration;

public class Application {

	private static final int MAX_ALEXA_VOL_LEVEL = 100;
	private static final String PREFIX_URI = "/gp/help/customer/display.html?nodeId=%s&amp;pop-up=1&amp;header=0";

	public static void main(String[] args) {

		final int a = 10;
		final int b = 39;
		final int c = 40;

		final int r = Math.round((a * b) / c);
		/*
		 * for (int i = 11; i <= 30; i++) { final int alexaVol =
		 * convertDeviceVolumeToAlexaScale(i, 15); final int deviceVol =
		 * convertAlexaVolumeToDeviceScale(alexaVol, 15); System.out.println(alexaVol +
		 * "----" + deviceVol); }
		 */

		/*
		 * final int alexaVol = convertDeviceVolumeToAlexaScale(2, 15); final int
		 * deviceVol = convertAlexaVolumeToDeviceScale(alexaVol + 10, 15); final int
		 * alexaVol1 = convertDeviceVolumeToAlexaScale(deviceVol, 15);
		 * System.out.println(alexaVol + "----" + deviceVol + "---" + alexaVol1);
		 */
		final int mx = 15;
		final int alexa = convertDeviceVolumeToAlexaScale(14, mx);
		System.out.println("alexa " + alexa);
		final int adjust = ((10 * 40) / mx);
		final int device = convertAlexaVolumeToDeviceScale(alexa + adjust, mx);
		System.out.println("device " + device);
		System.out.println(Math.round(1.5));

	}

	public static int convertAlexaVolumeToDeviceScale(int alexaVolume, int mainStreamMax) {
		final int deviceVolumeScaled = Math.round(((float) alexaVolume * (float) mainStreamMax) / MAX_ALEXA_VOL_LEVEL);
		System.out.println("Converting Alexa Vol : " + alexaVolume + " to Device Vol : " + deviceVolumeScaled
				+ " using DefaultConversion.");
		return deviceVolumeScaled;
	}

	public static int convertDeviceVolumeToAlexaScale(int deviceVolume, int mainStreamMax) {
		final int alexaVolumeScaled = Math.round((deviceVolume * MAX_ALEXA_VOL_LEVEL) / mainStreamMax);
		System.out.println("Converting Device Vol : " + deviceVolume + " to Alexa Vol : " + alexaVolumeScaled
				+ " using DefaultConversion.");
		return alexaVolumeScaled;
	}

	public static void executeCom() {
		final Configuration config = new Configuration();
		config.setInputPath("/Users/mayursw/Downloads/test");
		config.setOutputPath("/Users/mayursw/Downloads/out");
		config.setCompressionSizeInMB(1024);
		final CompressionContext context = new CompressionContext(CompressionMethod.ZIP);
		try {
			context.compress(config);
			config.setInputPath("/Users/mayursw/Downloads/out");
			context.decompress(config);
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

}
