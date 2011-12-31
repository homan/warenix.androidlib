package org.dyndns.warenix.util;

import java.io.File;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

public class ImageUtil {
	static final String LOG_TAG = "ImageUtil";

	public static boolean recycleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			Log.d(LOG_TAG, "recycled image");
			return true;
		}

		return false;
	}

	public static boolean recycleImageView(ImageView imageView) {
		Drawable d = imageView.getDrawable();
		if (d instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
			if (recycleBitmap(bitmap)) {
				imageView.setImageBitmap(null);
				return true;
			}
		}
		return false;
	}

	public static Bitmap getBitmapFromImageView(ImageView imageView) {
		Drawable d = imageView.getDrawable();
		if (d instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
			return bitmap;
		}
		return null;
	}

	public static final int FIELD_IMAGE_FILE = 0;
	public static final int FIELD_IMAGE_FILE_SIZE = 1;
	public static final int FIELD_IMAGE_FILE_PATH = 2;
	public static final int FIELD_IMAGE_FILE_CONTENT_TYPE = 3;

	public static Object[] convertUriToFullPath(Context context,
			Uri selectedImage) {

		ContentResolver cr = context.getContentResolver();
		Cursor cur = cr.query(selectedImage, new String[] {
				android.provider.MediaStore.Images.ImageColumns.DATA,
				android.provider.MediaStore.Images.ImageColumns.MIME_TYPE },
				null, null, null);
		cur.moveToFirst();
		String filePath = cur.getString(0);
		String contentType = cur.getString(1);

		File image = new File(filePath);
		long fileSize = image.length();
		Log.d("lab", String.format("file path: %s content-type: %s size: %d",
				filePath, contentType, fileSize));

		return new Object[] { image, fileSize, filePath, contentType };
	}
}