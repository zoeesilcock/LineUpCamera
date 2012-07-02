package com.zoeetrope.lineupcamera.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.zoeetrope.lineupcamera.daos.AlbumDAO;
import com.zoeetrope.lineupcamera.lists.AlbumListAdapter;
import com.zoeetrope.lineupcamera.models.Album;

public class AlbumListActivity extends SherlockListActivity {

	static final int DIALOG_NEW_ALBUM_ID = 0;
	static final int DIALOG_RENAME_ALBUM_ID = 1;
	static final int DIALOG_DELETE_ALBUM_ID = 2;

	private ArrayList<Album> mAlbums;
	private AlbumListAdapter mAdapter;
	private AlbumDAO mAlbumDAO;
	private Dialog mSplashDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAlbumDAO = new AlbumDAO();
		setContentView(R.layout.album_list);
		registerForContextMenu(getListView());

		showSplash();
		loadAlbums();
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadAlbums();
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

			mAlbums = result;
			mAdapter = new AlbumListAdapter(AlbumListActivity.this,
					R.layout.album_list_item, mAlbums);
			setListAdapter(mAdapter);

			hideSplash();
		}

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
							Intent cameraIntent = new Intent();
							cameraIntent.setComponent(new ComponentName(
									AlbumListActivity.this,
									LineUpCameraActivity.class));

							Bundle bundle = new Bundle();
							bundle.putString("ALBUM", nameField.getText()
									.toString());
							cameraIntent.putExtras(bundle);

							AlbumListActivity.this.startActivity(cameraIntent);
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
							Album album = mAlbums.get(albumIndex);

							mAlbumDAO.rename(album, nameField.getText()
									.toString());
						}
					});
			break;
		case DIALOG_DELETE_ALBUM_ID:
			builder.setMessage(R.string.confirm_delete_album_message);
			builder.setPositiveButton(R.string.confirm_delete_yes,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							mAlbumDAO.remove(mAlbums.get(albumIndex));

							mAlbums.remove(albumIndex);

							mAdapter.notifyDataSetChanged();
							getListView().invalidateViews();
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

		Intent imageListIntent = new Intent();
		imageListIntent.setComponent(new ComponentName(AlbumListActivity.this,
				ImageListActivity.class));

		Bundle bundle = new Bundle();
		bundle.putString("ALBUM", mAlbums.get(position).getName());
		imageListIntent.putExtras(bundle);

		AlbumListActivity.this.startActivity(imageListIntent);
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
