package com.zoeetrope.lineupcamera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.zoeetrope.lineupcamera.model.Album;

public class ImageListActivity extends Activity {

	private Album mAlbum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.image_list);
		GridView gridview = (GridView) findViewById(R.id.gridview);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mAlbum = new Album(extras.getString("ALBUM"));

			gridview.setAdapter(new ImageAdapter(this,
					R.layout.image_list_item, mAlbum));
			gridview.setOnItemClickListener(new OnItemClickListener() {
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
		}
	}
}
