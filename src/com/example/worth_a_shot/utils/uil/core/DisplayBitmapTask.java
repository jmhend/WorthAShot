package com.example.worth_a_shot.utils.uil.core;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.example.worth_a_shot.utils.uil.core.assist.ImageLoadingListener;
import com.example.worth_a_shot.utils.uil.core.display.BitmapDisplayer;

/**
 * Displays bitmap in {@link ImageView}. Must be called on UI thread.
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @see ImageLoadingListener
 * @see BitmapDisplayer
 */
final class DisplayBitmapTask implements Runnable {

	private final Bitmap bitmap;
	private final ImageView imageView;
	private final String memoryCacheKey;
	private final BitmapDisplayer bitmapDisplayer;
	private final ImageLoadingListener listener;

	public DisplayBitmapTask(Bitmap bitmap, ImageLoadingInfo imageLoadingInfo) {
		this.bitmap = bitmap;
		imageView = imageLoadingInfo.imageView;
		memoryCacheKey = imageLoadingInfo.memoryCacheKey;
		bitmapDisplayer = imageLoadingInfo.options.getDisplayer();
		listener = imageLoadingInfo.listener;
	}

	public void run() {
		if (isViewWasReused()) {
			listener.onLoadingCancelled();
		} else {
			Bitmap displayedBitmap = bitmapDisplayer.display(bitmap, imageView);
			listener.onLoadingComplete(displayedBitmap);
		}
	}

	/** Checks whether memory cache key (image URI) for current ImageView is actual */
	private boolean isViewWasReused() {
		String currentCacheKey = ImageLoader.getInstance().getLoadingUriForView(imageView);
		return !memoryCacheKey.equals(currentCacheKey);
	}
}
