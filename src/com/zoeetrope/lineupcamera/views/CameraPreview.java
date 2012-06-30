package com.zoeetrope.lineupcamera.views;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.zoeetrope.lineupcamera.daos.ImageDAO;
import com.zoeetrope.lineupcamera.models.Album;
import com.zoeetrope.lineupcamera.models.Image;

public class CameraPreview extends SurfaceView implements Callback,
		View.OnClickListener {

	private final static String TAG = "CameraPreview";

	private Camera mCamera;
	private SurfaceHolder mHolder;
	private CameraOverlay mOverlay;
	private Album mAlbum;
	private boolean mPreviewRunning;

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
		mAlbum = new Album("");

		setupSurface(getHolder());
		this.setOnClickListener(this);
	}

	public void setCamera(Camera camera) {
		this.mCamera = camera;
		startCameraPreview();
	}

	public void setOverlay(CameraOverlay overlay) {
		this.mOverlay = overlay;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder.getSurface() != null) {
			setupSurface(holder);

			if (mCamera != null) {
				startCameraPreview();

				requestLayout();
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		setupSurface(holder);

		if (mHolder.getSurface() != null && mCamera != null) {
			startCameraPreview();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		stopCameraPreview();
		mHolder = null;
	}

	@SuppressWarnings("deprecation")
	private void setupSurface(SurfaceHolder holder) {
		mHolder = holder;
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	private void startCameraPreview() {
		try {
			Camera.Parameters parameters = mCamera.getParameters();
			Camera.Size thumbnailSize = getOptimalThumbnailSize(parameters);
			Camera.Size previewSize = getOptimalPreviewSize(parameters);
			Camera.Size pictureSize = getOptimalPictureSize(parameters);

			parameters.setPictureSize(pictureSize.width, pictureSize.height);
			parameters.setJpegQuality(95);

			parameters.setJpegThumbnailSize(thumbnailSize.width,
					thumbnailSize.height);
			parameters.setJpegThumbnailQuality(80);

			setDisplayOrientation(mCamera, 0);
			setLayoutParams(new FrameLayout.LayoutParams(previewSize.width,
					previewSize.height));
			parameters.setPreviewSize(previewSize.width, previewSize.height);
			mOverlay.setSize(previewSize.width, previewSize.height);

			mCamera.setPreviewDisplay(mHolder);
			mCamera.setParameters(parameters);
			mCamera.startPreview();
			mPreviewRunning = true;

			new LoadImageTask().execute();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, "Error starting camera preview: " + e.getMessage() + " "
					+ e.getClass().toString());
		}
	}

	private void stopCameraPreview() {
		if (mCamera != null && mPreviewRunning) {
			mCamera.stopPreview();
			mPreviewRunning = false;
		}
	}

	private Camera.Size getOptimalPictureSize(Camera.Parameters parameters) {
		List<Size> pictureSizes = parameters.getSupportedPictureSizes();

		Collections.sort(pictureSizes, new Comparator<Camera.Size>() {
			@Override
			public int compare(Size lhs, Size rhs) {
				return rhs.height - lhs.height;
			}
		});

		return pictureSizes.get(0);
	}

	private Camera.Size getOptimalPreviewSize(Camera.Parameters parameters) {
		List<Size> previewSizes = parameters.getSupportedPreviewSizes();
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

	private Camera.Size getOptimalThumbnailSize(Camera.Parameters parameters) {
		List<Size> thumbnailSizes = parameters.getSupportedJpegThumbnailSizes();
		Camera.Size optimalSize = null;

		Collections.sort(thumbnailSizes, new Comparator<Camera.Size>() {
			@Override
			public int compare(Size lhs, Size rhs) {
				return lhs.width - rhs.width;
			}
		});

		Iterator<Size> iterator = thumbnailSizes.iterator();
		while (iterator.hasNext()) {
			Camera.Size cs = iterator.next();

			if (cs.width >= 300) {
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
	public void onClick(View v) {
		if (mCamera != null) {
			mCamera.takePicture(null, null, null, mPicture);
		}
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			ImageDAO imageDAO = new ImageDAO();
			imageDAO.create(mAlbum, data);

			new LoadImageTask().execute();

			camera.startPreview();
		}

	};

	public void setAlbumName(String name) {
		mAlbum.setName(name);
	}

	private class LoadImageTask extends AsyncTask<Void, Integer, Bitmap> {

		private Image latestImage = mAlbum.getLatestImage();
		private int mOrientation;

		@Override
		protected Bitmap doInBackground(Void... params) {
			if (latestImage != null) {
				mOrientation = latestImage.getOrientation();

				if (mOrientation != 0) {
					return latestImage.getResizedBitmap(mOverlay.getHeight(),
							mOverlay.getWidth());
				} else {
					return latestImage.getResizedBitmap(mOverlay.getWidth(),
							mOverlay.getHeight());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result != null) {
				mOverlay.setImage(result, mOrientation);
				requestLayout();
			}
		}
	};

}
