package com.snulife.snulife.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.parse.Parse;
import com.parse.PushService;
import com.snulife.snulife.R;
import com.snulife.snulife.app.activity.MainActivity;
import com.snulife.snulife.common.ThreadManager;

public class SNULifeApplication extends Application {
	
	private static SNULifeApplication instance = new SNULifeApplication();
	public static boolean is_push;
	public static int VERSION_CODE;
	public static String VERSION_NAME;
	public static String CLIENT_VERSION;
	public static String DEVICE_TYPE;
	public static String DEVICE_ID;
	public static boolean DEBUGGABLE;
	public static int SDK_INT;
	public static int TARGET;
	public static String DEVICE_MODEL;

	public SNULifeApplication() {
		instance = this;
	}

	public static Context getContext() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initialize();
		Parse.initialize(this, "qPNrSuFoL0BQTgXt3srfVHRQbCxznAK1LUPormxF", "1w7yQb9BwoJeDCEjuNstffQ6iMLFMdFo9rKFUAp7");

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		is_push = pref.getBoolean("pushnotify", true);

		if (is_push == true) {
			PushService.setDefaultPushCallback(this, MainActivity.class, R.drawable.icon_notification);
			PushService.subscribe(this, "", MainActivity.class);
		}
		else {
			PushService.setDefaultPushCallback(this, null);
	    	PushService.unsubscribe(this, "");
		}
	}

	private static void init() {
		VERSION_CODE = versionCode();
		VERSION_NAME = versionName();
		CLIENT_VERSION = clientVersion();
		DEVICE_TYPE = devideType();
		DEVICE_ID = deviceId();
		DEBUGGABLE = isDebuggable();
		SDK_INT = SDKINT();
		TARGET = TARGET();
		DEVICE_MODEL = (""+Build.MANUFACTURER + Build.MODEL).replace(" ","").toUpperCase();
		if (DEBUGGABLE) {
		}
	}


	private static int versionCode() {
		try {
			return instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			return 10000;
		}
	}

	private static String versionName() {
		try {
			return instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return null;
		}
	}
	
	private static String clientVersion() {
		String[] tmp = VERSION_NAME.split("\\.");
		String tmp2 = String.format("A%02d%02d%02d", Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]), Integer.parseInt(tmp[2]));
		return tmp2;
	}
	
	private static String devideType() {
		return "A";
	}

	private static String deviceId() {
        String deviceId = "";
        boolean na = false;
        TelephonyManager telManager = (TelephonyManager) instance.getSystemService(Context.TELEPHONY_SERVICE);
        deviceId = "0";
        if (telManager != null) {
            deviceId = telManager.getDeviceId();
            if (deviceId == null) {
                deviceId = "0";
            } else if (deviceId.equals("N/A")) {
                na = true;
                deviceId = "0";
            }
        }
        if (deviceId.equals("0")) {
            if (SDK_INT >= 8) {
                deviceId = Secure.getString(instance.getContentResolver(), Secure.ANDROID_ID);
                if (deviceId == null) {
                    deviceId = "0";
                }
            }
        }
        if (na && deviceId.equals("0")) {
            deviceId = "N/A";
        }
        return deviceId;
    }

	private static int SDKINT() {
		return android.os.Build.VERSION.SDK_INT;
	}
	
	private static int TARGET() {
		return Integer.parseInt(android.os.Build.VERSION.RELEASE.replaceAll("\\.", ""));
	}

	public static final boolean isDebuggable() {
		try {
			int flags = instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0).applicationInfo.flags;
			boolean isDebugMode = (flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
			return isDebugMode;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
	
	private void initialize() {
		ThreadManager.setMainThread(Thread.currentThread());
		init();
	}
}