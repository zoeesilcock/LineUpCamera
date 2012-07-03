package com.zoeetrope.lineupcamera.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.zoeetrope.lineupcamera.activities.ImageListActivity;
import com.zoeetrope.lineupcamera.daos.AlbumDAO;
import com.zoeetrope.lineupcamera.models.Album;

public class AlbumListController extends Controller {

	public static final int MESSAGE_MODEL_UPDATED = 1;

	public static final int MESSAGE_LOAD_ALBUMS = 2;
	public static final int MESSAGE_RENAME_ALBUM = 3;
	public static final int MESSAGE_REMOVE_ALBUM = 4;
	public static final int MESSAGE_VIEW_ALBUM = 5;
	public static final int MESSAGE_CREATE_ALBUM = 6;

	private ArrayList<Album> mAlbums;
	private AlbumDAO mAlbumDAO;
	private Context mContext;

	public AlbumListController(Context context, ArrayList<Album> albums) {
		mContext = context;
		mAlbums = albums;
		mAlbumDAO = new AlbumDAO();
	}

	@Override
	public boolean handle(int what, HashMap<String, Object> params) {
		switch (what) {
		case MESSAGE_LOAD_ALBUMS:
			loadAlbums();
			return true;
		case MESSAGE_RENAME_ALBUM:
			renameAlbum((Integer) params.get("index"),
					(String) params.get("new_name"));
			return true;
		case MESSAGE_REMOVE_ALBUM:
			removeAlbum((Integer) params.get("index"));
			return true;
		case MESSAGE_VIEW_ALBUM:
			viewAlbum((Integer) params.get("index"));
			return true;
		case MESSAGE_CREATE_ALBUM:
			createAlbum((String) params.get("name"));
			return true;
		}
		return false;
	}

	private void loadAlbums() {
		new LoadAlbumsTask().execute();
	}

	private class LoadAlbumsTask extends
			AsyncTask<Void, Integer, ArrayList<Album>> {

		@Override
		protected ArrayList<Album> doInBackground(Void... params) {
			return mAlbumDAO.getAll();
		}

		@Override
		protected void onPostExecute(ArrayList<Album> result) {
			super.onPostExecute(result);

			mAlbums.clear();
			mAlbums.addAll(result);

			notifyOutboxHandlers(MESSAGE_MODEL_UPDATED, 0, 0, null);
		}

	}

	private void renameAlbum(int index, String newName) {
		mAlbumDAO.rename(mAlbums.get(index), newName);

		notifyOutboxHandlers(MESSAGE_MODEL_UPDATED, 0, 0, null);
	}

	private void removeAlbum(int index) {
		mAlbumDAO.remove(mAlbums.get(index));
		mAlbums.remove(index);

		notifyOutboxHandlers(MESSAGE_MODEL_UPDATED, 0, 0, null);
	}

	private void createAlbum(String name) {
		Album album = new Album(name);

		mAlbums.add(album);
		notifyOutboxHandlers(MESSAGE_MODEL_UPDATED, 0, 0, null);

		viewAlbum(mAlbums.indexOf(album));
	}

	private void viewAlbum(int index) {
		Intent imageListIntent = new Intent();
		imageListIntent.setComponent(new ComponentName(mContext,
				ImageListActivity.class));

		Bundle bundle = new Bundle();
		bundle.putString("ALBUM", mAlbums.get(index).getName());
		imageListIntent.putExtras(bundle);

		mContext.startActivity(imageListIntent);
	}

}
