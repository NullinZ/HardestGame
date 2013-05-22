package com.nullin.hardestgame.map.data;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Bitmap.Config;

import com.nullin.hardestgame.C;
import com.nullin.hardestgame.engine.ILevelController;
import com.nullin.hardestgame.map.data.MapData;
import com.nullin.hardestgame.sprit.Sprite;

public class Map {

	Paint mPaint;
	private int mapX, mapY, mapWidth, mapHeight;
	private Bitmap mBgImgGrid;
	private int mapGrid[];
	private Bitmap mWholeMap;
	private int mLevel;
	private ILevelController levelCon;
	private Rect start = new Rect(), end = new Rect(), recordTmp;
	ArrayList<Rect> records;

	public Map() {
		mapX = C.GV_MARGIN_LEFT;
		mapY = C.GV_MARGIN_TOP;
		mapWidth = C.getWidth() - C.GV_MARGIN_LEFT;
		mapHeight = C.getHeight() - C.GV_MARGIN_TOP - C.GV_MARGIN_BOTTOM;
		records = new ArrayList<Rect>();
		initMapGrid();
	}

	public Bitmap toGate(int level) {
		if (mWholeMap == null || mLevel != level) {
			this.mLevel = level;
			mWholeMap = Bitmap.createBitmap(mapWidth, mapHeight, Config.ARGB_8888);
			Canvas c = new Canvas(mWholeMap);
			mapGrid = MapData.maps[mLevel];
			Paint ps = new Paint();
			Paint pr = new Paint();
			Paint pe = new Paint();
			Paint p2 = new Paint();
			ps.setColor(Color.YELLOW);
			ps.setAlpha(150);
			pr.setColor(Color.RED);
			pe.setColor(Color.GREEN);
			pe.setAlpha(150);
			p2.setColor(Color.BLACK);
			int gridX, gridY;
			for (int i = 0; i < mapGrid.length; i++) {
				gridX = i % C.GRID_W_SIZE * C.GRID_SIZE;
				gridY = i / C.GRID_W_SIZE * C.GRID_SIZE;
				if (mapGrid[i] == 1) {
					c.drawRect(gridX, gridY, gridX + C.GRID_SIZE, gridY + C.GRID_SIZE, p2);
				} else if (mapGrid[i] == 0) {
					c.drawBitmap(mBgImgGrid, new Rect(gridX, gridY, gridX + C.GRID_SIZE, gridY + C.GRID_SIZE),
							new Rect(gridX, gridY, gridX + C.GRID_SIZE, gridY + C.GRID_SIZE), null);
				} else if ((mapGrid[i] & C.ZONE_START) != 0) {
					if (!start.contains(gridX, gridY)) {
						start.left = gridX;
						start.right = gridY;
						int endPosition = findZoneEnd(i);
						start.right = endPosition % C.GRID_W_SIZE * C.GRID_SIZE + C.GRID_SIZE;
						start.bottom = endPosition / C.GRID_W_SIZE * C.GRID_SIZE + C.GRID_SIZE;
					}
					c.drawRect(gridX, gridY, gridX + C.GRID_SIZE, gridY + C.GRID_SIZE, ps);
				} else if ((mapGrid[i] & C.ZONE_END) != 0) {
					if (!end.contains(gridX, gridY)) {
						end.left = gridX;
						end.right = gridY;
						int endPosition = findZoneEnd(i);
						end.right = endPosition % C.GRID_W_SIZE * C.GRID_SIZE + C.GRID_SIZE;
						end.bottom = endPosition / C.GRID_W_SIZE * C.GRID_SIZE + C.GRID_SIZE;
					}
					c.drawRect(gridX, gridY, gridX + C.GRID_SIZE, gridY + C.GRID_SIZE, pe);
				} else if ((mapGrid[i] & C.ZONE_RECORD) != 0) {
					if (!recordTmp.contains(gridX, gridY)) {
						recordTmp = new Rect();
						recordTmp.left = gridX;
						recordTmp.right = gridY;
						int endPosition = findZoneEnd(i);
						recordTmp.right = endPosition % C.GRID_W_SIZE * C.GRID_SIZE + C.GRID_SIZE;
						recordTmp.bottom = endPosition / C.GRID_W_SIZE * C.GRID_SIZE + C.GRID_SIZE;
						records.add(recordTmp);
					}
					c.drawRect(gridX, gridY, gridX + C.GRID_SIZE, gridY + C.GRID_SIZE, pr);
				}
			}
		}
		return mWholeMap;
	}

	/**
	 * 生成棋盘格背景
	 */
	public void initMapGrid() {
		Bitmap bm = Bitmap.createBitmap(new int[] { 0x11ffffff, 0xFFEEEEEE, 0xFFEEEEEE, 0x11ffffff }, 2, 2,
				Bitmap.Config.RGB_565);
		Shader bg = new BitmapShader(bm, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		Matrix m = new Matrix();
		m.setScale(30, 30);
		bg.setLocalMatrix(m);
		mBgImgGrid = Bitmap.createBitmap(mapWidth, mapHeight, Config.ARGB_8888);
		Canvas c = new Canvas(mBgImgGrid);
		Paint p = new Paint();
		p.setShader(bg);
		c.drawRect(0, 0, mapWidth, mapHeight, p);
	}

	public boolean canPass(Sprite sprite, float x, float y) {
		if (x < 0 || y < 0 || x > C.getWidth() || y > C.getHeight())
			return false;
		int state = mapGrid[((int) x - C.GV_MARGIN_LEFT) / C.GRID_SIZE + (((int) y - C.GV_MARGIN_TOP) / C.GRID_SIZE)
				* C.GRID_W_SIZE];
		switch (state) {
		case C.ZONE_WALL:
			return false;
		case C.ZONE_END:
			if (levelCon != null)
				levelCon.levelComplete();
		case C.ZONE_RECORD:
			for (int i = 0; i < records.size(); i++) {
				recordTmp = records.get(i);
				if (recordTmp.contains((int) x, (int) y)) {
					sprite.recordPosition(recordTmp.centerX(), recordTmp.exactCenterY());
					break;
				}
			}
		default:
			return true;
		}
	}

	// private int findZoneStart(int point) {
	// int zoneType = mapGrid[point];
	// if (point - 1 >= 0 && mapGrid[point - 1] == zoneType) {
	// point = findZoneStart(point - C.GRID_W_SIZE);
	// } else if (point - C.GRID_W_SIZE >= 0 && mapGrid[point - C.GRID_W_SIZE]
	// == zoneType) {
	// point = findZoneStart(point - 1);
	// }
	// return point;
	// }

	private int findZoneEnd(int point) {
		int zoneType = mapGrid[point];
		if (point + 1 < mapGrid.length && mapGrid[point + 1] == zoneType) {
			point = findZoneEnd(point + C.GRID_W_SIZE);
		} else if (point + C.GRID_W_SIZE < mapGrid.length && mapGrid[point + C.GRID_W_SIZE] == zoneType) {
			point = findZoneEnd(point + 1);
		}
		return point;
	}

	public void drawMap(Canvas canvas) {
		canvas.drawBitmap(mWholeMap, mapX, mapY, null);
	}

	public int getHeight() {
		return mapHeight;
	}

	public int getWidth() {
		return mapWidth;
	}
}
