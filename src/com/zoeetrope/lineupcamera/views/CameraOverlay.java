package com.zoeetrope.lineupcamera.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class CameraOverlay extends View {

	private Bitmap mImage;
	private int mOpacity = 100;
	private int mOrientation;

	public CameraOverlay(Context context) {
		super(context);
	}

	public CameraOverlay(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public CameraOverlay(Context context, AttributeSet attributes, int defStyle) {
		super(context, attributes, defStyle);
	}

	protected void onDraw(Canvas canvas) {
		if (mImage != null) {
			Matrix matrix = new Matrix();
			Paint paint = new Paint();

			paint.setFilterBitmap(true);
			paint.setAlpha(mOpacity);

			canvas.save();
			if (mOrientation != 0) {
				matrix.setTranslate(-(mImage.getWidth()), 0);
				matrix.postRotate(mOrientation, 0, 0);
			}
			canvas.drawBitmap(mImage, matrix, paint);
			canvas.restore();

			super.onDraw(canvas);
		}
	}

	public void setImage(Bitmap image, int orientation) {
		mImage = image;
		mOrientation = orientation;
		invalidate();
	}

	public void setSize(int width, int height) {
		setLayoutParams(new FrameLayout.LayoutParams(width, height));
	}

	public void setOpacity(int opacity) {
		this.mOpacity = opacity;
		invalidate();
	}

}
