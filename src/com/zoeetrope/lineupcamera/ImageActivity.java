package com.zoeetrope.lineupcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.zoeetrope.lineupcamera.model.Album;

public class ImageActivity extends Activity implements OnTouchListener {

	private ImageView mImageView;
	private Album mAlbum;
	private int mPosition;
	private Bitmap mBitmap;

	private Point mStart = new Point();
	private Matrix mMatrix = new Matrix();
	private Matrix mSavedMatrix = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mMode = NONE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.image);
		mImageView = (ImageView) findViewById(R.id.image);
		mImageView.setOnTouchListener(this);

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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mSavedMatrix.set(mMatrix);
			mStart.set((int) event.getX(), (int) event.getY());
			mMode = DRAG;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mMode == DRAG) {
				mMatrix.set(mSavedMatrix);
				mMatrix.postTranslate(event.getX() - mStart.x, event.getY()
						- mStart.y);
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mMode = NONE;
			break;
		}

		mImageView.setImageMatrix(mMatrix);

		return true;
	}
}
