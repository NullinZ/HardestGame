package com.nullin.hardestgame.view;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.nullin.hardestgame.C;
import com.nullin.hardestgame.engine.GameEngine;
import com.nullin.hardestgame.utilites.Configuration;

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

	private int overFPS, allFPS;
	private SurfaceHolder mHolder;
	private GameEngine mGameEngine;

	public GameView(Context context) {
		super(context);
		mGameEngine = GameEngine.getEngine();
		mHolder = getHolder();
		mHolder.addCallback(this);
		setKeepScreenOn(true);

	}

	public void draw(Canvas canvas) {
		mGameEngine.getMap().drawMapWithBg(canvas);
		for (int i = 0; i < mGameEngine.getBalls().length; i++) {
			if (mGameEngine.getBalls()[i] != null)
				mGameEngine.getBalls()[i].draw(canvas);
		}

		for (int i = 0; i < mGameEngine.getCoins().length; i++) {
			if (mGameEngine.getCoins()[i] != null)
				mGameEngine.getCoins()[i].draw(canvas);
		}
		// canvas.drawText("DEATH: " + death, width - 100, 20, paint);
		mGameEngine.getHero().draw(canvas);
		if (Configuration.isRockerOn)
			mGameEngine.getPad().draw(canvas);
	}

	public void run() {
		Canvas canvas;
		long dt = 0;

		while (GameEngine.gameState != C.GAME_STATE_OVER) {
			canvas = null;
			allFPS++;
			if (dt < C.SPF) {
				dt = C.SPF - dt;
			} else {
				overFPS++;
				System.out.println("OVER:" + dt + " \tRATE:" + overFPS + "/" + allFPS + " \trate:"
						+ (overFPS * 100.0f / allFPS) + "%");
				dt = 0;
			}
			try {
				Thread.sleep(dt);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			dt = System.currentTimeMillis();
			if (GameEngine.gameState == C.GAME_STATE_RUNNING) {
				try {
					canvas = mHolder.lockCanvas();
					synchronized (this.mHolder) {
						draw(canvas);
					}
				} catch (Exception e) {
					// e.printStackTrace();
				} finally {
					if (canvas != null)
						mHolder.unlockCanvasAndPost(canvas);
				}
			}
			dt = System.currentTimeMillis() - dt;
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	public void surfaceCreated(SurfaceHolder holder) {
		new Thread(this).start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		GameEngine.gameState = C.GAME_STATE_OVER;
	}
}
