package com.zoeetrope.lineupcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;

import com.zoeetrope.lineupcamera.model.Album;

public class ImageActivity extends Activity {

	private ImageView mImageView;
	private Album mAlbum;
	private int mPosition;
	private Bitmap mBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.image);
		mImageView = (ImageView) findViewById(R.id.image);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mAlbum = new Album(extras.getString("ALBUM"));
			mPosition = extras.getInt("POSITION");
			loadImage();
		}
	}

	private void loadImage() {
		Display display = getWindowManager().getDefaultDisplay();
		mBitmap = mAlbum.getImages().get(mPosition)
				.getBitmap(display.getHeight());

		mImageView.setImageBitmap(mBitmap);
	}

	@Override
	protected void onResume() {
		super.onResume();

		loadImage();
	}

	@Override
	protected void onPause() {
		super.onPause();

		mBitmap.recycle();
	}
}
