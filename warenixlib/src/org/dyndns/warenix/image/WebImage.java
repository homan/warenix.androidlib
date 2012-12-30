package org.dyndns.warenix.image;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

import org.dyndns.warenix.util.AsyncTask;
import org.dyndns.warenix.util.DownloadUtil;
import org.dyndns.warenix.util.DownloadUtil.ProgressListener;
import org.dyndns.warenix.util.TouchUtil;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

//ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
//ImageView image = (ImageView) findViewById(R.id.image);
//
//WebImage webImage = new WebImage();
//webImage.setImageZoomable(true);
//webImage.startDownloadImage(
//		"http://anniestaninec.files.wordpress.com/2011/01/astaninec_horiz_high_res.jpg",
//		image, progressBar);

public class WebImage {
	public static final String LOG_TAG = WebImage.class.getSimpleName();

	/**
	 * if zoomable, the imageview will response to touch gesture
	 */
	protected boolean zoomable;

	public void setImageZoomable(boolean zoomable) {
		this.zoomable = zoomable;
	}

	public void setWebImageListener(WebImageListener webImageListener) {
		this.webImageListener = webImageListener;
	}

	protected ImageView image;
	protected ProgressBar progressBar;

	protected WebImageListener webImageListener;

	protected String url = "";

	DownloadImageTask task;

	protected String key;

	protected int progress;

	public int getProgress() {
		return progress;
	}

	public static HashSet<String> map = new HashSet<String>();
	public static HashSet<String> visibleMap = new HashSet<String>();

	public AsyncTask startDownloadImage(String key, String url,
			ImageView image, ProgressBar progressBar) {
		this.image = image;
		this.progressBar = progressBar;
		this.url = url;
		this.key = key;

		task = new DownloadImageTask();
		task.execute(url);
		return task;
	}

	class DownloadImageTask extends AsyncTask<String, Integer, Bitmap>
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
			try {

				map.add(key);
				Display display = ((WindowManager) image.getContext()
						.getSystemService(Context.WINDOW_SERVICE))
						.getDefaultDisplay();
				final int screenWidth = display.getWidth();
				final int screenHeight = display.getHeight();

				int desiredWidth = screenWidth > screenHeight ? screenWidth
						: screenHeight;
				String url = args[0];

				Bitmap bitmap = DownloadUtil.downloadImageBitmap(new URL(url),
						this, desiredWidth);
				return bitmap;

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Bitmap bitmap) {
			map.remove(key);
			// if (visibleMap.contains(key)) {
			if (progressBar != null) {
				progressBar.setVisibility(View.INVISIBLE);
			}
			if (bitmap != null) {
				Log.d("WebImage", "finished download, setting image view");
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

	public interface WebImageListener {
		public void onImageSet(ImageView image);

		public void onImageSet(ImageView image, Bitmap bitmap);
	}

	public class MatrixHelper {
		public Matrix calculateScaleAndTranslateMatrix(float paddingTop,
				float paddingLeft) {
			int imageWidth = image.getDrawable().getIntrinsicWidth();
			int imageHeight = image.getDrawable().getIntrinsicHeight();

			Log.d("warenix", String.format("image: width %d height %d",
					imageWidth, imageHeight));

			int frameWidth = convertDipToPx(230);
			int frameHeight = convertDipToPx(230);
			Log.d("warenix", String.format("frame width: %d", frameWidth));

			float scaleWidth = frameWidth * 1.0f / imageWidth;
			if (scaleWidth > 1.0f) {
				scaleWidth = 1.0f;
			}

			float scaleHeight = frameHeight * 1.0f / imageHeight;
			if (scaleHeight > 1.0f) {
				scaleHeight = 1.0f;
			}

			float scale = scaleHeight > scaleWidth ? scaleWidth : scaleHeight;

			Log.d("warenix", String.format(
					"scale: width %f height %f chosen %f", scaleWidth,
					scaleHeight, scale));

			float scaledImageWidth = imageWidth * scale;
			float scaledImageHeight = imageHeight * scale;
			Log.d("warenix", String.format("scaled: width %f height %f",
					scaledImageWidth, scaledImageHeight));

			Matrix matrix = new Matrix();
			float dx = (frameWidth - scaledImageWidth) / 2 + paddingLeft;
			float dy = (frameHeight - scaledImageHeight) / 2 + paddingTop;
			Log.d("warenix",
					String.format(
							"translate to center: x %f y %f paddingLeft %f paddingTop %f",
							dx, dy, paddingLeft, paddingTop));
			matrix.setScale(scale, scale);
			matrix.postTranslate(dx, dy);

			return matrix;
		}

		public int convertDipToPx(int dip) {

			Resources r = image.getResources();
			float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					dip, r.getDisplayMetrics());
			return (int) px;
		}
	}

	public String getURL() {
		return url;
	}

	public void stop() {
		if (task != null) {
			Log.d("warenix", "stop download image task");
			task.cancel(true);
		}
	}

}
