package org.dyndns.warenix.util;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.dyndns.warenix.util.DownloadUtil.ProgressListener;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class SDCache {

	static final String LOG_TAG = "SDCache";
	static String sdDrive = Environment.getExternalStorageDirectory()
			.getAbsolutePath();

	static int IO_BUFFER_SIZE = 1024;

	String fullLocalDirPath;

	public SDCache(String cacheDir) {
		fullLocalDirPath = String.format("%s/%s", sdDrive, cacheDir);
		createCacheDirectoryIfNeeded(fullLocalDirPath);
	}

	public String downloadFileToSD(String url, String saveAsFilename,
			ProgressListener progressListener) {

		return downloadFileToSD(url, fullLocalDirPath, saveAsFilename,
				progressListener);
	}

	public String downloadFileToSD(String url, String fullLocalDirPath,
			String saveAsFilename, ProgressListener progressListener) {

		try {
			byte[] data = DownloadUtil.getData(new URL(url), progressListener);

			String fullLocalFilePath = String.format("%s/%s", fullLocalDirPath,
					saveAsFilename);
			writeToFile(data, fullLocalFilePath);

			return fullLocalFilePath;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String isFileOnSD(String saveAsFilename) {
		String fullLocalFilePath = String.format("%s/%s", fullLocalDirPath,
				saveAsFilename);
		if (isExistInSaveInDir(fullLocalFilePath) != null) {
			return fullLocalFilePath;
		}

		return null;
	}

	boolean createCacheDirectoryIfNeeded(String fullLocalDirPath) {
		boolean success = (new File(fullLocalDirPath)).mkdirs();
		if (success) {
			Log.i(LOG_TAG, String.format("created dir[%s]", fullLocalDirPath));
		}

		return success;
	}

	void writeToFile(byte[] data, String fullLocalFilePath) throws IOException {
		FileOutputStream fos = new FileOutputStream(fullLocalFilePath);
		BufferedOutputStream bfs = new BufferedOutputStream(fos, IO_BUFFER_SIZE);
		bfs.write(data, 0, data.length);
		bfs.flush();

		closeStream(bfs);
	}

	private void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				// android.util.Log.e(LOG_TAG, "Could not close stream", e);
			}
		}
	}

	/**
	 * check if the file exists or not
	 * 
	 * @param saveInDir
	 * @param saveAsFilename
	 * @return full local dir path
	 */
	static String isExistInSaveInDir(String fullLocalDirPath) {
		File localFile = new File(fullLocalDirPath);
		// FIXME file size less than 700 is considered not yet finished download
		if (localFile.exists() && localFile.length() > 700) {
			return fullLocalDirPath;
		}
		return null;
	}

	public static String hashUrl(String url) {
		int lastDotIndex = url.lastIndexOf('.');
		// String ext = url.substring(lastDotIndex, lastDotIndex + 4);
		url = Uri.encode(url);
		int to = url.length() - 1;
		int from = url.length() > 6 ? to - 6 : 0;
		String prefix = url.substring(from, to);

		String finalFilename = (prefix.hashCode() + "_" + url.hashCode() + ".png");
		Log.d("SDCache", String.format("url[%s] to [%s]", url, finalFilename));
		return finalFilename;
	}

	public void removeAllFiles() {
		File dir = new File(fullLocalDirPath);
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				new File(dir, children[i]).delete();
			}
		}
	}
}
