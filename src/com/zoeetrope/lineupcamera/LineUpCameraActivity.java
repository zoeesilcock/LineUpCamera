package com.zoeetrope.lineupcamera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

public class LineUpCameraActivity extends Activity {

	private final String TAG = "LineUpCameraActivity";
	Camera mCamera;
	CameraPreview mPreview;
	CameraOverlay mOverlay;
	SeekBar mOverlayOpacity;

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
		
		mOverlayOpacity.setProgress(100);
		mOverlayOpacity.setOnSeekBarChangeListener(opacityListener);
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
	
	private SeekBar.OnSeekBarChangeListener opacityListener =
			new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			mOverlay.setOpacity(progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		};
	};

}