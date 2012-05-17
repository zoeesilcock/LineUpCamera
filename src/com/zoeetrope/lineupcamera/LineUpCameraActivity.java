package com.zoeetrope.lineupcamera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class LineUpCameraActivity extends Activity {

	private final String TAG = "LineUpCameraActivity";
	private Camera mCamera;
	private int mCurrentCameraId = 0;
	private CameraPreview mPreview;
	private CameraOverlay mOverlay;
	private LinearLayout mControls;
	private SeekBar mOverlayOpacity;
	private ImageButton mSwitchCamera;
	private GestureDetector mGestureDetector;
	private Bundle mExtras;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.camera);

		mOverlay = (CameraOverlay) findViewById(R.id.overlay);
		mPreview = (CameraPreview) findViewById(R.id.preview);
		mControls = (LinearLayout) findViewById(R.id.controls);
		mOverlayOpacity = (SeekBar) findViewById(R.id.overlayOpacity);
		mSwitchCamera = (ImageButton) findViewById(R.id.switchCamera);

		mPreview.setOverlay(mOverlay);

		mExtras = getIntent().getExtras();
		if (mExtras != null) {
			mPreview.setAlbumName(mExtras.getString("ALBUM"));
		}

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

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& Camera.getNumberOfCameras() > 1) {
			mSwitchCamera.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					int numberOfCameras = Camera.getNumberOfCameras();

					if (mCamera != null) {
						mCamera.stopPreview();
						mCamera.release();
						mCamera = null;
					}

					mCurrentCameraId = (mCurrentCameraId + 1) % numberOfCameras;
					openCamera();
				}

			});
		} else {
			mSwitchCamera.setVisibility(View.GONE);
		}
	}

	private void openCamera() {
		OpenCameraTask task = new OpenCameraTask();
		task.execute();
	}

	private class OpenCameraTask extends AsyncTask<Void, Integer, Camera> {

		@Override
		protected Camera doInBackground(Void... params) {
			Camera camera = null;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				camera = Camera.open(mCurrentCameraId);
			} else {
				camera = Camera.open();
			}

			return camera;
		}

		@Override
		protected void onPostExecute(Camera result) {
			mCamera = result;
			mPreview.setCamera(mCamera);
		}

	};

	@Override
	protected void onResume() {
		super.onResume();

		try {
			openCamera();
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
		switch (mControls.getVisibility()) {
		case LinearLayout.INVISIBLE:
			mControls.setVisibility(LinearLayout.VISIBLE);
			break;
		case LinearLayout.VISIBLE:
			mControls.setVisibility(LinearLayout.INVISIBLE);
			break;
		}
	}

}