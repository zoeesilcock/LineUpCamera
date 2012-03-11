package com.zoeetrope.lineupcamera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

public class LineUpCameraActivity extends Activity {

	private final String TAG = "LineUpCameraActivity";
	Camera mCamera;
	CameraPreview mPreview;
	CameraOverlay mOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mCamera = Camera.open();
		mOverlay = new CameraOverlay(this);
		mPreview = new CameraPreview(this, mCamera, mOverlay);
		
		setContentView(mPreview);
		addContentView(mOverlay, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
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

}