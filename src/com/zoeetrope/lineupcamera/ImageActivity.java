package com.zoeetrope.lineupcamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.util.FloatMath;
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
	private Point mPrevious = new Point();
	private Point mMidPoint = new Point();
	private Matrix mMatrix = new Matrix();
	private Matrix mSavedMatrix = new Matrix();
	private float mOldDistance = 0;

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mMode = NONE;
	private boolean mSwipeReset = false;

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
			mSwipeReset = false;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mOldDistance = getPointDistance(event);

			if (mOldDistance > 10f) {
				mSavedMatrix.set(mMatrix);
				updateMidPoint(mMidPoint, event);
				mMode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float newDistance = 0;
			float swipeDistanceX = event.getX(0) - mPrevious.x;
			float swipeDistanceY = event.getY(0) - mPrevious.y;

			if (swipeDistanceX > 100 && !mSwipeReset
					&& (swipeDistanceY < 50 && swipeDistanceY > -50)) {
				showPreviousImage();
				mSwipeReset = true;
			} else if (swipeDistanceX < -100 && !mSwipeReset
					&& (swipeDistanceY < 50 && swipeDistanceY > -50)) {
				showNextImage();
				mSwipeReset = true;
			}

			if (mMode == ZOOM) {
				newDistance = getPointDistance(event);
			}

			if (mMode == DRAG || (mMode == ZOOM && newDistance > 10f)) {
				mMatrix.set(mSavedMatrix);

				mMatrix.postTranslate(event.getX(0) - mStart.x, event.getY(0)
						- mStart.y);

				if (mMode == ZOOM) {
					float scale = newDistance / mOldDistance;
					mMatrix.postScale(scale, scale, mMidPoint.x, mMidPoint.y);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mMode = NONE;
			break;
		}

		mPrevious.set((int) event.getX(), (int) event.getY());
		mImageView.setImageMatrix(mMatrix);

		return true;
	}

	private void showNextImage() {
		mPosition += 1;

		if (mPosition >= mAlbum.getImages().size()) {
			mPosition = 0;
		}

		mBitmap.recycle();
		loadImage();
	}

	private void showPreviousImage() {
		mPosition -= 1;

		if (mPosition < 0) {
			mPosition = mAlbum.getImages().size() - 1;
		}

		mBitmap.recycle();
		loadImage();
	}

	private float getPointDistance(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);

		return FloatMath.sqrt(x * x + y * y);
	}

	private void updateMidPoint(Point point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(((int) x / 2), ((int) y / 2));
	}
}
