package com.zhao.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;

public class LoadingDrawable extends AnimationDrawable {

	public LoadingDrawable(Activity act) {
		super();
		DisplayMetrics dm = Utils.getDevInfo(act);
		this.setOneShot(false);
		Paint whitepaint = new Paint();
		whitepaint.setColor(Color.parseColor("#80ff9600"));
		whitepaint.setStyle(Paint.Style.FILL);
		whitepaint.setAntiAlias(true);
		
		Paint redPaint = new Paint();
//        color1 = Color.parseColor("#80ff9600");
//        color2 = Color.parseColor("#30000000");
		redPaint.setColor(Color.parseColor("#30000000"));
		redPaint.setStyle(Paint.Style.FILL);
		redPaint.setAntiAlias(true);
		
		int width = 54;
		int height = 12;
		Canvas canvas = new Canvas();
		Bitmap bitmap;
		for (int i = 0; i < 4; i++) {
			bitmap = Bitmap
					.createBitmap(width, height, Bitmap.Config.ARGB_4444);
			bitmap.setDensity(dm.densityDpi);
			canvas.setBitmap(bitmap);
			canvas.drawColor(0);
			Paint p;
			for (int j = 0; j < 4; j++) {
				if (j == i) {
					p = redPaint;
				} else {
					p = whitepaint;
				}
				canvas.drawRect(new RectF(((j + 1) * 3.6f) + j * 8, 3.0f,
						((j + 1) * 3.6f) + (j + 1) * 8, 11.0f), p);
			}
			this.addFrame(new BitmapDrawable(null, bitmap), 400);
		}
	}
}
