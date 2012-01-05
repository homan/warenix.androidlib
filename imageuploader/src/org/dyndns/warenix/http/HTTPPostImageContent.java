package org.dyndns.warenix.http;

import java.io.File;

public interface HTTPPostImageContent extends HTTPPostContent {

	public void setImage(File image, String contentType);
}
