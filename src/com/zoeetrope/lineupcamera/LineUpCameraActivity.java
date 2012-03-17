package com.zoeetrope.lineupcamera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

public class LineUpCameraActivity extends Activity {

	private final String TAG = "LineUpCameraActivity";
	private Camera mCamera;
	private CameraPreview mPreview;
	private CameraOverlay mOverlay;
	private SeekBar mOverlayOpacity;
	private GestureDetector mGestureDetector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.camera);

		mOverlay = (CameraOverlay) findViewById(R.id.overlay);
		mPreview = (CameraPreview) findViewById(R.id.preview);
		mOverlayOpacity = (SeekBar) findViewById(R.id.overlayOpacity);

		mCamera = Camera.open();
		mPreview.setCamera(mCamera);
		mPreview.setOverlay(mOverlay);

		mGestureDetector = new GestureDetector(this, new CameraGestureListener(
				this));

		OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		};

		mPreview.setOnTouchListener(gestureListener);

		mOverlayOpacity.setProgress(100);
		mOverlayOpacity.setOnSeekBarChangeListener(mOpacityListener);
	}

	@Override
	protected void onResume() {
		super.onResume();

		try {
			mCamera = Camera.open();
			mPreview.setCamera(mCamera);
		} catch (Exception e) {
			Log.d(TAG, "Error opening the camera: " + e.getMessage());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mCamera != null) {
			mPreview.setCamera(null);
			mCamera.release();
			mCamera = null;
		}
	}

	private SeekBar.OnSeekBarChangeListener mOpacityListener = new SeekBar.OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			mOverlay.setOpacity(progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		};
	};

	public void toggleControls() {
		switch (mOverlayOpacity.getVisibility()) {
		case SeekBar.INVISIBLE:
			mOverlayOpacity.setVisibility(SeekBar.VISIBLE);
			break;
		case SeekBar.VISIBLE:
			mOverlayOpacity.setVisibility(SeekBar.INVISIBLE);
			break;
		}
	}

}