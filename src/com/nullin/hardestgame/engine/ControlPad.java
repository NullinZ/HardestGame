package com.nullin.hardestgame.engine;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.nullin.hardestgame.C;

public class ControlPad {

	public final int MAX_RANG = 50;
	public final float SENSITIVITY = 3f;

	private Bitmap padBorder;
	private Bitmap padBorderN;
	private Bitmap padBorderA;
	private Bitmap padNormal;
	private Bitmap padActive;
	private Bitmap pad;
	private float padCenterX, padCenterY, mPadX, mPadY;
	private int padBorderWHalf, padBorderHHalf;
	private int padWidth, padHeight;
	private int direction = C.DIRECTION_NULL;
	private boolean touchDown = false;

	private ResourceManager mRM;

	public ControlPad(float x, float y) {
		super();
		mPadX = this.padCenterX = x;
		mPadY = this.padCenterY = y;

		mRM = ResourceManager.getManager();
		mRM.initPadRes();
		initRes();
	}

	private void initRes() {
		padBorder = padBorderN = mRM.rPadBorder;
		padBorderA = mRM.rPadBorderA;

		padBorderWHalf = padBorderA.getWidth() >> 1;
		padBorderHHalf = padBorderA.getHeight() >> 1;

		pad = padNormal = mRM.rPadNormal;
		padActive = mRM.rPadActive;

		padWidth = padNormal.getWidth() >> 1;
		padHeight = padNormal.getHeight() >> 1;
	}

	public boolean doTouchEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		int padR = (int) Math.sqrt(((padCenterX - x) * (padCenterX - x) + (padCenterY - y) * (padCenterY - y)));
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			pad = padActive;
			padBorder = padBorderA;
			touchDown = true;
			break;
		case MotionEvent.ACTION_MOVE:
			if (!touchDown)
				return false;
			if (padR > MAX_RANG) {
				mPadX = MAX_RANG * (x - padCenterX) / padR + padCenterX;
				mPadY = MAX_RANG * (y - padCenterY) / padR + padCenterY;
			} else {
				mPadX = x;
				mPadY = y;
			}
			if (mPadX - padCenterX > 0 && mPadX - padCenterX > MAX_RANG / SENSITIVITY) {
				direction = direction | C.TOUCH_RIGHT;
				direction = direction & C.TOUCH_LEFT_CANCEL;
			} else if (mPadX - padCenterX < 0 && mPadX - padCenterX < -MAX_RANG / SENSITIVITY) {
				direction = direction | C.TOUCH_LEFT;
				direction = direction & C.TOUCH_RIGHT_CANCEL;
			} else {
				direction = direction & C.TOUCH_RIGHT_CANCEL;
				direction = direction & C.TOUCH_LEFT_CANCEL;
			}
			if (mPadY - padCenterY > 0 && mPadY - padCenterY > MAX_RANG / SENSITIVITY) {
				direction = direction | C.TOUCH_DOWN;
				direction = direction & C.TOUCH_UP_CANCEL;
			} else if (mPadY - padCenterY < 0 && mPadY - padCenterY < -MAX_RANG / SENSITIVITY) {
				direction = direction | C.TOUCH_UP;
				direction = direction & C.TOUCH_DOWN_CANCEL;
			} else {
				direction = direction & C.TOUCH_DOWN_CANCEL;
				direction = direction & C.TOUCH_UP_CANCEL;
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			touchDown = false;
			reset();
			break;

		default:
			break;
		}
		KeyThread.forward(direction);
		return true;
	}

	public void reset() {
		mPadX = padCenterX;
		mPadY = padCenterY;
		direction = C.DIRECTION_NULL;
		pad = padNormal;
		padBorder = padBorderN;
	}

	public void draw(Canvas canvas) {
		if (canvas != null) {
			canvas.drawBitmap(padBorder, padCenterX - padBorderWHalf, padCenterY - padBorderHHalf, null);
			canvas.drawBitmap(pad, mPadX - padWidth, mPadY - padHeight, null);
		}
	}
}
