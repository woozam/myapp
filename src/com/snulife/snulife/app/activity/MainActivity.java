package com.snulife.snulife.app.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;
import com.snulife.snulife.R;
import com.snulife.snulife.app.SNULifeApplication;

public class MainActivity extends Activity {
    private myWebChromeClient mWebChromeClient;

	Menu mMenu;
	public static String objectId;
	public static String domain = "http://snulife.com";


	static WebView SiteView;
	public static ArrayList<Activity> at = new ArrayList<Activity>();

    private ValueCallback<Uri> mUploadMessage;
    private final static int FILECHOOSER_RESULTCODE = 1;
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
    		Intent intent) {
    	if (requestCode == FILECHOOSER_RESULTCODE) {
    		if (null == mUploadMessage)
    			return;
    		Uri result = intent == null || resultCode != RESULT_OK ? null
    				: intent.getData();
    		mUploadMessage.onReceiveValue(result);
    		mUploadMessage = null;
    	}
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        at.add(this);
        setContentView(R.layout.activity_main);
        
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(domain, "app=1");
        
        SiteView = new WebView(getApplicationContext());
        SiteView.getSettings().setJavaScriptEnabled(true);
        //SiteView.getSettings().setPluginState(PluginState. ON);
        mWebChromeClient = new myWebChromeClient();
        SiteView.setWebChromeClient(mWebChromeClient);
        SiteView.getSettings().setSaveFormData(true);
        SiteView.getSettings().setSavePassword(true);
        SiteView.getSettings().setSupportMultipleWindows(true);
        SiteView.getSettings().setAppCacheEnabled(true);
        SiteView.getSettings().setDatabaseEnabled(true);
        SiteView.getSettings().setDomStorageEnabled(true);
        SiteView.getSettings().setLightTouchEnabled(true);
        SiteView.setVerticalScrollbarOverlay(true);
        SiteView.setWebViewClient(new MyView());
        
        setContentView(SiteView);
        
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    	SNULifeApplication.is_push =  pref.getBoolean("pushnotify", true);

		if(SNULifeApplication.is_push == true) {
			if (ParseInstallation.getCurrentInstallation().getObjectId() == null) {
	    		ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
	    			@Override
	    			public void done(com.parse.ParseException arg0) {
	    				objectId = ParseInstallation.getCurrentInstallation().getObjectId();
	    				SiteView.loadUrl(domain + "/?m=1&obid=" + objectId);
	    			}
	    		});
	    	}
	    	else {
	    		objectId = ParseInstallation.getCurrentInstallation().getObjectId();
	    		SiteView.loadUrl(domain + "/?m=1&obid=" + objectId);
	    	}
	    }
	    else {
	    	ParseInstallation.getCurrentInstallation().deleteInBackground();
	    	SiteView.loadUrl(domain + "/?m=1");
	    }
    	CookieSyncManager.getInstance().startSync();
       
	}
    
    class MyView extends WebViewClient 
    {
    	@Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
        	if (!url.startsWith(domain) && !url.startsWith(domain)) 
            {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
                return true;
            }
            else {
            	view.loadUrl(url);
            	return true;
            }
        }
        
        @Override
        public void onPageStarted(WebView view , String url, Bitmap favicon) {
        	super.onPageStarted(view , url, favicon);
        	view.setClickable(false);
        	
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            CookieSyncManager.getInstance().sync();
            if(SNULifeApplication.is_push == true ) {
            }
            view.setClickable(true);

        }
        
    	@Override  
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {  
            handler.proceed();
        }   
    }
    
    class myWebChromeClient extends WebChromeClient
    {
    	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
    		mUploadMessage = uploadMsg;
    		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
    		i.addCategory(Intent.CATEGORY_OPENABLE);
    		i.setType("*/*");
    		MainActivity.this.startActivityForResult(
    				Intent.createChooser(i, "사진을 선택하세요"),
    				FILECHOOSER_RESULTCODE);
    	}

    	public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
			openFileChooser(uploadMsg,"","");
		}

		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
			openFileChooser(uploadMsg,"", "");
		}
		
    	@Override
    	public void onProgressChanged(WebView view, int newProgress) {
    	}
     }     
    
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	if(keyCode==KeyEvent.KEYCODE_BACK)
    	{
    		if(SiteView.canGoBack())
    		{
    			SiteView.goBack();
    			return true;
    		}
    	}
    	return super.onKeyDown(keyCode, event);
    }

    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
       super.onConfigurationChanged(newConfig);
    }
        
    private void clearApplicationCache(java.io.File dir){
        if(dir==null)
            dir = getCacheDir();
        else;
        if(dir==null)
            return;
        else;
        java.io.File[] children = dir.listFiles();
        try{
            for(int i=0;i<children.length;i++)
                if(children[i].isDirectory())
                    clearApplicationCache(children[i]);
                else children[i].delete();
        }
        catch(Exception e){}
    }

	@Override
    public void onDestroy() {
    	super.onDestroy();
    	clearApplicationCache(null);
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
    	SNULifeApplication.is_push =  pref.getBoolean("pushnotify", true);
    }
    
    @Override
    public void onStop() {
      super.onStop();
    }
}