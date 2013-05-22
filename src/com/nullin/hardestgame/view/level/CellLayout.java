package com.nullin.hardestgame.view.level;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CellLayout extends ViewGroup {

	Drawable drawable;

	public CellLayout(Context context, AttributeSet attr) {
		super(context, attr);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}

	ImageView iv;

	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		for (int i = 0; i < getChildCount(); i++) {
			iv = (ImageView) getChildAt(i);
			int marginLeft = ((getWidth() / 3) - 200) / 2;
			int marginTop = ((getHeight() / 2) - 150) / 2;
			if (iv.getVisibility() != GONE)
				iv.layout(i % 3 * (getWidth() / 3) + marginLeft, i / 3 * (getHeight() / 2) + marginTop, i % 3
						* (getWidth() / 3) + marginLeft + 200, i / 3 * (getHeight() / 2) + marginTop + 150);
		}
	}
}
