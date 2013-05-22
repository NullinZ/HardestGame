package com.nullin.hardestgame.sprit;

import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.nullin.hardestgame.C;
import com.nullin.hardestgame.map.Map;

public class Hero extends Sprite {
	private float r;
	private Paint mPaint;
	private int direction = 0;
	private Rect border;
	private int dieTimes = 0;
	private boolean dying = false;
	private int blurW = 2;
	private float velocity = C.HERO_VELOCITY;

	public Hero(float x, float y, float r) {
		super();
		startX = this.x = x;
		startY = this.y = y;
		this.r = r;
		this.mPaint = new Paint();
		mPaint.setColor(Color.RED);
		mPaint.setMaskFilter(new BlurMaskFilter(blurW, Blur.NORMAL));
		border = new Rect();
	}

	@Override
	public boolean isHit(float x, float y) {
		RectF rect = new RectF(this.x - r, this.y - r, this.x + r, this.y + r);
		return rect.contains(x, y);
	}

	public boolean isHit(RectF rect) {
		RectF rectf = new RectF(this.x - r, this.y - r, this.x + r, this.y + r);
		return rectf.contains(rect);
	}

	public void resetBorder() {
		border.left = (int) (this.x - r);
		border.right = (int) (this.x + r);
		border.top = (int) (this.y - r);
		border.bottom = (int) (this.y + r);
	}

	public void die() {
		dying = true;
		for (int i = 10; i > 0; i--) {
			mPaint.setAlpha(i * 25);
			try {
				Thread.sleep(C.SPF);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mPaint.setAlpha(255);
		this.x = startX;
		this.y = startY;
		dieTimes++;
		dying = false;

	}

	public int dieTimes() {
		return dieTimes;
	}

	@Override
	public void updatePosition() {

	}

	public void move(Map map, int direction) {
		if (dying)
			return;
		float dx = 0, dy = 0;
		if ((direction & C.TOUCH_UP) != 0 && map.canPass(this, x, y - velocity - r)) {// 上
			dy = -velocity;
		} else if ((direction & C.TOUCH_DOWN) != 0 && map.canPass(this, x, y + velocity + r)) {// 下
			dy = velocity;
		}

		if ((direction & C.TOUCH_LEFT) != 0 && map.canPass(this, x - velocity - r, y)) {// 左
			dx = -velocity;
		} else if ((direction & C.TOUCH_RIGHT) != 0 && map.canPass(this, x + velocity + r, y)) {// 右
			dx = velocity;
		}
		move(dx, dy);
		resetBorder();
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(x - r, y - r, x + r, y + r, mPaint);
	}

	public Rect getBorder() {
		return border;
	}

	public float getR() {
		return r;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int getDirection() {
		return direction;
	}

	public int getBlurW() {
		return blurW;
	}

	public void setAlpha(int alpha) {
		mPaint.setAlpha(alpha);
	}

	public void setBlurW(int blurW) {
		this.blurW = blurW;
		mPaint.setMaskFilter(new BlurMaskFilter(blurW, Blur.NORMAL));
	}

}
