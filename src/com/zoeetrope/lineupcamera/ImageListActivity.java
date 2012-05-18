package com.zoeetrope.lineupcamera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.zoeetrope.lineupcamera.model.Album;

public class ImageListActivity extends Activity {

	private Album mAlbum;
	private GridView mGridview;
	private ImageAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.image_list);
		mGridview = (GridView) findViewById(R.id.gridview);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mAlbum = new Album(extras.getString("ALBUM"));
			mAdapter = new ImageAdapter(this, R.layout.image_list_item, mAlbum);

			mGridview.setAdapter(mAdapter);
			mGridview.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					Intent imageIntent = new Intent();
					imageIntent.setComponent(new ComponentName(
							ImageListActivity.this, ImageActivity.class));

					Bundle bundle = new Bundle();
					bundle.putString("ALBUM", mAlbum.getName());
					bundle.putInt("POSITION", position);
					imageIntent.putExtras(bundle);

					ImageListActivity.this.startActivity(imageIntent);
				}
			});

			registerForContextMenu(mGridview);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.image_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		if (item.getItemId() == R.id.remove) {
			mAlbum.remove(info.position);
			mAdapter.notifyDataSetChanged();
			mGridview.invalidateViews();
			return true;
		} else {
			return super.onContextItemSelected(item);
		}
	}
}
