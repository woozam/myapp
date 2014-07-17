package com.snulife.snulife.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;
import com.snulife.snulife.app.activity.MainActivity;

public class SNULifeApplication extends Application {
	
	private static SNULifeApplication instance = new SNULifeApplication();
	public static boolean is_push;

	public SNULifeApplication() {
		instance = this;
	}

	public static Context getContext() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "qPNrSuFoL0BQTgXt3srfVHRQbCxznAK1LUPormxF", "1w7yQb9BwoJeDCEjuNstffQ6iMLFMdFo9rKFUAp7");

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		is_push =  pref.getBoolean("pushnotify", true);

		if (is_push == true) {
			PushService.setDefaultPushCallback(this, MainActivity.class);
			PushService.subscribe(this, "", MainActivity.class);
		}
		else {
			PushService.setDefaultPushCallback(this, null);
	    	PushService.unsubscribe(this, "");
		}
	}
}