package com.snulife.snulife.app.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ActivityNavigation {
	
	public static void goToBrowser(Context context, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		context.startActivity(intent);
	}
}