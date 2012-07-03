package com.zoeetrope.lineupcamera.activities;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.zoeetrope.lineupcamera.R;
import com.zoeetrope.lineupcamera.controllers.AlbumListController;
import com.zoeetrope.lineupcamera.lists.AlbumListAdapter;
import com.zoeetrope.lineupcamera.models.Album;

public class AlbumListActivity extends SherlockListActivity implements
		Handler.Callback {

	static final int DIALOG_NEW_ALBUM_ID = 0;
	static final int DIALOG_RENAME_ALBUM_ID = 1;
	static final int DIALOG_DELETE_ALBUM_ID = 2;

	private AlbumListController mController;
	private ArrayList<Album> mAlbums;
	private AlbumListAdapter mAdapter;
	private Dialog mSplashDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		showSplash();

		setContentView(R.layout.album_list);
		registerForContextMenu(getListView());

		mAlbums = new ArrayList<Album>();
		mController = new AlbumListController(this, mAlbums);
		mController.addOutboxHandler(new Handler(this));

		mAdapter = new AlbumListAdapter(AlbumListActivity.this,
				R.layout.album_list_item, mAlbums);
		setListAdapter(mAdapter);

		mController.handle(AlbumListController.MESSAGE_LOAD_ALBUMS);
	}

	@Override
	protected void onResume() {
		super.onResume();

		mController.handle(AlbumListController.MESSAGE_LOAD_ALBUMS);
	}

	@Override
	public boolean handleMessage(Message message) {
		switch (message.what) {
		case AlbumListController.MESSAGE_MODEL_UPDATED:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					hideSplash();
					mAdapter.notifyDataSetChanged();
				}
			});
			return true;
		}
		return false;
	}

	private void showSplash() {
		mSplashDialog = new Dialog(this, R.style.SplashScreen);
		mSplashDialog.setContentView(R.layout.splash_screen);
		mSplashDialog.setCancelable(false);
		mSplashDialog.show();

		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				hideSplash();
			}
		}, 3000);
	}

	private void hideSplash() {
		if (mSplashDialog != null) {
			mSplashDialog.dismiss();
			mSplashDialog = null;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog alertDialog = null;
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.album_name_dialog, null);
		final int albumIndex = args.getInt("ALBUM_INDEX");
		final EditText nameField = (EditText) layout
				.findViewById(R.id.albumName);

		switch (id) {
		case DIALOG_NEW_ALBUM_ID:
			builder.setView(layout);
			builder.setTitle(R.string.new_album_dialog_title);
			builder.setPositiveButton(R.string.ok_button,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							HashMap<String, Object> params = new HashMap<String, Object>();
							params.put("index", albumIndex);
							params.put("name", nameField.getText().toString());

							mController.handle(
									AlbumListController.MESSAGE_CREATE_ALBUM,
									params);
						}
					});
			break;
		case DIALOG_RENAME_ALBUM_ID:
			builder.setView(layout);
			builder.setTitle(R.string.rename_album_dialog_title);
			builder.setPositiveButton(R.string.ok_button,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							HashMap<String, Object> params = new HashMap<String, Object>();
							params.put("index", albumIndex);
							params.put("new_name", nameField.getText()
									.toString());

							mController.handle(
									AlbumListController.MESSAGE_RENAME_ALBUM,
									params);
						}
					});
			break;
		case DIALOG_DELETE_ALBUM_ID:
			builder.setMessage(R.string.confirm_delete_album_message);
			builder.setPositiveButton(R.string.confirm_delete_yes,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							HashMap<String, Object> params = new HashMap<String, Object>();
							params.put("index", albumIndex);

							mController.handle(
									AlbumListController.MESSAGE_REMOVE_ALBUM,
									params);
						}

					});
			builder.setNegativeButton(R.string.confirm_delete_no,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}

					});
			break;
		}

		alertDialog = builder.create();
		alertDialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

		return alertDialog;
	}

	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		EditText nameField = (EditText) dialog.findViewById(R.id.albumName);
		final int albumIndex = args.getInt("ALBUM_INDEX");

		switch (id) {
		case DIALOG_NEW_ALBUM_ID:
			String untitled = getString(R.string.untitled_album);

			nameField.setText(untitled);
			nameField.setSelection(0, untitled.length());
			break;
		case DIALOG_RENAME_ALBUM_ID:
			String albumName = mAlbums.get(albumIndex).getName();

			nameField.setText(albumName);
			nameField.setSelection(albumName.length());
			break;
		}

	};

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("index", position);
		mController.handle(AlbumListController.MESSAGE_VIEW_ALBUM, params);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		android.view.MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.album_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		if (item.getItemId() == R.id.remove) {
			Bundle bundle = new Bundle();
			bundle.putInt("ALBUM_INDEX", info.position);
			AlbumListActivity.this.showDialog(DIALOG_DELETE_ALBUM_ID, bundle);
			return true;
		} else if (item.getItemId() == R.id.rename) {
			Bundle bundle = new Bundle();
			bundle.putInt("ALBUM_INDEX", info.position);
			AlbumListActivity.this.showDialog(DIALOG_RENAME_ALBUM_ID, bundle);
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_albums, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.newAlbum) {
			this.showDialog(DIALOG_NEW_ALBUM_ID, new Bundle());
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

}
