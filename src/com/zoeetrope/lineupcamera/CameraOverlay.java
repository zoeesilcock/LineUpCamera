package com.zoeetrope.lineupcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class CameraOverlay extends View {

	private final String TAG = "CameraOverlay";
	Bitmap mImage;
	int width;
	int height;
	int mOpacity = 100;

	public CameraOverlay(Context context) {
		super(context);
	}

	public CameraOverlay(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public CameraOverlay(Context context, AttributeSet attributes, int defStyle) {
		super(context, attributes, defStyle);
	}

	protected void onDraw(Canvas c) {
		if (mImage != null) {
			Rect dest = new Rect(0, 0, getWidth(), getHeight());
			Paint paint = new Paint();
			paint.setFilterBitmap(true);
			paint.setAlpha(mOpacity);
			Log.d(TAG, "Width: " + getWidth() + " Height: " + getHeight());
			c.drawBitmap(mImage, null, dest, paint);

			super.onDraw(c);
		}
	}

	public void setImage(Bitmap image) {
		Log.d(TAG,
				"Image Width: " + image.getWidth() + " Height: "
						+ image.getHeight());
		mImage = this.getResizedBitmap(image, this.width, this.height);

		invalidate();
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;

		setLayoutParams(new FrameLayout.LayoutParams(width, height));
	}

	public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);

		return resizedBitmap;
	}

	public void setOpacity(int opacity) {
		this.mOpacity = opacity;
		invalidate();
	}

}
