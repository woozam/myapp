package com.snulife.snulife.common.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.snulife.snulife.app.SNULifeApplication;

public class CommonUtils {
	
	public static DisplayMetrics DISPLAY_METRICS;

	public static void copyClipBoard(CharSequence text) {
		ClipData clipData = ClipData.newPlainText("text", text);
		ClipboardManager clipboard = (ClipboardManager) SNULifeApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
		clipboard.setPrimaryClip(clipData);
	}
	
	public static int convertDipToPx(int dip) {
		if (DISPLAY_METRICS == null)
			DISPLAY_METRICS = SNULifeApplication.getContext().getResources().getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, DISPLAY_METRICS);
	}
}
