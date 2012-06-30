package com.zoeetrope.lineupcamera.daos;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;
import android.util.Log;

import com.zoeetrope.lineupcamera.model.Album;
import com.zoeetrope.lineupcamera.model.Image;

public class AlbumDAO {

	public ArrayList<Album> getAll() {
		ArrayList<Album> albums = new ArrayList<Album>();
		File storageFolder = getStorageFolder();

		if (!storageFolder.exists()) {
			if (!storageFolder.mkdirs()) {
				Log.d("LineUpCamera", "failed to create directory");
			}
		}

		for (File child : storageFolder.listFiles()) {
			if (child.isDirectory()) {
				albums.add(new Album(child.getName()));
			}
		}

		return albums;
	}

	public void rename(Album album, String newName) {
		File albumFolder = new File(getStorageFolder(), album.getName());

		if (albumFolder != null) {
			String albumPath = albumFolder.getAbsolutePath();
			albumPath = albumPath.substring(0,
					albumPath.lastIndexOf(File.separator));

			File newFile = new File(albumPath + File.separator + newName);

			if (albumFolder.renameTo(newFile)) {
				album.setName(newName);
			}
		}
	}

	public void remove(Album album) {
		File albumFolder = new File(getStorageFolder(), album.getName());
		ImageDAO imageDAO = new ImageDAO();

		for (Image image : imageDAO.getAll(album.getName())) {
			imageDAO.remove(image);
		}

		albumFolder.delete();
	}

	private File getStorageFolder() {
		return new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"LineUpCamera");
	}

}
