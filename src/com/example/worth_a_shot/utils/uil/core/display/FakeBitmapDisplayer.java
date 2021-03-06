package com.example.worth_a_shot.utils.uil.core.display;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.worth_a_shot.utils.uil.core.DisplayImageOptions;
import com.example.worth_a_shot.utils.uil.core.ImageLoader;

/**
 * Fake displayer which doesn't display Bitmap in ImageView. Should be used for in {@linkplain DisplayImageOptions
 * display options} for
 * {@link ImageLoader#loadImage(String, int, int, com.nostra13.universalimageloader.core.DisplayImageOptions, com.nostra13.universalimageloader.core.assist.ImageLoadingListener)
 * ImageLoader.loadImage()}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class FakeBitmapDisplayer implements BitmapDisplayer {
	@Override
	public Bitmap display(Bitmap bitmap, ImageView imageView) {
		// Do nothing
		return bitmap;
	}
}
