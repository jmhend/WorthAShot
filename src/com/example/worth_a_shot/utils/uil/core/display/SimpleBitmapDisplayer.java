package com.example.worth_a_shot.utils.uil.core.display;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Just displays {@link Bitmap} in {@link ImageView}
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class SimpleBitmapDisplayer implements BitmapDisplayer {
	@Override
	public Bitmap display(Bitmap bitmap, ImageView imageView) {
		try {
			imageView.setImageBitmap(bitmap);
			return bitmap;
		} catch (Throwable t) {
			t.printStackTrace();
			imageView.setImageBitmap(null);
			return bitmap;
		}		
	}
}