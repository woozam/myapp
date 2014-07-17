package com.snulife.snulife.app.activity;

import java.net.URLDecoder;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Xml.Encoding;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseInstallation;
import com.parse.SaveCallback;
import com.snulife.snulife.R;
import com.snulife.snulife.app.SNULifeApplication;

public class MainActivity extends Activity implements OnClickListener {
	private myWebChromeClient mWebChromeClient;

	Menu mMenu;
	public static String objectId;
	public static String DOMAIN = "http://snulife.com";
	public static String SNULIFE = "snulife";

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerLayoutToggle;
	private RelativeLayout mRootView;
	private View mMenuView;
	private View mMenuTestPage;
	private View mMenuExit;
	private WebView mSiteView;
	private ProgressBar mProgressBar;

	public static ArrayList<Activity> at = new ArrayList<Activity>();

	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		intent.getParcelableExtra("data");
		String url = null;
		try {
			JSONObject jsonObject = new JSONObject(intent.getStringExtra("com.parse.Data"));
			url = Html.fromHtml(jsonObject.getString("url")).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

		at.add(this);
		setContentView(R.layout.activity_main);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
		mDrawerLayoutToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.open_drawer, R.string.close_drawer) {

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}

		};
		mDrawerLayout.setDrawerListener(mDrawerLayoutToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mRootView = (RelativeLayout) findViewById(R.id.main_root);
		mMenuView = findViewById(R.id.main_menu);
		mMenuTestPage = mMenuView.findViewById(R.id.main_menu_test_page);
		mMenuTestPage.setOnClickListener(this);
		mMenuExit = mMenuView.findViewById(R.id.main_menu_exit);
		mMenuExit.setOnClickListener(this);

		int width = getResources().getDisplayMetrics().widthPixels / 3 * 2;
		DrawerLayout.LayoutParams params = (android.support.v4.widget.DrawerLayout.LayoutParams) mMenuView.getLayoutParams();
		params.width = width;
		mMenuView.setLayoutParams(params);

		mProgressBar = (ProgressBar) findViewById(R.id.main_progress_bar);
		mProgressBar.setVisibility(View.INVISIBLE);

		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.setCookie(DOMAIN, "app=1");

		mSiteView = new WebView(this);
		mSiteView.getSettings().setJavaScriptEnabled(true);
		mWebChromeClient = new myWebChromeClient();
		mSiteView.setWebChromeClient(mWebChromeClient);
		mSiteView.getSettings().setSaveFormData(true);
		mSiteView.getSettings().setAppCacheEnabled(true);
		mSiteView.getSettings().setDatabaseEnabled(true);
		mSiteView.getSettings().setDomStorageEnabled(true);
		mSiteView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
		mSiteView.getSettings().setSupportMultipleWindows(false);
		mSiteView.setVerticalScrollbarOverlay(true);
		mSiteView.setWebViewClient(new MyView());

		mRootView.addView(mSiteView, 0);
		
		if (url == null) {
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
			SNULifeApplication.is_push = pref.getBoolean("pushnotify", true);

			if (SNULifeApplication.is_push == true) {
				if (ParseInstallation.getCurrentInstallation().getObjectId() == null) {
					ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
						@Override
						public void done(com.parse.ParseException arg0) {
							objectId = ParseInstallation.getCurrentInstallation().getObjectId();
							mSiteView.loadUrl(DOMAIN + "/?m=1&obid=" + objectId);
						}
					});
				} else {
					objectId = ParseInstallation.getCurrentInstallation().getObjectId();
					mSiteView.loadUrl(DOMAIN + "/?m=1&obid=" + objectId);
				}
			} else {
				ParseInstallation.getCurrentInstallation().deleteInBackground();
				mSiteView.loadUrl(DOMAIN + "/?m=1");
			}
			CookieSyncManager.getInstance().startSync();
		} else {
			mSiteView.loadUrl(DOMAIN + url);
		}
	}

	class MyView extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (!url.contains(SNULIFE)) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(i);
				return true;
			} else {
				view.loadUrl(url);
				mProgressBar.setVisibility(View.VISIBLE);
				return true;
			}
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			view.setClickable(false);
			mProgressBar.setVisibility(View.VISIBLE);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			CookieSyncManager.getInstance().sync();
			if (SNULifeApplication.is_push == true) {
			}
			view.setClickable(true);
			mProgressBar.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			handler.proceed();
		}

	}

	class myWebChromeClient extends WebChromeClient {
		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
			mUploadMessage = uploadMsg;
			Intent i = new Intent(Intent.ACTION_GET_CONTENT);
			i.addCategory(Intent.CATEGORY_OPENABLE);
			i.setType("*/*");
			MainActivity.this.startActivityForResult(Intent.createChooser(i, "사진을 선택하세요"), FILECHOOSER_RESULTCODE);
		}

		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
			openFileChooser(uploadMsg, "", "");
		}

		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			openFileChooser(uploadMsg, "", "");
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			mProgressBar.setProgress(newProgress < 5 ? 5 : newProgress);
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
			return false;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerLayoutToggle.onConfigurationChanged(newConfig);
	}

	private void clearApplicationCache(java.io.File dir) {
		if (dir == null)
			dir = getCacheDir();
		else
			;
		if (dir == null)
			return;
		else
			;
		java.io.File[] children = dir.listFiles();
		try {
			for (int i = 0; i < children.length; i++)
				if (children[i].isDirectory())
					clearApplicationCache(children[i]);
				else
					children[i].delete();
		} catch (Exception e) {
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		clearApplicationCache(null);
		at.remove(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		CookieSyncManager.getInstance().stopSync();

		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		SNULifeApplication.is_push = pref.getBoolean("pushnotify", true);
	}

	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerLayoutToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerLayoutToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(mMenuView)) {
			mDrawerLayout.closeDrawer(mMenuView);
		} else if (mSiteView.canGoBack()) {
			mSiteView.goBack();
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		if (v == mMenuTestPage) {
			mSiteView.loadUrl(DOMAIN + "/testing");
			mDrawerLayout.closeDrawer(mMenuView);
		} else if (v == mMenuExit) {
			finish();
		}
	}
}