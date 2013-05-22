package com.nullin.hardestgame.sprit;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.BlurMaskFilter.Blur;

import com.nullin.hardestgame.map.ILocus;

public class Ball extends Sprite {
	private ILocus l;
	private float r;
	private Paint paint;
	private Bitmap circle;
	private int blurW = 2;
	private Paint mPaint;

	public Ball(ILocus l, float r) {
		this(0, 0, r, l, -1);
	}

	public Ball(float x, float y, float r, int color) {
		this(x, y, r, null, color);
	}

	public Ball(float x, float y, float r, ILocus l) {
		this(x, y, r, l, -1);
	}

	public Ball(float x, float y, float r, ILocus l, int color) {
		super();
		this.x = x;
		this.y = y;
		this.r = r;
		this.l = l;
		circle = Bitmap.createBitmap((int) r << 2, (int) r << 2, Config.ARGB_8888);
		paint = new Paint();
		paint.setColor(color != -1 ? color : Color.BLUE);
		paint.setAntiAlias(true);
		genBall();
		mPaint = new Paint();
	}

	private void genBall() {
		Canvas c = new Canvas(circle);
		paint.setMaskFilter(new BlurMaskFilter(blurW, Blur.SOLID));
		c.drawColor(Color.TRANSPARENT);
		c.drawCircle(2 * r, 2 * r, r, paint);
	}

	@Override
	public void updatePosition() {
		if (l != null) {
			float[] position = l.getCoordinates();
			moveTo(position[0], position[1]);
		}
	}

	@Override
	public void draw(Canvas canvas) {
		// canvas.drawCircle(x, y, r, paint);
		canvas.drawBitmap(circle, x - 2 * r, y - 2 * r, mPaint);
	}

	@Override
	public boolean isHit(float x, float y) {
		return (r * r >= (x - this.x) * (x - this.x) + (y - this.y) * (y - this.y)) ? true : false;
	}

	public boolean isHit(Rect rect) {
		return isHit(rect.left, rect.top) || isHit(rect.right, rect.top) || isHit(rect.left, rect.bottom)
				|| isHit(rect.right, rect.bottom) || rect.contains((int) this.x, (int) this.y);
	}

	public int getBlurW() {
		return blurW;
	}

	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	public void setBlurW(int blurW) {
		this.blurW = blurW;
		genBall();
	}
}
