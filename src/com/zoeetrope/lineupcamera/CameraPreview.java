package com.zoeetrope.lineupcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

public class CameraPreview extends SurfaceView implements Callback,
		View.OnClickListener {

	public static final int MEDIA_TYPE_IMAGE = 1;
	private final String TAG = "CameraPreview";

	private Camera mCamera;
	private SurfaceHolder mHolder;
	private CameraOverlay mOverlay;

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
			if (pictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions");
				return;
			}

			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();

				mOverlay.setImage(BitmapFactory.decodeFile(pictureFile
						.getAbsolutePath()));

				camera.stopPreview();
				camera.startPreview();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}
		}

	};

	public CameraPreview(Context context) {
		super(context);

		initialize();
	}

	public CameraPreview(Context context, AttributeSet attributes) {
		super(context, attributes);

		initialize();
	}

	public CameraPreview(Context context, AttributeSet attributes, int defStyle) {
		super(context, attributes, defStyle);

		initialize();
	}

	private void initialize() {
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		this.setOnClickListener(this);
	}

	public void setCamera(Camera camera) {
		this.mCamera = camera;
		requestLayout();
	}

	public void setOverlay(CameraOverlay overlay) {
		this.mOverlay = overlay;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();

				requestLayout();
			} catch (IOException e) {
				Log.d(TAG, "Error setting camera preview: " + e.getMessage());
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (mHolder.getSurface() == null) {
			return;
		}

		try {
			mCamera.stopPreview();
		} catch (Exception e) {
		}

		try {
			Camera.Parameters parameters = mCamera.getParameters();
			List<Camera.Size> sizes = parameters.getSupportedPictureSizes();

			Camera.Size cs = sizes.get(0);
			parameters.setPictureSize(cs.width, cs.height);

			sizes = parameters.getSupportedPreviewSizes();
			cs = getOptimalPreviewSize(sizes);

			setLayoutParams(new FrameLayout.LayoutParams(cs.width, cs.height));
			parameters.setPreviewSize(cs.width, cs.height);
			mOverlay.setSize(cs.width, cs.height);

			parameters.setJpegQuality(90);

			if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					setDisplayOrientation(mCamera, 90);
				}
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					setDisplayOrientation(mCamera, 0);
				}
			} else {
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					parameters.set("orientation", "portrait");
					parameters.set("rotation", 90);
				}
				if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
					parameters.set("orientation", "landscape");
					parameters.set("rotation", 90);
				}
			}

			mCamera.setPreviewDisplay(mHolder);
			mCamera.setParameters(parameters);
			mCamera.startPreview();

			mOverlay.invalidate();

			requestLayout();
		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	private Size getOptimalPreviewSize(List<Size> previewSizes) {
		Camera.Size optimalSize = null;

		Collections.sort(previewSizes, new Comparator<Camera.Size>() {
			@Override
			public int compare(Size lhs, Size rhs) {
				return rhs.height - lhs.height;
			}
		});

		Iterator<Size> iterator = previewSizes.iterator();

		while (iterator.hasNext()) {
			Camera.Size cs = iterator.next();

			if (cs.height <= getHeight()) {
				optimalSize = cs;
				break;
			}
		}

		return optimalSize;
	}

	protected void setDisplayOrientation(Camera camera, int angle) {
		Method downPolymorphic;
		try {
			downPolymorphic = camera.getClass().getMethod(
					"setDisplayOrientation", new Class[] { int.class });
			if (downPolymorphic != null)
				downPolymorphic.invoke(camera, new Object[] { angle });
		} catch (Exception e1) {
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {

	}

	@Override
	public void onClick(View v) {
		if (mCamera != null) {
			mCamera.takePicture(null, null, null, mPicture);
		}
	}

	private static File getOutputMediaFile(int type) {
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("LineUpCamera", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

}
