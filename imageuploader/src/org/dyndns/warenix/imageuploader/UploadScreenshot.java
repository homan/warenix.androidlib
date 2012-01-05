package org.dyndns.warenix.imageuploader;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.dyndns.warenix.http.CustomMultiPartEntity;
import org.dyndns.warenix.http.HTTPPostContent;
import org.dyndns.warenix.http.CustomMultiPartEntity.ProgressListener;

public class UploadScreenshot implements HTTPPostContent {

	/**
	 * http content
	 */
	CustomMultiPartEntity multipartEntity;

	/**
	 * multipary progress listener
	 */

	ProgressListener progressListener;

	File image;

	public UploadScreenshot(ProgressListener progressListener, File image) {
		this.progressListener = progressListener;
		this.image = image;
	}

	public CustomMultiPartEntity getMultipartEntity() {
		CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(
				progressListener);
		try {
			multipartContent.addPart("apiKey", new StringBody(
					"519acd4be68445997245348820"));
			multipartContent.addPart("xmlOutput", new StringBody("1"));
			multipartContent.addPart("testMode", new StringBody("1"));
			multipartContent.addPart("userfile", new FileBody(image));

			return multipartContent;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
