package com.zoeetrope.lineupcamera;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

public class Album {

	private final static String TAG = "Album";
	public static final int MEDIA_TYPE_IMAGE = 1;

	public Date mLastModificationDate;
	private String mName;

	public Album(String name) {
		this.mName = name;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public Date getLastModifiedDate() {
		return mLastModificationDate;
	}

	public Bitmap getLatestImage(int requiredHeight) {
		File pictureFile = null;
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"LineUpCamera");

		File albumFolder = new File(mediaStorageDir, mName);
		if (!albumFolder.exists()) {
			if (!albumFolder.mkdirs()) {
				Log.d(TAG, "failed to create directory");
			}
		}

		File[] images = albumFolder.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});

		long lastMod = Long.MIN_VALUE;
		for (File file : images) {
			if (file.lastModified() > lastMod) {
				pictureFile = file;
				lastMod = file.lastModified();
			}
		}

		this.mLastModificationDate = new Date(pictureFile.lastModified());
		return decodeFile(pictureFile, requiredHeight);
	}

	public void saveNewImage(byte[] data) {
		File mediaFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);

		try {
			FileOutputStream fos = new FileOutputStream(mediaFile);
			fos.write(data);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d(TAG, "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d(TAG, "Error accessing file: " + e.getMessage());
		}
	}

	private Bitmap decodeFile(File f, int requiredHeight) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outHeight / scale / 2 >= requiredHeight)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	private File getOutputMediaFile(int type) {
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"LineUpCamera");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(TAG, "failed to create directory");
				return null;
			}
		}

		File albumFolder = new File(mediaStorageDir, mName);
		if (!albumFolder.exists()) {
			if (!albumFolder.mkdirs()) {
				Log.d(TAG, "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(albumFolder.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

}
