package com.zoeetrope.lineupcamera.daos;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;

import com.zoeetrope.lineupcamera.model.Album;
import com.zoeetrope.lineupcamera.model.Image;

public class ImageDAO {

	public ArrayList<Image> getAll(String albumName) {
		ArrayList<Image> images = new ArrayList<Image>();

		File albumFolder = new File(getStorageFolder(), albumName);
		if (!albumFolder.exists()) {
			if (!albumFolder.mkdirs()) {

			}
		}

		File[] files = albumFolder.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});

		for (File file : files) {
			images.add(new Image(file));
		}

		Collections.sort(images, new ImageDateComparator());
		return images;
	}

	public void create(Album album, byte[] data) {
		File mediaFile = getOutputMediaFile(album.getName());

		try {
			FileOutputStream fos = new FileOutputStream(mediaFile);
			fos.write(data);
			fos.close();

			album.add(new Image(mediaFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void remove(Image image) {
		image.getFile().delete();
	}

	public Bitmap getThumbnail(File file) {
		ExifInterface exif;
		Bitmap bitmap = null;

		try {
			exif = new ExifInterface(file.getPath());
			byte[] thumbnail = exif.getThumbnail();

			if (thumbnail != null) {
				ByteArrayInputStream stream = new ByteArrayInputStream(
						thumbnail);
				bitmap = BitmapFactory.decodeStream(stream, null, null);
				stream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public Bitmap getBitmap(File file, int requiredHeight) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(file), null, o);

			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outHeight / scale / 2 >= requiredHeight)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;

			Bitmap bitmap = null;
			try {
				FileInputStream fis = new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(fis, null, o2);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Bitmap getResizedBitmap(File file, int newWidth, int newHeight) {
		Bitmap bm = this.getBitmap(file, newHeight);
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

	private File getStorageFolder() {
		return new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"LineUpCamera");
	}

	private File getOutputMediaFile(String albumName) {
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"LineUpCamera");

		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		File albumFolder = new File(mediaStorageDir, albumName);
		if (!albumFolder.exists()) {
			if (!albumFolder.mkdirs()) {
				return null;
			}
		}

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile = new File(albumFolder.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}

	public class ImageDateComparator implements Comparator<Image> {
		@Override
		public int compare(Image o1, Image o2) {
			return o1.getModifiedDate().compareTo(o2.getModifiedDate());
		}
	}

}
