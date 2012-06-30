package com.zoeetrope.lineupcamera.model;

import java.io.File;
import java.util.Date;

import android.graphics.Bitmap;

import com.zoeetrope.lineupcamera.daos.ImageDAO;

public class Image {

	private File mFile;
	private Date mModificationDate;
	private ImageDAO mImageDAO;

	public Image(File imageFile) {
		mFile = imageFile;
		mModificationDate = new Date(imageFile.lastModified());
		mImageDAO = new ImageDAO();
	}

	public Date getModifiedDate() {
		return this.mModificationDate;
	}

	public String getName() {
		return this.mFile.getName();
	}

	public File getFile() {
		return this.mFile;
	}

	public float getAspectRatio() {
		Bitmap image = getThumbnail();
		float aspectRatio = 0;

		if (image != null) {
			aspectRatio = (float) image.getWidth() / (float) image.getHeight();
		}

		return aspectRatio;
	}

	public int getOrientation() {
		int orientation = 0;
		Bitmap image = getBitmap(100);

		if (image.getHeight() > image.getWidth()) {
			orientation = -90;
		}

		return orientation;
	}

	public Bitmap getThumbnail() {
		return mImageDAO.getThumbnail(mFile);
	}

	public Bitmap getBitmap(int requiredHeight) {
		return mImageDAO.getBitmap(mFile, requiredHeight);
	}

	public Bitmap getResizedBitmap(int newWidth, int newHeight) {
		return mImageDAO.getResizedBitmap(mFile, newWidth, newHeight);
	}

}
