package org.dyndns.warenix.imageuploader;

import java.io.File;
import java.util.ArrayList;

import org.dyndns.warenix.http.CustomMultiPartEntity.ProgressListener;
import org.dyndns.warenix.http.HTTPPostClient;
import org.dyndns.warenix.http.HTTPPostContent;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ProgressBar;

public class ImageuploaderActivity extends Activity {

	Button selectPhoto;
	Button uploadPhoto;
	Gallery imageQueue;
	ProgressBar uploadProgress;

	ImageQueueAdapter imageQueueAdapter;

	final int SELECT_PHOTO = 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		imageQueueAdapter = new ImageQueueAdapter(this);

		selectPhoto = (Button) findViewById(R.id.selectPhoto);
		selectPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pickMultipleLocalImage();
			}
		});

		uploadPhoto = (Button) findViewById(R.id.uploadPhoto);
		uploadPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ArrayList<Uri> imageUriList = imageQueueAdapter.getImageQueue();
				if (imageUriList != null) {
					uploadProgress.setVisibility(View.VISIBLE);
					new HTTPPostAsyncTask(imageQueueAdapter).execute();
				}
			}
		});

		imageQueue = (Gallery) findViewById(R.id.imageQueue);
		imageQueue.setAdapter(imageQueueAdapter);
		imageQueue.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("lab", "" + position);
				imageQueueAdapter.removeImageUri((Uri) imageQueueAdapter
						.getItem(position));
				return false;
			}
		});

		uploadProgress = (ProgressBar) findViewById(R.id.uploadProgress);
		uploadProgress.setVisibility(View.INVISIBLE);

		Intent imageReturnedIntent = getIntent();
		if (imageReturnedIntent != null) {
			if (Intent.ACTION_SEND_MULTIPLE.equals(imageReturnedIntent
					.getAction())
					&& imageReturnedIntent.hasExtra(Intent.EXTRA_STREAM)) {
				ArrayList<Parcelable> list = imageReturnedIntent
						.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
				for (Parcelable p : list) {
					Uri uri = (Uri) p;
					imageQueueAdapter.addImageUri(uri);
					Log.d("lab", "onActivityResult:" + uri);
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch (requestCode) {
		case SELECT_PHOTO:
			if (resultCode == RESULT_OK) {
				Uri selectedImage = imageReturnedIntent.getData();
				imageQueueAdapter.addImageUri(selectedImage);
			}
		}
	}

	void pickMultipleLocalImage() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);

	}

	class HTTPPostAsyncTask extends AsyncTask<Void, Integer, Void> implements
			ProgressListener {
		ImageQueueAdapter imageQueueAdapter;

		long totalFileSize;

		public HTTPPostAsyncTask(ImageQueueAdapter imageQueueAdapter) {
			this.imageQueueAdapter = imageQueueAdapter;
		}

		@Override
		protected Void doInBackground(Void... params) {

			ArrayList<Uri> imageUriList = imageQueueAdapter.getImageQueue();

			while (imageUriList != null && imageUriList.size() > 0) {
				Log.d("lab", "posting image uri:" + imageUriList.get(0));

				final Uri selectedImage = imageUriList.get(0);

				ContentResolver cr = getContentResolver();
				Cursor cur = cr
						.query(selectedImage,
								new String[] {
										android.provider.MediaStore.Images.ImageColumns.DATA,
										android.provider.MediaStore.Images.ImageColumns.MIME_TYPE },
								null, null, null);
				cur.moveToFirst();
				String filePath = cur.getString(0);
				String contentType = cur.getString(1);

				File image = new File(filePath);
				totalFileSize = image.length();
				Log.d("lab", String.format(
						"file path: %s content-type: %s size: %d", filePath,
						contentType, totalFileSize));

				HTTPPostContent httpPostCommunication = new UploadScreenshot(
						this, image);

				new HTTPPostClient().httpPost(
						"http://img1.uploadscreenshot.com/api-upload.php",
						httpPostCommunication.getMultipartEntity(),
						new ToastResponder(ImageuploaderActivity.this));

				imageQueue.post(new Runnable() {

					@Override
					public void run() {
						imageQueueAdapter.removeImageUri(selectedImage);

					}
				});

			}
			return null;
		}

		protected void onProgressUpdate(Integer... progress) {
			uploadProgress.setProgress(progress[0]);
		}

		protected void onPostExecute(Void v) {
			uploadProgress.setVisibility(View.INVISIBLE);
		}

		@Override
		public void transferred(long num) {
			publishProgress((int) (num * 100 / totalFileSize));
		}
	}

}