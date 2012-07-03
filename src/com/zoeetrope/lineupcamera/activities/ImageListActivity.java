package com.zoeetrope.lineupcamera.activities;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.zoeetrope.lineupcamera.R;
import com.zoeetrope.lineupcamera.controllers.ImageListController;
import com.zoeetrope.lineupcamera.lists.ImageListAdapter;
import com.zoeetrope.lineupcamera.models.Album;
import com.zoeetrope.lineupcamera.models.Image;

public class ImageListActivity extends SherlockActivity implements
		Handler.Callback {

	private ImageListController mController;
	private Album mAlbum;
	private GridView mGridview;
	private ImageListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.image_list);
		mGridview = (GridView) findViewById(R.id.gridview);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			mAlbum = new Album(extras.getString("ALBUM"));
			mController = new ImageListController(this, mAlbum);
			mController.addOutboxHandler(new Handler(this));

			mAdapter = new ImageListAdapter(this, R.layout.image_list_item,
					mAlbum);

			mGridview.setAdapter(mAdapter);
			mGridview.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("index", position);
					mController.handle(ImageListController.MESSAGE_VIEW_IMAGE,
							params);
				}
			});

			initActionBar();
			registerForContextMenu(mGridview);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		mController.handle(ImageListController.MESSAGE_LOAD_IMAGES);
	}

	private void initActionBar() {
		ActionBar bar = getSupportActionBar();
		Image latestImage = mAlbum.getLatestImage();

		bar.setTitle((CharSequence) mAlbum.getName());
		bar.setSubtitle(mAlbum.getImages().size() + " "
				+ getResources().getString(R.string.picture_count));
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);

		if (latestImage != null) {
			Bitmap bitmap = latestImage.getThumbnail();

			if (bitmap != null) {
				bar.setIcon(new BitmapDrawable(getResources(), bitmap));
			}
		}
	}

	@Override
	public boolean handleMessage(Message message) {
		switch (message.what) {
		case ImageListController.MESSAGE_MODEL_UPDATED:
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mAdapter.notifyDataSetChanged();
				}
			});
			return true;
		}
		return false;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		android.view.MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.image_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		if (item.getItemId() == R.id.remove) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final int imageIndex = info.position;

			builder.setMessage(R.string.confirm_delete_image_message);
			builder.setPositiveButton(R.string.confirm_delete_yes,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int id) {
							HashMap<String, Object> params = new HashMap<String, Object>();
							params.put("index", imageIndex);
							mController.handle(
									ImageListController.MESSAGE_REMOVE_IMAGE,
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

			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_images, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else if (item.getItemId() == R.id.newPicture) {
			mController.handle(ImageListController.MESSAGE_CREATE_IMAGE);
		}

		return super.onOptionsItemSelected(item);
	}

}
