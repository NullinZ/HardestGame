package com.nullin.hardestgame;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;

import com.nullin.hardestgame.utilites.Configuration;

//import com.cosina.game.crystallight.ui.activity.ActivityStatus;
//import com.cosina.game.crystallight.util.World;

public class MediaPlayerAdapter {

	private static SoundPool pool = new SoundPool(20, AudioManager.STREAM_MUSIC, 1);
	MediaPlayer mp;
	private static Map<Integer, Integer> key2key = new HashMap<Integer, Integer>();
	private static HGActivity mHG;

	public static void init(HGActivity context) {
		mHG = context;
		for (Field each : R.raw.class.getDeclaredFields()) {
			try {
				int resid = (Integer) each.get(null);
				key2key.put(resid, pool.load(context, resid, 1));
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("cosina1985", "can't load resource with ", e);
			}
		}
	}

	private static void play(int resId) {
		if (Configuration.soundOn) {
			pool.play(key2key.get(resId), 1f, 1f, 1, 0, 1);
		}
	}

	public static void getCoin() {
		play(R.raw.coin);
	}

	public static void die() {
		play(R.raw.die);
	}

	public static void playMusic() {
		// pool.play(key2key.get(R.raw.bg_music), 1, 1, 1, 0, 1);
		new Thread() {
			public void run() {

				MediaPlayer player = MediaPlayer.create(mHG, R.raw.bg_music);// new
				// MediaPlayer();
				player.setAudioStreamType(AudioManager.STREAM_MUSIC);
				player.setLooping(true);
				try {
					player.prepare();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				player.start();
				System.out.println("Start .=--------------------------");
			};
		}.start();
	}

}
