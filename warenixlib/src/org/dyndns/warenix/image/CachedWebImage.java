package org.dyndns.warenix.image;

import org.dyndns.warenix.util.DownloadUtil.ProgressListener;
import org.dyndns.warenix.util.SDCache;
import org.dyndns.warenix.util.TouchUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class CachedWebImage extends WebImage {

	static SDCache sdCache = null;

	public void startDownloadImage(String key, String url, ImageView image,
			ProgressBar progressBar) {
		this.image = image;
		this.progressBar = progressBar;
		this.url = url;
		this.key = key;

		// url =
		// "http://upload.wikimedia.org/wikipedia/commons/7/7a/Basketball.png";

		String saveAsFilename = SDCache.hashUrl(url);
		String fullLocalFilePath = sdCache.isFileOnSD(saveAsFilename);
		if (fullLocalFilePath != null) {
			Bitmap bitmap = convertFileToBitmap(fullLocalFilePath);
			image.setImageBitmap(bitmap);

		} else {
			DownloadImageAsyncTask task = new DownloadImageAsyncTask();
			task.execute(url);
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
		bmpFactoryOptions.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(fullLocalFilePath, bmpFactoryOptions);
	}

	public static void setCacheDir(String cacheDir) {
		sdCache = new SDCache(cacheDir);
	}

	public static void removeCacheDir(String cacheDir) {
		sdCache.removeAllFiles();
	}

	class DownloadImageAsyncTask extends AsyncTask<String, Integer, Bitmap>
			implements ProgressListener {

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
			String url = args[0];
			String saveAsFilename = SDCache.hashUrl(url);
			String fullLocalFilePath = sdCache.downloadFileToSD(url,
					saveAsFilename, null);

			return convertFileToBitmap(fullLocalFilePath);
		}

		protected void onPostExecute(Bitmap bitmap) {
			map.remove(key);
			// if (visibleMap.contains(key)) {
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			if (bitmap != null) {

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
