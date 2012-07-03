package com.zoeetrope.lineupcamera.controllers;

import java.util.HashMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zoeetrope.lineupcamera.activities.ImageActivity;
import com.zoeetrope.lineupcamera.activities.LineUpCameraActivity;
import com.zoeetrope.lineupcamera.models.Album;

public class ImageListController extends Controller {

	public static final int MESSAGE_MODEL_UPDATED = 1;

	public static final int MESSAGE_LOAD_IMAGES = 2;
	public static final int MESSAGE_CREATE_IMAGE = 3;
	public static final int MESSAGE_REMOVE_IMAGE = 4;
	public static final int MESSAGE_VIEW_IMAGE = 5;

	private Context mContext;
	private Album mAlbum;

	public ImageListController(Context context, Album album) {
		mContext = context;
		mAlbum = album;
	}

	@Override
	public boolean handle(int what, HashMap<String, Object> params) {
		switch (what) {
		case MESSAGE_LOAD_IMAGES:
			loadImages();
			return true;
		case MESSAGE_CREATE_IMAGE:
			createImage();
			return true;
		case MESSAGE_REMOVE_IMAGE:
			removeImage((Integer) params.get("index"));
			return true;
		case MESSAGE_VIEW_IMAGE:
			viewImage((Integer) params.get("index"));
			return true;
		}
		return false;
	}

	private void loadImages() {
		mAlbum.loadImages();

		notifyOutboxHandlers(MESSAGE_MODEL_UPDATED, 0, 0, null);
	}

	private void viewImage(int index) {
		Intent imageIntent = new Intent();
		imageIntent.setComponent(new ComponentName(mContext,
				ImageActivity.class));

		Bundle bundle = new Bundle();
		bundle.putString("ALBUM", mAlbum.getName());
		bundle.putInt("POSITION", index);
		imageIntent.putExtras(bundle);

		mContext.startActivity(imageIntent);
	}

	private void createImage() {
		Intent cameraIntent = new Intent();
		cameraIntent.setComponent(new ComponentName(mContext,
				LineUpCameraActivity.class));

		Bundle bundle = new Bundle();
		bundle.putString("ALBUM", mAlbum.getName());
		cameraIntent.putExtras(bundle);

		mContext.startActivity(cameraIntent);
	}

	private void removeImage(int index) {
		mAlbum.remove(index);

		notifyOutboxHandlers(MESSAGE_MODEL_UPDATED, 0, 0, null);
	}

}
