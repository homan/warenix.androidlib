package org.dyndns.warenix.image;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dyndns.warenix.util.AsyncTask;
import org.dyndns.warenix.util.DownloadUtil.ProgressListener;
import org.dyndns.warenix.util.SDCache;
import org.dyndns.warenix.util.TouchUtil;
import org.dyndns.warenix.util.WLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class CachedWebImage extends WebImage {
	private static final String TAG = "CachedWebImage";
	private static final String CACHE_SUFFIX = ".cached";
	static SDCache sdCache = null;

	public AsyncTask startDownloadImage(String key, String url,
			ImageView image, ProgressBar progressBar) {
		this.image = image;
		this.progressBar = progressBar;
		this.url = url;
		this.key = key;

		// url =
		// "http://upload.wikimedia.org/wikipedia/commons/7/7a/Basketball.png";

		String saveAsFilename = SDCache.hashUrl(url);
		String fullLocalFilePath = sdCache.isFileOnSD(saveAsFilename
				+ CACHE_SUFFIX);
		if (fullLocalFilePath != null) {
			Bitmap bitmap = convertFileToBitmap(fullLocalFilePath);
			image.setImageBitmap(bitmap);
			return null;
		} else {
			DownloadImageAsyncTask task = new DownloadImageAsyncTask();
			task.execute(url);
			return task;
			// org.dyndns.warenix.util.AsyncTaskUtil.executeorg.dyndns.warenix.util.AsyncTask(task);
		}

	}

	Bitmap convertFileToBitmap(String fullLocalFilePath) {
		Display display = ((WindowManager) image.getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		final int screenWidth = display.getWidth();
		final int screenHeight = display.getHeight();

		int desiredWidth = screenWidth > screenHeight ? screenWidth
				: screenHeight;

		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		bmpFactoryOptions.inPurgeable = true;
		BitmapFactory.decodeFile(fullLocalFilePath, bmpFactoryOptions);

		int srcWidth = bmpFactoryOptions.outWidth;
		int srcHeight = bmpFactoryOptions.outHeight;

		if (srcWidth == -1) {
			return null;
		}

		if (desiredWidth > srcWidth)
			desiredWidth = srcWidth;

		int inSampleSize = 1;
		while (srcWidth / 2 > desiredWidth) {
			srcWidth /= 2;
			srcHeight /= 2;
			inSampleSize *= 2;
		}
		bmpFactoryOptions.inSampleSize = inSampleSize;
		bmpFactoryOptions.inDither = true;
		bmpFactoryOptions.inJustDecodeBounds = false;
		try {
			Bitmap scaledBitmap = BitmapFactory.decodeFile(fullLocalFilePath,
					bmpFactoryOptions);
			return scaledBitmap;
		} catch (OutOfMemoryError e) {
			WLog.d(TAG, String.format("OOM! inSampleSize[%d]", inSampleSize));
			throw e;
		}
	}

	public static void setCacheDir(String cacheDir) {
		sdCache = new SDCache(cacheDir);
	}

	public static void removeCacheDir(String cacheDir) {
		sdCache.removeAllFiles();
	}

	private static void writeAsCache(Bitmap bitmap, String fullLocalFilePath) {
		if (bitmap == null || bitmap.isRecycled())
			return;

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fullLocalFilePath);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class DownloadImageAsyncTask extends
			AsyncTask<String, Integer, Bitmap> implements ProgressListener {

		String url;;
		String saveAsFilename;
		String fullLocalFilePath;

		protected void onProgressUpdate(Integer... progress) {
			if (progressBar != null) {
				progressBar.setProgress(progress[0]);
			}
		}

		protected void onPreExecute() {
			if (progressBar != null) {
				progressBar.setVisibility(View.VISIBLE);
			}
			// recycleImage();
		}

		@Override
		protected Bitmap doInBackground(String... args) {
			url = args[0];
			saveAsFilename = SDCache.hashUrl(url) + CACHE_SUFFIX;
			fullLocalFilePath = sdCache.downloadFileToSD(url, saveAsFilename,
					null);

			Bitmap bitmap = convertFileToBitmap(fullLocalFilePath);
			writeAsCache(bitmap, fullLocalFilePath);
			return bitmap;
		}

		protected void onPostExecute(Bitmap bitmap) {
			map.remove(key);
			// if (visibleMap.contains(key)) {
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			if (bitmap != null && !isCancelled()) {

				if (webImageListener != null) {
					webImageListener.onImageSet(image, bitmap);
				} else {
					image.setImageBitmap(bitmap);
					if (zoomable) {
						TouchUtil.setImageViewPinchToZoom(image);
					}
				}

			}
			// }

		}

		@Override
		public void onProgress(int read, int offset, int total) {
			int percentage = offset * 100 / total;
			publishProgress(percentage);
			progress = percentage;
		}

	}

}
