package com.snulife.snulife.common.toast;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.widget.Toast;

import com.snulife.snulife.app.SNULifeApplication;
import com.snulife.snulife.common.utils.CommonUtils;


/**
 * @author jaeyoung.woo
 */
public class ToastManager {
	private static ToastManager sToastManager;

	/** 80dp **/
	public static final int DEFAULT_OFFSET;
	
	static {
		DEFAULT_OFFSET = CommonUtils.convertDipToPx(80);
	}
	
	public static ToastManager getInstance() {
		synchronized (ToastManager.class) {
			if (sToastManager == null) {
				synchronized (ToastManager.class) {
					sToastManager = new ToastManager();
				}
			}
			return sToastManager;
		}
	}

	private ToastDefault mToast;
	private ToastHandler mToastHandler;
	private ToastParameters mToastParameters;

	public ToastManager() {
		mToastHandler = new ToastHandler();
		mToastParameters = new ToastParameters();
		mToastHandler.sendMessage(mToastHandler.obtainMessage(0, null));
	}

	public void makeAndShowToastOnMainThread(CharSequence title, CharSequence content, int gravity, int offset, int duration, String imageName, Bitmap bmp) {
		mToastParameters.title = title;
		mToastParameters.content = content;
		mToastParameters.gravity = gravity;
		mToastParameters.offset = offset;
		mToastParameters.duration = duration;
		mToastParameters.imageName = imageName;
		mToastParameters.bmp = bmp;
		mToastHandler.sendMessage(mToastHandler.obtainMessage(1, mToastParameters));
	}

	public void makeAndShowToastOnMainThread(CharSequence title, CharSequence content, int gravity, int offset, int duration, Bitmap bmp) {
		makeAndShowToastOnMainThread(title, content, gravity, offset, duration, null, bmp);
	}

	public void makeAndShowToastOnMainThread(CharSequence title, CharSequence content, int gravity, int offset, int duration, String imageName) {
		makeAndShowToastOnMainThread(title, content, gravity, offset, duration, imageName, null);
	}
	
	public void makeAndShowToastOnMainThread(CharSequence title, CharSequence content, int gravity, int offset, int duration) {
		makeAndShowToastOnMainThread(title, content, gravity, offset, duration, null, null);
	}

	public void makeAndShowSimpleToastOnMainThread(CharSequence title, CharSequence message) {
		makeAndShowToastOnMainThread(title, message, Gravity.BOTTOM, DEFAULT_OFFSET, Toast.LENGTH_SHORT);
	}
	
	public void makeAndShowSimpleToastOnMainThread(CharSequence message) {
		makeAndShowToastOnMainThread(null, message, Gravity.BOTTOM, DEFAULT_OFFSET, Toast.LENGTH_SHORT);
	}

	/**
	 * Gravity.BOTTOM and offset 100dp.
	 */
	public void makeAndShowToastOnMainThread(String title, String content, int duration, Bitmap bmp) {
		makeAndShowToastOnMainThread(title, content, Gravity.BOTTOM, DEFAULT_OFFSET, duration, bmp);
	}

	private class ToastHandler extends Handler {
		public ToastHandler() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mToast = new ToastDefault(SNULifeApplication.getContext());
				break;
			case 1:
				if (mToast != null) {
					ToastParameters toastParameters = (ToastParameters) msg.obj;
					if (toastParameters != null) {
						ToastDefault toast = mToast;
						if (toastParameters.title == null) {
							toast.setTitleVisibility(false);
						} else {
							toast.setTitleVisibility(true);
							toast.setTitle(toastParameters.title);
						}
						toast.setTitle(toastParameters.title);
						toast.setContent(toastParameters.content);
						toast.setGravity(toastParameters.gravity, toastParameters.offset);
						toast.setDuration(toastParameters.duration);
						toast.setIconVisibility(false);
						
						if (toastParameters.bmp != null) {
							toast.setIconVisibility(true);
							toast.setImageBitmap(toastParameters.bmp);
						} else if (toastParameters.imageName != null) {
							toast.setIconVisibility(true);
							toast.setImage(toastParameters.imageName);
						}
						toast.show();
					}
				}
				break;
			}
		}
	}

	private class ToastParameters {
		public CharSequence title;
		public CharSequence content;
		public int gravity;
		public int offset;
		public int duration;
		public String imageName;
		public Bitmap bmp;
	}

	public void simpleToast(String string) {
		makeAndShowSimpleToastOnMainThread(string);
	}
	
	public void simpleToast(int resId) {		
		makeAndShowSimpleToastOnMainThread(SNULifeApplication.getContext().getString(resId));
	}
	
	public void simpleToast(String title, String string) {
		makeAndShowSimpleToastOnMainThread(title, string);
	}
}
