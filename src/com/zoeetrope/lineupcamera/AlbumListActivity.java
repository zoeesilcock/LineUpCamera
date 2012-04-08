package com.zoeetrope.lineupcamera;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class AlbumListActivity extends ListActivity {

	static final int DIALOG_NEW_ALBUM_ID = 0;

	private ArrayList<Album> mAlbums;
	private Button mNewAlbumButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.album_list);
		loadAlbums();

		mNewAlbumButton = (Button) findViewById(R.id.newAlbum);
		mNewAlbumButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				AlbumListActivity.this.showDialog(DIALOG_NEW_ALBUM_ID);
			}

		});
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;

		switch (id) {
		case DIALOG_NEW_ALBUM_ID:
			AlertDialog.Builder builder;
			AlertDialog alertDialog;
			Context context = AlbumListActivity.this;
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			final View layout = inflater.inflate(R.layout.album_name_dialog,
					null);

			builder = new AlertDialog.Builder(context);
			builder.setView(layout);
			builder.setTitle(R.string.ablum_name_dialog_title);
			builder.setPositiveButton(R.string.ok_button,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent cameraIntent = new Intent();
							cameraIntent.setComponent(new ComponentName(
									AlbumListActivity.this,
									LineUpCameraActivity.class));

							EditText name = (EditText) layout
									.findViewById(R.id.albumName);

							Bundle bundle = new Bundle();
							bundle.putString("ALBUM", name.getText().toString());
							cameraIntent.putExtras(bundle);

							AlbumListActivity.this.startActivity(cameraIntent);
						}
					});

			alertDialog = builder.create();

			dialog = alertDialog;
			break;
		}

		return dialog;
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadAlbums();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent cameraIntent = new Intent();
		cameraIntent.setComponent(new ComponentName(AlbumListActivity.this,
				LineUpCameraActivity.class));

		Bundle bundle = new Bundle();
		bundle.putString("ALBUM", mAlbums.get(position).getName());
		cameraIntent.putExtras(bundle);

		AlbumListActivity.this.startActivity(cameraIntent);
	}

	private void loadAlbums() {
		mAlbums = new ArrayList<Album>();
		File storageFolder = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"LineUpCamera");

		if (!storageFolder.exists()) {
			if (!storageFolder.mkdirs()) {
				Log.d("LineUpCamera", "failed to create directory");
			}
		}

		for (File child : storageFolder.listFiles()) {
			if (child.isDirectory()) {
				mAlbums.add(new Album(child.getName()));
			}
		}

		setListAdapter(new AlbumListAdapter(this, R.layout.album_list_item,
				mAlbums));
	}
}
