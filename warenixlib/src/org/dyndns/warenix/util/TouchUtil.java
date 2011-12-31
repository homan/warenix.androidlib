package org.dyndns.warenix.util;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class TouchUtil {
	public static final String LOG_TAG = TouchUtil.class.getSimpleName();

	/**
	 * Set an imageView response to pinch to zoom and dragging.
	 * 
	 * @param image
	 */
	public static void setImageViewPinchToZoom(final ImageView image) {
		Display display = ((WindowManager) image.getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		final int screenWidth = display.getWidth();
		final int screenHeight = display.getHeight();
		final int originalImageWidth = image.getDrawable().getIntrinsicWidth();
		final int originalImageHeight = image.getDrawable()
				.getIntrinsicHeight();
		final int hiddenWidth = originalImageWidth - screenWidth;

		final Matrix matrix = new Matrix();
		final Matrix savedMatrix = new Matrix();

		image.setScaleType(ScaleType.CENTER_INSIDE);

		matrix.set(image.getImageMatrix());

		image.setImageMatrix(matrix);
		image.setOnTouchListener(new OnTouchListener() {

			// We can be in one of these 3 states
			static final int NONE = 0;
			static final int DRAG = 1;
			static final int ZOOM = 2;
			int mode = NONE;

			PointF start = new PointF();
			PointF mid = new PointF();

			float oldDist;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					savedMatrix.set(matrix);
					start.set(event.getX(), event.getY());
					Log.d(LOG_TAG, "mode=DRAG begin at" + start.x + ","
							+ start.y);
					mode = DRAG;
					break;
				case MotionEvent.ACTION_UP:
					int xDiff = (int) Math.abs(event.getX() - start.x);
					int yDiff = (int) Math.abs(event.getY() - start.y);
					if (xDiff < 8 && yDiff < 8) {
						return performClick();
					}
					break;
				case MotionEvent.ACTION_POINTER_UP:
					mode = NONE;
					Log.d(LOG_TAG, "mode=NONE");
					break;
				case MotionEvent.ACTION_MOVE:
					if (mode == DRAG) {
						image.setScaleType(ScaleType.MATRIX);

						float[] values = new float[9];
						matrix.getValues(values);
						float globalX = values[2];
						float globalY = values[5];
						float width = values[0] * image.getWidth();
						float height = values[4] * image.getHeight();

						Log.d(LOG_TAG,
								String.format(
										"image x %f y %f width %f height %f screenWidth %d screenHeight %d imageWidth %d imageHeight %d",
										globalX, globalY, width, height,
										screenWidth, screenHeight,
										image.getWidth(), image.getHeight()));

						float newX = event.getX() - start.x;
						float newY = event.getY() - start.y;

						float minLeft = (screenWidth - width - hiddenWidth
								* values[0]);

						Log.d(LOG_TAG, "newX " + newX + " " + "newY " + newY
								+ " minLeft " + minLeft);
						RectF r = new RectF();
						matrix.mapRect(r);
						Log.i(LOG_TAG, "Rect " + r.left + " " + r.top + " "
								+ r.right + " " + r.bottom);

						// if (// bound dragging to the left to see the
						// rightmost
						// (r.left < minLeft && newX < 0)
						// || (r.left > 0 && newX > 0)) {
						// break;
						// }
						matrix.set(savedMatrix);
						matrix.postTranslate(newX, newY);

						Drawable drawable = image.getDrawable();
						Rect imageBounds = drawable.getBounds();

						matrix.getValues(values);
						Log.d(LOG_TAG,
								String.format(
										"newX %f newY %f postX %f, postY %f boundLeft: %d",
										newX, newY, values[2], values[5],
										imageBounds.left));

					} else if (mode == ZOOM) {
						float newDist = spacing(event);
						Log.d(LOG_TAG, "newDist=" + newDist);
						if (newDist > 10f) {
							matrix.set(savedMatrix);
							float scale = newDist / oldDist;
							matrix.postScale(scale, scale, mid.x, mid.y);
						}
					}
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					oldDist = spacing(event);
					Log.d(LOG_TAG, "oldDist=" + oldDist);
					if (oldDist > 10f) {
						savedMatrix.set(matrix);
						midPoint(mid, event);
						mode = ZOOM;
						Log.d(LOG_TAG, "mode=ZOOM");
					}
					break;
				}

				image.setImageMatrix(matrix);
				return true;
			}

			private float spacing(MotionEvent event) {
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}

			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x / 2, y / 2);
			}

			boolean performClick() {
				Log.d(LOG_TAG, "click");
				// matrix.setScale(1.0f, 1.0f);
				// image.setImageMatrix(matrix);
				return true;
			}
		});

	}
}
