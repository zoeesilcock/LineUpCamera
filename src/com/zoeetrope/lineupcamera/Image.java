package com.zoeetrope.lineupcamera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
		Bitmap image = getBitmap(200);
		
		return (float) image.getWidth() / (float) image.getHeight();
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
			return BitmapFactory.decodeStream(new FileInputStream(mFile), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}
}
