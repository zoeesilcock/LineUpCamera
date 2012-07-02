package com.zoeetrope.lineupcamera.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.zoeetrope.lineupcamera.R;
import com.zoeetrope.lineupcamera.views.CameraOverlay;
import com.zoeetrope.lineupcamera.views.CameraPreview;

public class LineUpCameraActivity extends Activity {

	private final String TAG = "LineUpCameraActivity";
	private Camera mCamera;
	private int mCurrentCameraId = 0;
	private CameraPreview mPreview;
	private CameraOverlay mOverlay;
	private LinearLayout mControls;
	private SeekBar mOverlayOpacity;
	private ImageButton mSwitchCamera;
	private Bundle mExtras;
	public OrientationEventListener mOrientationListener;

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

		private ProgressDialog mDialog;

		@Override
		protected void onPreExecute() {
			String message = LineUpCameraActivity.this.getResources()
					.getString(R.string.starting_camera);
			mDialog = new ProgressDialog(LineUpCameraActivity.this);

			mDialog.setMessage(message);
			mDialog.setIndeterminate(true);
			mDialog.setCancelable(false);
			mDialog.show();
		}

		@Override
		protected Camera doInBackground(Void... params) {
			Camera camera = null;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				camera = Camera.open(mCurrentCameraId);
			} else {
				camera = Camera.open();
			}

			mOrientationListener = new MyOrientationListener(
					LineUpCameraActivity.this);
			mOrientationListener.enable();

			return camera;
		}

		@Override
		protected void onPostExecute(Camera result) {
			mCamera = result;
			mPreview.setCamera(mCamera);
			mDialog.dismiss();
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

			mOrientationListener.disable();
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

	private class MyOrientationListener extends OrientationEventListener {

		public MyOrientationListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {
			if (orientation != ORIENTATION_UNKNOWN && mCamera != null) {
				CameraInfo info = new CameraInfo();
				android.hardware.Camera.getCameraInfo(mCurrentCameraId, info);
				int rotation = 0;

				orientation = (orientation + 45) / 90 * 90;

				if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
					rotation = (info.orientation - orientation + 360) % 360;
				} else {
					rotation = (info.orientation + orientation) % 360;
				}

				Camera.Parameters parameters = mCamera.getParameters();
				parameters.setRotation(rotation);
				mCamera.setParameters(parameters);

				// Calculate the rotation of the switch camera button.
				switch (orientation) {
				case 0:
				case 180:
				case 360:
					orientation -= 90;
					break;
				case 90:
				case 270:
					orientation += 90;
					break;
				}

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
						&& Camera.getNumberOfCameras() > 1) {
					mSwitchCamera.setRotation(orientation);
				}
			}
		}
	}

}