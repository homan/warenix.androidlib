package org.dyndns.warenix.image;

import java.io.File;
import java.io.FileOutputStream;

import org.dyndns.warenix.util.WLog;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class ImageUtil {
	private static final String TAG = "ImageUtil";

	public static File createResizedPhotoIfNeeded(String fullLocalFilePath,
			int maxWidth, int maxHeight) {
		// int maxWidth = 1024;
		// int maxHeight = 2048;

		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fullLocalFilePath, bmpFactoryOptions);

		int srcWidth = bmpFactoryOptions.outWidth;
		int srcHeight = bmpFactoryOptions.outHeight;

		WLog.d(TAG, String.format("src width[%d] src height[%d]", srcWidth,
				srcHeight));

		if (maxWidth < srcWidth || maxHeight < srcHeight) {
			float scaleWidth = ((float) srcWidth / maxWidth);
			float scaleHeight = ((float) srcHeight / maxHeight);

			float scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;
			scale = 1.0f / scale;

			// create a matrix for the manipulation
			Matrix matrix = new Matrix();
			// resize the bit map
			matrix.postScale(scale, scale);

			Bitmap bitmap = BitmapFactory.decodeFile(fullLocalFilePath);
			// recreate the new Bitmap
			Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, srcWidth,
					srcHeight, matrix, false);

			WLog.d(TAG, String.format("resized width[%d] resized height[%d]",
					resizedBitmap.getWidth(), resizedBitmap.getHeight()));

			bitmap.recycle();
			bitmap = null;

			return new File(writeBitmapToFile(resizedBitmap, fullLocalFilePath));
		}
		return new File(fullLocalFilePath);
	}

	private static String writeBitmapToFile(Bitmap bmp, String fullLocalFilePath) {
		try {
			File imageFile = new File(fullLocalFilePath);
			String fileName = imageFile.getName();
			int mid = fileName.lastIndexOf(".");
			String prefix = fileName.substring(0, mid);
			String suffix = "."
					+ fileName.substring(mid + 1, fileName.length());
			File f = File.createTempFile(prefix, suffix);
			f.deleteOnExit();

			FileOutputStream out = new FileOutputStream(f);
			if (bmp.compress(Bitmap.CompressFormat.PNG, 100, out)) {
				return f.getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
