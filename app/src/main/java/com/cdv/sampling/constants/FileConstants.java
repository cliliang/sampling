package com.cdv.sampling.constants;

import android.os.Environment;

import java.io.File;

public final class FileConstants {

	public static final String ROOT_DIR = "LH-Sampling";
	public static final String ROOT_PATH = Environment.getExternalStorageDirectory() + File.separator + ROOT_DIR + File.separator;
	public static final String ROOT_LOG_PATH = ROOT_PATH + "logs" + File.separator;
	public static final String CRASH_LOG_PATH = ROOT_LOG_PATH + "crash/";
	public static final String ROOT_CACHE_PATH = ROOT_PATH + "c" + File.separator;
	public static final String ROOT_IMAGE_PATH = ROOT_PATH + "Image" + File.separator;
	public static final String ROOT_AUDIO_PATH = ROOT_PATH + "a" + File.separator;
	public static final String ROOT_VIDEO_PATH = ROOT_PATH + "v" + File.separator;
	public static final String ROOT_DOWNLOAD_PATH = ROOT_PATH + "download" + File.separator;
}
