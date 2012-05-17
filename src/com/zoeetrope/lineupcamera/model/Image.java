package com.zoeetrope.lineupcamera.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class Image {

	private File mFile;
	private Date mModificationDate;

	public Image(File imageFile) {
		this.mFile = imageFile;
		this.mModificationDate = new Date(imageFile.lastModified());
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

		return (float) image.getWidth() / (float) image.getHeight();
	}

	public Bitmap getThumbnail() {
		ExifInterface exif;
		try {
			exif = new ExifInterface(mFile.getPath());
			byte[] thumbnail = exif.getThumbnail();

			ByteArrayInputStream stream = new ByteArrayInputStream(thumbnail);
			Bitmap bitmap = BitmapFactory.decodeStream(stream, null, null);
			stream.close();

			return bitmap;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Bitmap getBitmap(int requiredHeight) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(mFile), null, o);

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outHeight / scale / 2 >= requiredHeight)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;

			Bitmap bitmap = null;
			try {
				FileInputStream fis = new FileInputStream(mFile);
				bitmap = BitmapFactory.decodeStream(fis, null, o2);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return bitmap;
		} catch (FileNotFoundException e) {
		}

		return null;
	}

	public Bitmap getResizedBitmap(int newWidth, int newHeight) {
		Bitmap bm = this.getBitmap(newHeight);
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);

		return resizedBitmap;
	}

	public void remove() {
		mFile.delete();
	}
}
