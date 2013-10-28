package com.xxx.appstore.common.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.xxx.appstore.Constants;
import com.xxx.appstore.R;
import com.xxx.appstore.Session;
import com.xxx.appstore.common.AndroidHttpClient;
import com.xxx.appstore.common.HttpClientFactory;
import com.xxx.appstore.common.widget.LoadingDrawable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.concurrent.RejectedExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

public class ImageUtils {
	private static final int TYPE_IMAGE = 2;
	private static final int TYPE_NORAML = 1;
	private static final int TYPE_SCREENSHOT = 3;
	private static final int TYPE_TOP = 4;

	// 取消该ImageView正在进行的下载
	private static boolean cancelPotentialBitmapDownload(String url, ImageView paramImageView) {
		boolean bool = true;
		BitmapDownloaderTask localBitmapDownloaderTask = getBitmapDownloaderTask1(paramImageView);
		if (localBitmapDownloaderTask != null) {
			String str = localBitmapDownloaderTask.url;
			if ((str != null) && (str.equals(url)))
				bool = false;
			else
				localBitmapDownloaderTask.cancel(true);
		}
		return bool;
	}

	private static boolean cancelPotentialImageDownload(String paramString,
			ImageView paramImageView) {
		boolean bool = true;
		BitmapDownloaderTask localBitmapDownloaderTask = getBitmapDownloaderTask2(paramImageView);
		if (localBitmapDownloaderTask != null) {
			String str = localBitmapDownloaderTask.url;
			if ((str != null) && (str.equals(paramString)))
				bool = false;
			else
				localBitmapDownloaderTask.cancel(true);
		}
		return bool;
	}

	public static Bitmap createHomeUserIcon(Context context, Bitmap bitmap) {
		float f = context.getResources().getDisplayMetrics().density;
		int i = (int) (48F * f);
		int j = bitmap.getWidth();
		int k = bitmap.getHeight();
		float f3;
		float f4;
		float f5;
		Bitmap bitmap1;
		Canvas canvas;
		Matrix matrix;
		Paint paint;
		Bitmap bitmap2;
		int l;
		int i1;
		Bitmap bitmap3;
		Canvas canvas1;
		Bitmap bitmap4;
		if (j * i > i * k) {
			float f6 = (float) i / (float) k;
			float f7 = 0.5F * ((float) i - f6 * (float) j);
			f3 = f6;
			f5 = f7;
			f4 = 0.0F;
		} else {
			float f1 = (float) i / (float) j;
			float f2 = 0.5F * ((float) i - f1 * (float) k);
			f3 = f1;
			f4 = f2;
			f5 = 0.0F;
		}
		bitmap1 = Bitmap.createBitmap(i, i,
				android.graphics.Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap1);
		matrix = new Matrix();
		paint = new Paint(1);
		matrix.setScale(f3, f3);
		matrix.postTranslate((int) (f5 + 0.5F), (int) (f4 + 0.5F));
		canvas.drawBitmap(bitmap, matrix, paint);
		bitmap2 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.default_user_mask);
		paint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.DST_ATOP));
		canvas.drawBitmap(bitmap2, 0.0F, 0.0F, paint);
		l = (int) (60F * f);
		i1 = (int) (f * 6F);
		bitmap3 = Bitmap.createBitmap(l, l,
				android.graphics.Bitmap.Config.ARGB_8888);
		canvas1 = new Canvas(bitmap3);
		canvas1.drawBitmap(bitmap1, i1, i1, paint);
		bitmap4 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.home_gallery_user_mask);
		paint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.SRC_OVER));
		canvas1.drawBitmap(bitmap4, 0.0F, 0.0F, paint);
		canvas1.save();
		return bitmap3;
	}

	private static Bitmap createUserIcon(Context context, Bitmap bitmap) {
		int i = (int) (48F * context.getResources().getDisplayMetrics().density);
		int j = bitmap.getWidth();
		int k = bitmap.getHeight();
		float f2;
		float f3;
		float f4;
		Bitmap bitmap1;
		Canvas canvas;
		Matrix matrix;
		Paint paint;
		Bitmap bitmap2;
		Bitmap bitmap3;
		if (j * i > i * k) {
			float f5 = (float) i / (float) k;
			float f6 = 0.5F * ((float) i - f5 * (float) j);
			f2 = f5;
			f4 = f6;
			f3 = 0.0F;
		} else {
			float f = (float) i / (float) j;
			float f1 = 0.5F * ((float) i - f * (float) k);
			f2 = f;
			f3 = f1;
			f4 = 0.0F;
		}
		bitmap1 = Bitmap.createBitmap(i, i,
				android.graphics.Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap1);
		matrix = new Matrix();
		paint = new Paint(1);
		matrix.setScale(f2, f2);
		matrix.postTranslate((int) (f4 + 0.5F), (int) (f3 + 0.5F));
		canvas.drawBitmap(bitmap, matrix, paint);
		bitmap2 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.default_user_mask);
		paint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.DST_ATOP));
		canvas.drawBitmap(bitmap2, 0.0F, 0.0F, paint);
		bitmap3 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.default_user_mask2);
		paint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.SRC_OVER));
		canvas.drawBitmap(bitmap3, 0.0F, 0.0F, paint);
		canvas.save();
		return bitmap1;
	}

	public static void download(Context context, String url,
			ImageView imageview) {
		download(context, url, imageview, R.drawable.loading_icon, false);
	}

	// 读取应用icon
	public static void download(Context context, String pkgName,
			ImageView imageview, int iconID) {
		Bitmap bitmap = CacheManager.getInstance().getDrawableFromCache(
				context, pkgName);
		if (bitmap != null) {
			imageview.setImageBitmap(bitmap);
			return;
		}

		Drawable drawable1 = context.getResources().getDrawable(
				iconID);
		Drawable drawable2 = imageview.getDrawable();
		if (drawable2 != null)
			drawable2.setCallback(null);
		imageview.setImageDrawable(drawable1);
		if (Session.get(context).isStopDownloadImage()) {
			imageview.setVisibility(View.GONE);
		} else {
			imageview.setVisibility(View.VISIBLE);
			if (cancelPotentialBitmapDownload(pkgName, imageview)) {
				DrawableDownloaderTask drawableDownloaderTask = new DrawableDownloaderTask(
						imageview);
				imageview.setImageDrawable(new DownloadedDrawable3(
						drawable1, drawableDownloaderTask));
				try {
					Object[] arrayOfObject = new Object[2];
					arrayOfObject[0] = context;
					arrayOfObject[1] = pkgName;
					drawableDownloaderTask.execute(arrayOfObject);
				} catch (RejectedExecutionException localRejectedExecutionException) {
					Utils.E("RejectedExecutionException when download image",
							localRejectedExecutionException);
				}
			}
		}
	}

	public static void download(Context context, String url,
			ImageView paramImageView, int drawableID, boolean paramBoolean) {
		CacheManager localCacheManager = CacheManager.getInstance();
		Bitmap localBitmap;
//		if (paramImageView.getId() == R.id.iv_user_icon) {
//			localBitmap = localCacheManager.getDrawableFromCache(paramContext,
//					paramString + String.valueOf(R.id.iv_user_icon));
//		} else 
		if (paramImageView.getId() == R.id.home_gallery_user) {
			localBitmap = localCacheManager.getDrawableFromCache(context,
					url + String.valueOf(R.id.home_gallery_user));
		} else{
			localBitmap = localCacheManager.getDrawableFromCache(context,
					url);
		}
		
		if (localBitmap != null){	// 缓存不为空
			paramImageView.setImageBitmap(localBitmap);
			return;
		}	
		Drawable localDrawable1 = context.getResources().getDrawable(drawableID);
		Drawable localDrawable2 = paramImageView.getDrawable();
		if (localDrawable2 != null)
			localDrawable2.setCallback(null);
		paramImageView.setImageDrawable(localDrawable1);	// 设置图片加载时显示的图片
		if (Session.get(context).isStopDownloadImage()) {
			if (paramBoolean) {
				paramImageView.setVisibility(View.VISIBLE);
				paramImageView.setImageResource(drawableID);
			} else {
				paramImageView.setVisibility(View.GONE);
			}
		} else {	// 下载图片
			paramImageView.setVisibility(View.VISIBLE);
			if (cancelPotentialBitmapDownload(url, paramImageView)) {
				BitmapDownloaderTask localBitmapDownloaderTask = new BitmapDownloaderTask(
						paramImageView);
				paramImageView.setImageDrawable(new DownloadedDrawable1(
						localDrawable1, localBitmapDownloaderTask));
				try {
					Object[] arrayOfObject = new Object[3];
					arrayOfObject[0] = context;
					arrayOfObject[1] = url;
					arrayOfObject[2] = Integer.valueOf(/*1*/TYPE_NORAML);
					// 执行下载
					localBitmapDownloaderTask.execute(arrayOfObject);
				} catch (RejectedExecutionException localRejectedExecutionException) {
					Utils.E("RejectedExecutionException when download image",
							localRejectedExecutionException);
				}
			}
		}
	}

	public static void downloadDeatilScreenshot(Context paramContext,
			String paramString, ImageView paramImageView) {
		if (Session.get(paramContext).isStopDownloadImage())
			return;
		BitmapDrawable localBitmapDrawable = new BitmapDrawable();
		if (cancelPotentialBitmapDownload(paramString, paramImageView)) {
			BitmapDownloaderTask localBitmapDownloaderTask = new BitmapDownloaderTask(
					paramImageView);
			paramImageView.setImageDrawable(new DownloadedDrawable1(
					localBitmapDrawable, localBitmapDownloaderTask));
			Object[] arrayOfObject = new Object[3];
			arrayOfObject[0] = paramContext;
			arrayOfObject[1] = paramString;
			arrayOfObject[2] = Integer.valueOf(/*3*/TYPE_SCREENSHOT);
			localBitmapDownloaderTask.execute(arrayOfObject);
		}
	}

	public static void downloadHomeTopDrawable(Context paramContext,
			String paramString, ImageView paramImageView) {
		if (Session.get(paramContext).isStopDownloadImage())
			return;
		Bitmap localBitmap = CacheManager.getInstance().getDrawableFromCache(
				paramContext, paramString);
		if (localBitmap != null) {
			BitmapDrawable localBitmapDrawable1 = new BitmapDrawable(
					localBitmap);
			paramImageView.setImageDrawable(getMaskDrawable(paramContext));
			paramImageView.setBackgroundDrawable(localBitmapDrawable1);
		} else {
			BitmapDrawable localBitmapDrawable2 = new BitmapDrawable(
					BitmapFactory.decodeResource(paramContext.getResources(),
							R.drawable.banner_loading));
			if (cancelPotentialBitmapDownload(paramString, paramImageView)) {
				BitmapDownloaderTask localBitmapDownloaderTask = new BitmapDownloaderTask(
						paramImageView);
				paramImageView.setImageDrawable(new DownloadedDrawable1(
						localBitmapDrawable2, localBitmapDownloaderTask));
				Object[] arrayOfObject = new Object[3];
				arrayOfObject[0] = paramContext;
				arrayOfObject[1] = paramString;
				arrayOfObject[2] = Integer.valueOf(/*4*/TYPE_TOP);
				localBitmapDownloaderTask.execute(arrayOfObject);
			}
		}
	}

	public static void downloadScreenShot(Context paramContext,
			String paramString, ImageView paramImageView) {
		if (Session.get(paramContext).isStopDownloadImage())
			return;
		LoadingDrawable localLoadingDrawable = new LoadingDrawable(paramContext);
		if (cancelPotentialImageDownload(paramString, paramImageView)) {
			BitmapDownloaderTask localBitmapDownloaderTask = new BitmapDownloaderTask(
					paramImageView);
			DownloadedDrawable2 localDownloadedDrawable2 = new DownloadedDrawable2(
					localLoadingDrawable, localBitmapDownloaderTask);
			paramImageView.setImageDrawable(null);
			paramImageView.setBackgroundDrawable(localDownloadedDrawable2);
			localDownloadedDrawable2.start();
			Object[] arrayOfObject = new Object[3];
			arrayOfObject[0] = paramContext;
			arrayOfObject[1] = paramString;
			arrayOfObject[2] = Integer.valueOf(/*2*/TYPE_IMAGE);
			localBitmapDownloaderTask.execute(arrayOfObject);
		}
	}

	private static BitmapDownloaderTask getBitmapDownloaderTask1(ImageView imageview) {
		BitmapDownloaderTask bitmapdownloadertask;
		if (imageview != null) {
			Drawable drawable = imageview.getDrawable();
			if (drawable == null || !(drawable instanceof DownloadedDrawable1))
				bitmapdownloadertask = null;
			else
				bitmapdownloadertask = ((DownloadedDrawable1) drawable)
						.getBitmapDownloaderTask();
		} else {
			bitmapdownloadertask = null;
		}
		return bitmapdownloadertask;
	}

	private static BitmapDownloaderTask getBitmapDownloaderTask2(
			ImageView imageview) {
		BitmapDownloaderTask bitmapdownloadertask;
		if (imageview != null) {
			Drawable drawable = imageview.getBackground();
			if (drawable == null || !(drawable instanceof DownloadedDrawable2))
				bitmapdownloadertask = null;
			else
				bitmapdownloadertask = ((DownloadedDrawable2) drawable)
						.getBitmapDownloaderTask();
		} else {
			bitmapdownloadertask = null;
		}
		return bitmapdownloadertask;
	}

	private static DrawableDownloaderTask getBitmapDownloaderTask3(
			ImageView imageview) {
		DrawableDownloaderTask drawabledownloadertask;
		if (imageview != null) {
			Drawable drawable = imageview.getDrawable();
			if (drawable == null || !(drawable instanceof DownloadedDrawable3))
				drawabledownloadertask = null;
			else
				drawabledownloadertask = ((DownloadedDrawable3) drawable)
						.getBitmapDownloaderTask();
		} else {
			drawabledownloadertask = null;
		}
		return drawabledownloadertask;
	}

	public static Bitmap getBitmapFromSdcard(Context paramContext,
			String paramString) {
		int i = paramString.hashCode();
		File localFile = new File(paramContext.getCacheDir(), String.valueOf(i));
		if (!localFile.exists())
			return null;

		Bitmap localBitmap = null;
		try {
			localBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

		} catch (OutOfMemoryError localOutOfMemoryError) {
			Utils.E("decode bitmap from sdcard erroe", localOutOfMemoryError);
		}
		return localBitmap;
	}

	public static Bitmap getImageFromUrl(Context context,String url, boolean bSample) {
		HttpGet httpget;
		AndroidHttpClient androidhttpclient;
		httpget = new HttpGet(url);
		androidhttpclient = HttpClientFactory.get().getHttpClient();
		HttpResponse httpresponse = null;
		int statusCode = 0;
		try {
			httpresponse = androidhttpclient.execute(httpget);
			statusCode = httpresponse.getStatusLine().getStatusCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Utils.W((new StringBuilder()).append("IOException ")
					.append(url).toString(), e);
		}

		if ((statusCode != 200) || (null == httpresponse)) {
			Utils.W((new StringBuilder()).append("Error ").append(statusCode)
					.append(" while retrieving bitmap from ")
					.append(url).toString());
			httpget.abort();
			return null;
		}

		HttpEntity httpentity = httpresponse.getEntity();
		if (httpentity == null) {
			httpget.abort();
			return null;
		}

		InputStream inputstream1 = null;
		try {
			inputstream1 = httpentity.getContent();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (inputstream1 == null) {
			httpget.abort();
			return null;
		}

		Bitmap bitmap;
		android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
		options.inPurgeable = true;
		if (bSample){
			options.inSampleSize = 2;
		}
		bitmap = BitmapFactory.decodeStream(
				new FlushedInputStream(inputstream1), null, options);
		try {
			inputstream1.close();
			httpentity.consumeContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		httpget.abort();
		return bitmap;
	}

	public static StateListDrawable getMaskDrawable(Context paramContext) {
		StateListDrawable localStateListDrawable = new StateListDrawable();
		Resources localResources = paramContext.getResources();
		int[] arrayOfInt = new int[1];
		arrayOfInt[0] =  android.R.attr.state_pressed;
		localStateListDrawable.addState(
				arrayOfInt,
				new BitmapDrawable(localResources, BitmapFactory
						.decodeResource(localResources, R.drawable.banner_pressed)));
		return localStateListDrawable;
	}

	public static Bitmap rotateImage(int i, int j, Bitmap bitmap) {
		if (bitmap == null)
			return null;

		Bitmap bitmap2;
		int k = bitmap.getWidth();
		int l = bitmap.getHeight();
		Bitmap bitmap1;
		int i1;
		if ((float) l / (float) k < 1.0F) {
			Matrix matrix = new Matrix();
			matrix.postRotate(90F);
			try {
				bitmap1 = Bitmap
						.createBitmap(bitmap, 0, 0, l, k, matrix, false);
			} catch (OutOfMemoryError outofmemoryerror) {
				bitmap1 = null;
			}
		} else {
			bitmap1 = bitmap;
		}
		i1 = (int) (((float) j / (float) l) * (float) k);
		if (i1 > i)
			i1 = i;
		bitmap2 = Bitmap.createScaledBitmap(bitmap1, i1, j, false);
		return bitmap2;
	}

	public static Bitmap rotateImage(Bitmap paramBitmap) {

		if (paramBitmap == null)
			return null;
		Bitmap bmp;

		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		if (j / i > 1.0F) {
			bmp = paramBitmap;
		} else {
			Matrix localMatrix = new Matrix();
			localMatrix.postRotate(90.0F);
			try {
				Bitmap localBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, i,
						j, localMatrix, false);
				bmp = localBitmap;
			} catch (OutOfMemoryError localOutOfMemoryError) {
				bmp = null;
			}
		}

		return bmp;
	}

	// 缓存图片到SD卡
	public static void saveBitmapToSdcard(Context context, int hCode, Bitmap bitmap) {
		File file;
		FileOutputStream fileoutputstream = null;
		file = context.getCacheDir();

		try {
			fileoutputstream = new FileOutputStream(new File(file,
					String.valueOf(hCode)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Utils.E("Error when save bitmap to sdcard", e);
			return;
		}
		bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100,fileoutputstream);
		try {
			fileoutputstream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Bitmap scaleBitmap(Context paramContext, Bitmap paramBitmap) {
		if (paramBitmap == null)
			return null;

		Bitmap localObject = null;

		int i = paramBitmap.getWidth();
		int j = paramBitmap.getHeight();
		int k = paramContext.getResources().getDisplayMetrics().widthPixels;
		int m = (int) (k / i * j);
		try {
			Bitmap localBitmap = Bitmap.createScaledBitmap(paramBitmap, k, m,
					false);
			localObject = localBitmap;
		} catch (OutOfMemoryError localOutOfMemoryError) {
			localObject = null;
		}

		return localObject;
	}

	static class BitmapDownloaderTask extends AsyncTaskEx<Object, Void, Bitmap> {
		CacheManager cache = CacheManager.getInstance();
		private Context context;
		// 当前在执行下载的ImageView
		private final WeakReference<ImageView> imageViewReference;
		private int type;
		private String url;

		public BitmapDownloaderTask(ImageView paramImageView) {
			this.imageViewReference = new WeakReference(paramImageView);
		}

		protected Bitmap doInBackground(Object[] paramArrayOfObject) {
			Bitmap localBitmap = null;
			this.context = ((Context) paramArrayOfObject[0]);
			this.url = ((String) paramArrayOfObject[1]);
			this.type = ((Integer) paramArrayOfObject[2]).intValue();
			String strUrl = this.url;
			if (this.imageViewReference != null) {
				ImageView localImageView = (ImageView) this.imageViewReference.get();
				if (localImageView != null) {
//					if (localImageView.getId() == R.id.iv_user_icon)
//						str = this.url + String.valueOf(R.id.iv_user_icon);
//					else 
						if (localImageView.getId() == R.id.home_gallery_user)
							strUrl = this.url + String.valueOf(R.id.home_gallery_user);
				}

				if (this.cache.existsDrawable(strUrl)) {	// 先判断缓存有没有
					localBitmap = ImageUtils.getBitmapFromSdcard(this.context,strUrl);
				} else {
					if (this.type == TYPE_SCREENSHOT){	// 如果是截图，需要Sample Size
						localBitmap = ImageUtils.getImageFromUrl(this.context,this.url, true);
					}else{
						localBitmap = ImageUtils.getImageFromUrl(this.context,this.url, false);
					}
				}

				if ((localImageView != null) && (localBitmap != null)) {
//					if (localImageView.getId() == R.id.iv_user_icon)
//						localBitmap = ImageUtils.createUserIcon(this.context,
//								localBitmap);
//					else 
						if (localImageView.getId() == R.id.home_gallery_user){
							localBitmap = ImageUtils.createHomeUserIcon(this.context, localBitmap);
						}
				}

				if ((localImageView != null) && (localBitmap != null)) {
					int viewId = localImageView.getId();
					if (/*(i == R.id.iv_user_icon)
							|| */(viewId == R.id.home_gallery_user)) {
						this.cache.cacheDrawable(this.context, strUrl, localBitmap);
					} else {
						this.cache.cacheDrawableToL2(this.context, strUrl, localBitmap);
					}
				}
			} else {
				this.cache.cacheDrawable(this.context, this.url, localBitmap);
			}

			return localBitmap;
		}

		protected void onPostExecute(Bitmap paramBitmap) {
			if (isCancelled())
				return;
			ImageView localImageView;
			BitmapDownloaderTask localBitmapDownloaderTask;
			if (this.imageViewReference != null) {
				localImageView = (ImageView) this.imageViewReference.get();

				if (this.type == TYPE_IMAGE) {
					localBitmapDownloaderTask = ImageUtils.getBitmapDownloaderTask2(localImageView);
				} else {
					localBitmapDownloaderTask = ImageUtils.getBitmapDownloaderTask1(localImageView);
				}

				if ((this == localBitmapDownloaderTask)
						&& (null != paramBitmap)) {
					if (this.type == TYPE_IMAGE) {
						localImageView.setBackgroundDrawable(null);
						localImageView.setImageBitmap(ImageUtils.scaleBitmap(this.context,ImageUtils.rotateImage(paramBitmap)));
					} else if (this.type == TYPE_SCREENSHOT) {
						localImageView.setImageBitmap(ImageUtils
								.rotateImage(paramBitmap));
					} else if (this.type == TYPE_TOP) {
						BitmapDrawable localBitmapDrawable = new BitmapDrawable(
								paramBitmap);
						localImageView.setImageDrawable(ImageUtils
								.getMaskDrawable(this.context));
						localImageView
								.setBackgroundDrawable(localBitmapDrawable);
					} else {
						localImageView.setImageBitmap(paramBitmap);
					}

					localImageView.startAnimation(AnimationUtils.loadAnimation(this.context, R.anim.show_in));
				}
			}
		}
	}

	static class DownloadedDrawable1 extends BitmapDrawable {
		private final WeakReference<ImageUtils.BitmapDownloaderTask> bitmapDownloaderTaskReference1;

		public DownloadedDrawable1(Drawable paramDrawable,ImageUtils.BitmapDownloaderTask paramBitmapDownloaderTask) {
			super();
			this.bitmapDownloaderTaskReference1 = new WeakReference(paramBitmapDownloaderTask);
		}

		public ImageUtils.BitmapDownloaderTask getBitmapDownloaderTask() {
			return (ImageUtils.BitmapDownloaderTask) this.bitmapDownloaderTaskReference1.get();
		}
	}

	static class DownloadedDrawable2 extends AnimationDrawable {
		private final WeakReference<ImageUtils.BitmapDownloaderTask> bitmapDownloaderTaskReference2;

		public DownloadedDrawable2(Drawable paramDrawable,
				ImageUtils.BitmapDownloaderTask paramBitmapDownloaderTask) {
			AnimationDrawable localAnimationDrawable = (AnimationDrawable) paramDrawable;
			int i = localAnimationDrawable.getNumberOfFrames();
			for (int j = 0; j < i; j++)
				super.addFrame(localAnimationDrawable.getFrame(j),
						localAnimationDrawable.getDuration(j));
			super.setOneShot(false);
			this.bitmapDownloaderTaskReference2 = new WeakReference(
					paramBitmapDownloaderTask);
		}

		public ImageUtils.BitmapDownloaderTask getBitmapDownloaderTask() {
			return (ImageUtils.BitmapDownloaderTask) this.bitmapDownloaderTaskReference2
					.get();
		}
	}

	static class DownloadedDrawable3 extends BitmapDrawable {
		private final WeakReference<ImageUtils.DrawableDownloaderTask> bitmapDownloaderTaskReference3;

		public DownloadedDrawable3(Drawable paramDrawable,
				ImageUtils.DrawableDownloaderTask paramDrawableDownloaderTask) {
			super();
			this.bitmapDownloaderTaskReference3 = new WeakReference(
					paramDrawableDownloaderTask);
		}

		public ImageUtils.DrawableDownloaderTask getBitmapDownloaderTask() {
			return (ImageUtils.DrawableDownloaderTask) this.bitmapDownloaderTaskReference3
					.get();
		}
	}

	static class DrawableDownloaderTask extends
			AsyncTaskEx<Object, Void, Drawable> {
		CacheManager cache = CacheManager.getInstance();
		private Context context;
		private final WeakReference<ImageView> imageViewReference;

		public DrawableDownloaderTask(ImageView paramImageView) {
			this.imageViewReference = new WeakReference(paramImageView);
		}

		// 读取应用icon
		protected Drawable doInBackground(Object[] paramArrayOfObject) {
			this.context = ((Context) paramArrayOfObject[0]);
			String str = (String) paramArrayOfObject[1];
			PackageManager localPackageManager = this.context
					.getPackageManager();
			Drawable localDrawable = null;
			try {
				PackageInfo localPackageInfo2 = localPackageManager
						.getPackageInfo(str, 0);
				if (localPackageInfo2 == null) {
					return null;
				}

				localDrawable = localPackageInfo2.applicationInfo
						.loadIcon(localPackageManager);
				if ((localDrawable instanceof BitmapDrawable)) {
					Bitmap localBitmap = ((BitmapDrawable) localDrawable)
							.getBitmap();
					this.cache.cacheDrawable(this.context, str, localBitmap);
				}
			} catch (PackageManager.NameNotFoundException localNameNotFoundException) {

				localNameNotFoundException.printStackTrace();

			}
			return localDrawable;
		}

		protected void onPostExecute(Drawable paramDrawable) {
			if (isCancelled())
				return;

			if (this.imageViewReference != null) {
				ImageView localImageView = (ImageView) this.imageViewReference
						.get();
				if ((this == ImageUtils
						.getBitmapDownloaderTask3(localImageView))
						&& (paramDrawable != null)) {
					localImageView.setImageDrawable(paramDrawable);
					localImageView.startAnimation(AnimationUtils.loadAnimation(
							this.context, 2130968577));
				}
			}
		}
	}
}