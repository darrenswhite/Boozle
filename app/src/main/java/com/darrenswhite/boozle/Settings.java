package com.darrenswhite.boozle;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Darren White
 */
public class Settings {

	public static final String SHOW_ANIMATIONS = "show_anims";
	public static final String SHOW_DESCRIPTIONS = "show_descs";
	public static final String SHOW_PLAYERS = "show_players";
	public static final String NEXT_ACTION_PERIOD = "next_action_period";
	private static final String TAG = "Settings";
	private static final Properties prop = new Properties();

	public static String getProperty(String key) {
		return prop.getProperty(key);
	}

	private static File getSettingsFile(Context c) {
		return new File(c.getDir("boozle", Context.MODE_PRIVATE), "settings.dat");
	}

	public static void load(Context c) throws IOException {
		File f = getSettingsFile(c);

		if (f.exists()) {
			prop.load(new FileInputStream(f));
		} else {
			setProperty(SHOW_ANIMATIONS, true);
			setProperty(SHOW_DESCRIPTIONS, false);
			setProperty(SHOW_PLAYERS, true);
			setProperty(NEXT_ACTION_PERIOD, 30);
			prop.store(new FileOutputStream(f), null);
		}
	}

	public static void setAndStore(Context c, String key, Object value) {
		try {
			setProperty(key, value);
			store(c);
		} catch (IOException ex) {
			Log.e(TAG, "Unable to set and store setting: " + key);
		}
	}

	public static synchronized Object setProperty(String key, Object value) {
		return prop.setProperty(key, String.valueOf(value));
	}

	public static void store(Context c) throws IOException {
		prop.store(new FileOutputStream(getSettingsFile(c)), null);
	}
}