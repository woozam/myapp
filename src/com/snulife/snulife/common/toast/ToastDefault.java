package com.snulife.snulife.common.toast;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snulife.snulife.R;

public class ToastDefault extends Toast {

	private final int LAYOUT_ID = R.layout.toast_default;
	private final int IMAGE_ID = R.id.toast_default_image;
	private final int TITLE_ID = R.id.toast_default_title;
	private final int CONTENT_ID = R.id.toast_default_content;

	private Context mContext;

	private RelativeLayout mLayout;
	private ImageView mImage;
	private TextView mTitle;
	private TextView mContent;

	public ToastDefault(Context context) {
		super(context);
		mContext = context;
		initViews();
	}

	private void initViews() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		mLayout = (RelativeLayout) inflater.inflate(LAYOUT_ID, null);
		mImage = (ImageView) mLayout.findViewById(IMAGE_ID);
		mTitle = (TextView) mLayout.findViewById(TITLE_ID);
		mContent = (TextView) mLayout.findViewById(CONTENT_ID);
		setView(mLayout);
	}

	public void setTitle(CharSequence title) {
		mTitle.setText(title);
	}

	public void setContent(CharSequence content) {
		mContent.setText(content);
	}

	public void setImage(String imageName) {
		// mImage.setImageLazy(MediaSource.getUrl(imageName,
		// ClassType.THUMBNAIL), LoadType.WEB_DISK, null);
	}

	public void setImageBitmap(Bitmap bm) {
		mImage.setImageBitmap(bm);
	}

	public void setImageDrawable(Drawable drawable) {
		mImage.setImageDrawable(drawable);
	}

	public void setGravity(int gravity, int offset) {
		this.setGravity(gravity | Gravity.CENTER, 0, offset);
	}

	public void setIconVisibility(boolean visible) {
		mImage.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	public void setTitleVisibility(boolean visible) {
		mTitle.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
}
