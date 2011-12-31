package org.dyndns.warenix.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DownloadUtil {
	public static final String LOG_TAG = DownloadUtil.class.getSimpleName();

	/**
	 * Download a bitmap and scale it not larger than the desiredWidth from an
	 * url and report download progress to the progressListener
	 * 
	 * @param url
	 * @param progressListener
	 * @param desiredWidth
	 * @return
	 * @throws IOException
	 */
	public static Bitmap downloadImageBitmap(URL url,
			ProgressListener progressListener, int desiredWidth)
			throws IOException {

		Bitmap result = null;

		// while (result == null) {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		int length = connection.getContentLength();
		byte[] imageData = getData(url, progressListener);
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(imageData, 0, length, bmpFactoryOptions);

		int srcWidth = bmpFactoryOptions.outWidth;
		int srcHeight = bmpFactoryOptions.outHeight;

		Log.d(LOG_TAG, String.format("decoded bitmap width %d height %d",
				srcWidth, srcHeight));

		// int desiredWidth = 480;
		if (desiredWidth > srcWidth)
			desiredWidth = srcWidth;

		int inSampleSize = 1;
		while (srcWidth / 2 > desiredWidth) {
			srcWidth /= 2;
			srcHeight /= 2;
			inSampleSize *= 2;
		}

		Log.d(LOG_TAG, String.format(
				"decode bitmap with inSampleSize %d desiredWidth %d",
				inSampleSize, desiredWidth));

		bmpFactoryOptions.inSampleSize = inSampleSize;
		bmpFactoryOptions.inJustDecodeBounds = false;
		result = BitmapFactory.decodeByteArray(imageData, 0, length,
				bmpFactoryOptions);
		connection.disconnect();
		// }
		return result;
		// When finished, return the resulting Bitmap, this will cause
		// the
		// Activity to call onPostExecute()
	}

	public interface ProgressListener {

		public void onProgress(int read, int offset, int total);

	}

	public static byte[] getData(URL url, ProgressListener listener)
			throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		int length = connection.getContentLength();

		byte[] data = null;
		if (length != -1) {
			InputStream is = (InputStream) url.getContent();
			data = new byte[length];

			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			int percentage = 0;
			while (offset < length
					&& (numRead = is.read(data, offset, length - offset)) >= 0) {
				offset += numRead;

				if (listener != null) {
					percentage = (int) (Math.floor(100.0 * offset
							/ (double) length));
					listener.onProgress(percentage, offset, length);
				}
			}
		} else {
			data = getAsByteArray(url, listener);
		}
		return data;
	}

	public static byte[] getAsByteArray(URL url, ProgressListener listener)
			throws IOException {
		URLConnection connection = url.openConnection();
		// Since you get a URLConnection, use it to get the InputStream
		InputStream in = connection.getInputStream();
		// Now that the InputStream is open, get the content length
		int contentLength = connection.getContentLength();

		// To avoid having to resize the array over and over and over as
		// bytes are written to the array, provide an accurate estimate of
		// the ultimate size of the byte array
		ByteArrayOutputStream tmpOut;
		if (contentLength != -1) {
			tmpOut = new ByteArrayOutputStream(contentLength);
		} else {
			tmpOut = new ByteArrayOutputStream(16384); // Pick some appropriate
														// size
		}

		byte[] buf = new byte[512];
		while (true) {
			int len = in.read(buf);
			if (len == -1) {
				break;
			}
			tmpOut.write(buf, 0, len);
		}
		in.close();
		tmpOut.close(); // No effect, but good to do anyway to keep the metaphor
						// alive

		if (listener != null) {
			listener.onProgress(100, buf.length, buf.length);
		}

		byte[] array = tmpOut.toByteArray();

		// Lines below used to test if file is corrupt
		// FileOutputStream fos = new FileOutputStream("C:\\abc.pdf");
		// fos.write(array);
		// fos.close();

		return array;
	}

}
