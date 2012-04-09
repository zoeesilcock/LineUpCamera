package com.zoeetrope.lineupcamera;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

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
					// Start the image viewer.
				}
			});
		}
	}
}
