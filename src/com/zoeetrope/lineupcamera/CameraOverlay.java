package com.zoeetrope.lineupcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class CameraOverlay extends View {

	Bitmap mImage;
	
	public CameraOverlay(Context context) {
		super(context);
	}
	
	protected void onDraw(Canvas c) {
		if(mImage != null) {
			Rect dest = new Rect(0, 0, getWidth(), getHeight());
			Paint paint = new Paint();
			paint.setFilterBitmap(true);
			paint.setAlpha(100);
			
			c.drawBitmap(mImage, null, dest, paint);
			
			super.onDraw(c); 
		}
	}
	
	public void setImage(Bitmap image) {
		mImage = image;
		invalidate();
	}

}
