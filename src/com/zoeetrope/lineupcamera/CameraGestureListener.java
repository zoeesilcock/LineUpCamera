package com.zoeetrope.lineupcamera;

import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class CameraGestureListener implements OnGestureListener {

	LineUpCameraActivity mActivity;

	public CameraGestureListener(LineUpCameraActivity lineUpCameraActivity) {
		this.mActivity = lineUpCameraActivity;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		mActivity.toggleControls();

		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}
