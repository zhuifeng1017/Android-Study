package com.mappn.gfan.common.util;

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
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.mappn.gfan.Session;
import com.mappn.gfan.common.AndroidHttpClient;
import com.mappn.gfan.common.HttpClientFactory;
import com.mappn.gfan.common.widget.LoadingDrawable;
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

public class ImageUtils
{
  private static final int TYPE_IMAGE = 2;
  private static final int TYPE_NORAML = 1;
  private static final int TYPE_SCREENSHOT = 3;
  private static final int TYPE_TOP = 4;

	private static boolean cancelPotentialBitmapDownload(String paramString,
			ImageView paramImageView) {
		boolean bool = true;
		BitmapDownloaderTask localBitmapDownloaderTask = getBitmapDownloaderTask1(paramImageView);
		if (localBitmapDownloaderTask != null) {
			String str = localBitmapDownloaderTask.url;
			if ((str != null) && (str.equals(paramString)))
				bool = false;
			else
				localBitmapDownloaderTask.cancel(true);
		}
		return bool;
	}

  private static boolean cancelPotentialImageDownload(String paramString, ImageView paramImageView)
  {
	  boolean bool = true;
    BitmapDownloaderTask localBitmapDownloaderTask = getBitmapDownloaderTask2(paramImageView);
    if (localBitmapDownloaderTask != null)
    {
      String str = localBitmapDownloaderTask.url;
      if ((str != null) && (str.equals(paramString)))
    	  bool = false;
      else
    	  localBitmapDownloaderTask.cancel(true);
    }
      return bool;
  }

  public static Bitmap createHomeUserIcon(Context context, Bitmap bitmap)
  {
      float f = context.getResources().getDisplayMetrics().density;
      int i = (int)(48F * f);
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
      if(j * i > i * k)
      {
          float f6 = (float)i / (float)k;
          float f7 = 0.5F * ((float)i - f6 * (float)j);
          f3 = f6;
          f5 = f7;
          f4 = 0.0F;
      } else
      {
          float f1 = (float)i / (float)j;
          float f2 = 0.5F * ((float)i - f1 * (float)k);
          f3 = f1;
          f4 = f2;
          f5 = 0.0F;
      }
      bitmap1 = Bitmap.createBitmap(i, i, android.graphics.Bitmap.Config.ARGB_8888);
      canvas = new Canvas(bitmap1);
      matrix = new Matrix();
      paint = new Paint(1);
      matrix.setScale(f3, f3);
      matrix.postTranslate((int)(f5 + 0.5F), (int)(f4 + 0.5F));
      canvas.drawBitmap(bitmap, matrix, paint);
      bitmap2 = BitmapFactory.decodeResource(context.getResources(), 0x7f020043);
      paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_ATOP));
      canvas.drawBitmap(bitmap2, 0.0F, 0.0F, paint);
      l = (int)(60F * f);
      i1 = (int)(f * 6F);
      bitmap3 = Bitmap.createBitmap(l, l, android.graphics.Bitmap.Config.ARGB_8888);
      canvas1 = new Canvas(bitmap3);
      canvas1.drawBitmap(bitmap1, i1, i1, paint);
      bitmap4 = BitmapFactory.decodeResource(context.getResources(), 0x7f020065);
      paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_OVER));
      canvas1.drawBitmap(bitmap4, 0.0F, 0.0F, paint);
      canvas1.save();
      return bitmap3;
  }

  private static Bitmap createUserIcon(Context context, Bitmap bitmap)
  {
      int i = (int)(48F * context.getResources().getDisplayMetrics().density);
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
      if(j * i > i * k)
      {
          float f5 = (float)i / (float)k;
          float f6 = 0.5F * ((float)i - f5 * (float)j);
          f2 = f5;
          f4 = f6;
          f3 = 0.0F;
      } else
      {
          float f = (float)i / (float)j;
          float f1 = 0.5F * ((float)i - f * (float)k);
          f2 = f;
          f3 = f1;
          f4 = 0.0F;
      }
      bitmap1 = Bitmap.createBitmap(i, i, android.graphics.Bitmap.Config.ARGB_8888);
      canvas = new Canvas(bitmap1);
      matrix = new Matrix();
      paint = new Paint(1);
      matrix.setScale(f2, f2);
      matrix.postTranslate((int)(f4 + 0.5F), (int)(f3 + 0.5F));
      canvas.drawBitmap(bitmap, matrix, paint);
      bitmap2 = BitmapFactory.decodeResource(context.getResources(), 0x7f020043);
      paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_ATOP));
      canvas.drawBitmap(bitmap2, 0.0F, 0.0F, paint);
      bitmap3 = BitmapFactory.decodeResource(context.getResources(), 0x7f020044);
      paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_OVER));
      canvas.drawBitmap(bitmap3, 0.0F, 0.0F, paint);
      canvas.save();
      return bitmap1;
  }

  public static void download(Context paramContext, String paramString, ImageView paramImageView)
  {
    download(paramContext, paramString, paramImageView, 2130837640, false);
  }

	public static void download(Context paramContext, String paramString,
			ImageView paramImageView, int paramInt) {
		Bitmap localBitmap = CacheManager.getInstance().getDrawableFromCache(
				paramContext, paramString);
		if (localBitmap != null)
			paramImageView.setImageBitmap(localBitmap);

		Drawable localDrawable1 = paramContext.getResources().getDrawable(
				paramInt);
		Drawable localDrawable2 = paramImageView.getDrawable();
		if (localDrawable2 != null)
			localDrawable2.setCallback(null);
		paramImageView.setImageDrawable(localDrawable1);
		if (Session.get(paramContext).isStopDownloadImage()) {
			paramImageView.setVisibility(8);
		} else {
			paramImageView.setVisibility(0);
			if (cancelPotentialBitmapDownload(paramString, paramImageView)) {
				DrawableDownloaderTask localDrawableDownloaderTask = new DrawableDownloaderTask(
						paramImageView);
				paramImageView.setImageDrawable(new DownloadedDrawable3(
						localDrawable1, localDrawableDownloaderTask));
				try {
					Object[] arrayOfObject = new Object[2];
					arrayOfObject[0] = paramContext;
					arrayOfObject[1] = paramString;
					localDrawableDownloaderTask.execute(arrayOfObject);
				} catch (RejectedExecutionException localRejectedExecutionException) {
					Utils.E("RejectedExecutionException when download image",
							localRejectedExecutionException);
				}
			}
		}
	}

	public static void download(Context paramContext, String paramString,
			ImageView paramImageView, int paramInt, boolean paramBoolean) {
		CacheManager localCacheManager = CacheManager.getInstance();
		Bitmap localBitmap;
		if (paramImageView.getId() == 2131492878) {
			localBitmap = localCacheManager.getDrawableFromCache(paramContext,
					paramString + String.valueOf(2131492878));
		}
		else if (paramImageView.getId() == 2131492971) {
			localBitmap = localCacheManager.getDrawableFromCache(paramContext,
					paramString + String.valueOf(2131492971));
		} else
			localBitmap = localCacheManager.getDrawableFromCache(paramContext,
					paramString);
		if (localBitmap != null)
			paramImageView.setImageBitmap(localBitmap);

		Drawable localDrawable1 = paramContext.getResources().getDrawable(
				paramInt);
		Drawable localDrawable2 = paramImageView.getDrawable();
		if (localDrawable2 != null)
			localDrawable2.setCallback(null);
		paramImageView.setImageDrawable(localDrawable1);
		if (Session.get(paramContext).isStopDownloadImage()) {
			if (paramBoolean) {
				paramImageView.setVisibility(0);
				paramImageView.setImageResource(paramInt);
			} else {
				paramImageView.setVisibility(8);
			}
		} else {
			paramImageView.setVisibility(0);
			if (cancelPotentialBitmapDownload(paramString, paramImageView)) {
				BitmapDownloaderTask localBitmapDownloaderTask = new BitmapDownloaderTask(
						paramImageView);
				paramImageView.setImageDrawable(new DownloadedDrawable1(
						localDrawable1, localBitmapDownloaderTask));
				try {
					Object[] arrayOfObject = new Object[3];
					arrayOfObject[0] = paramContext;
					arrayOfObject[1] = paramString;
					arrayOfObject[2] = Integer.valueOf(1);
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
			arrayOfObject[2] = Integer.valueOf(3);
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
							2130837550));
			if (cancelPotentialBitmapDownload(paramString, paramImageView)) {
				BitmapDownloaderTask localBitmapDownloaderTask = new BitmapDownloaderTask(
						paramImageView);
				paramImageView.setImageDrawable(new DownloadedDrawable1(
						localBitmapDrawable2, localBitmapDownloaderTask));
				Object[] arrayOfObject = new Object[3];
				arrayOfObject[0] = paramContext;
				arrayOfObject[1] = paramString;
				arrayOfObject[2] = Integer.valueOf(4);
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
			arrayOfObject[2] = Integer.valueOf(2);
			localBitmapDownloaderTask.execute(arrayOfObject);
		}
	}

	private static BitmapDownloaderTask getBitmapDownloaderTask1(ImageView imageview)
    {
        BitmapDownloaderTask bitmapdownloadertask;
        if(imageview != null)
        {
            Drawable drawable = imageview.getDrawable();
            if(drawable == null || !(drawable instanceof DownloadedDrawable1))
                bitmapdownloadertask = null;
            else
                bitmapdownloadertask = ((DownloadedDrawable1)drawable).getBitmapDownloaderTask();
        } else
        {
            bitmapdownloadertask = null;
        }
        return bitmapdownloadertask;
    }

	private static BitmapDownloaderTask getBitmapDownloaderTask2(ImageView imageview)
    {
        BitmapDownloaderTask bitmapdownloadertask;
        if(imageview != null)
        {
            Drawable drawable = imageview.getBackground();
            if(drawable == null || !(drawable instanceof DownloadedDrawable2))
                bitmapdownloadertask = null;
            else
                bitmapdownloadertask = ((DownloadedDrawable2)drawable).getBitmapDownloaderTask();
        } else
        {
            bitmapdownloadertask = null;
        }
        return bitmapdownloadertask;
    }

	private static DrawableDownloaderTask getBitmapDownloaderTask3(ImageView imageview)
    {
        DrawableDownloaderTask drawabledownloadertask;
        if(imageview != null)
        {
            Drawable drawable = imageview.getDrawable();
            if(drawable == null || !(drawable instanceof DownloadedDrawable3))
                drawabledownloadertask = null;
            else
                drawabledownloadertask = ((DownloadedDrawable3)drawable).getBitmapDownloaderTask();
        } else
        {
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

  public static Bitmap getImageFromUrl(Context paramContext, String paramString, boolean paramBoolean)
  {
		HttpGet httpget;
		AndroidHttpClient androidhttpclient;
		httpget = new HttpGet(paramString);
		androidhttpclient = HttpClientFactory.get().getHttpClient();
		HttpResponse httpresponse = null;
		int i = 0;
		try {
			httpresponse = androidhttpclient.execute(httpget);
			i = httpresponse.getStatusLine().getStatusCode();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Utils.W((new StringBuilder()).append("IOException ")
					.append(paramString).toString(), e);
		}

		if ((i != 200)||(null==httpresponse)) {
			Utils.W((new StringBuilder()).append("Error ").append(i)
					.append(" while retrieving bitmap from ")
					.append(paramString).toString());
			httpget.abort();
			return null;
		}
		
		HttpEntity httpentity = httpresponse.getEntity();
        if(httpentity == null) {
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
        if(inputstream1 == null) {
        	httpget.abort();
        	return null;
        }
        
        Bitmap bitmap;
        android.graphics.BitmapFactory.Options options = new android.graphics.BitmapFactory.Options();
        options.inPurgeable = true;
        if(paramBoolean)
            options.inSampleSize = 2;
        bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputstream1), null, options);
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

  public static StateListDrawable getMaskDrawable(Context paramContext)
  {
    StateListDrawable localStateListDrawable = new StateListDrawable();
    Resources localResources = paramContext.getResources();
    int[] arrayOfInt = new int[1];
    arrayOfInt[0] = 16842919;
    localStateListDrawable.addState(arrayOfInt, new BitmapDrawable(localResources, BitmapFactory.decodeResource(localResources, 2130837551)));
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

	public static void saveBitmapToSdcard(Context context, int i, Bitmap bitmap) {
		File file;
		FileOutputStream fileoutputstream = null;
		file = context.getCacheDir();

		try {
			fileoutputstream = new FileOutputStream(new File(file,
					String.valueOf(i)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Utils.E("Error when save bitmap to sdcard", e);
			return;
		}

		bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100,
				fileoutputstream);

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

  static class BitmapDownloaderTask extends AsyncTaskEx<Object, Void, Bitmap>
  {
    CacheManager cache = CacheManager.getInstance();
    private Context context;
    private final WeakReference<ImageView> imageViewReference;
    private int type;
    private String url;

    public BitmapDownloaderTask(ImageView paramImageView)
    {
      this.imageViewReference = new WeakReference(paramImageView);
    }

    protected Bitmap doInBackground(Object[] paramArrayOfObject)
    {
    	Bitmap localBitmap = null;
      this.context = ((Context)paramArrayOfObject[0]);
      this.url = ((String)paramArrayOfObject[1]);
      this.type = ((Integer)paramArrayOfObject[2]).intValue();
      String str = this.url;
      if (this.imageViewReference != null)
      {
    	ImageView localImageView = (ImageView)this.imageViewReference.get();
        if (localImageView != null)
        {
          if (localImageView.getId() == 2131492878)
        	  str = this.url + String.valueOf(2131492878);
          else if (localImageView.getId() == 2131492971)
        	  str = this.url + String.valueOf(2131492971);
        }
        
        if (this.cache.existsDrawable(str)) {
      	  Object localObject = ImageUtils.getBitmapFromSdcard(this.context, str);
            if (localObject == null)
            {
          	  if (this.type == 3)
          		  localBitmap = ImageUtils.getImageFromUrl(this.context, this.url, true);
          	  else
          		localBitmap = ImageUtils.getImageFromUrl(this.context, this.url, false);
            }
            
            if ((localImageView != null) && (localBitmap != null))
            {
              if (localImageView.getId() == 2131492878)
            	  localBitmap = ImageUtils.createUserIcon(this.context, localBitmap);
              else if (localImageView.getId() == 2131492971)
            	  localBitmap = ImageUtils.createHomeUserIcon(this.context, localBitmap);
            }
            
            if ((localImageView != null) && (localBitmap != null))
            {
              int i = localImageView.getId();
              if ((i == 2131492878) || (i == 2131492971))
              {
                this.cache.cacheDrawable(this.context, str, localBitmap);
              }
            }
        }
      }
      else
      {
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

				if (this.type == 2) {
					localBitmapDownloaderTask = ImageUtils
							.getBitmapDownloaderTask2(localImageView);
				} else
					localBitmapDownloaderTask = ImageUtils
							.getBitmapDownloaderTask1(localImageView);
				if (this.type == 2) {
					localImageView.setBackgroundDrawable(null);
					localImageView.setImageBitmap(ImageUtils.scaleBitmap(
							this.context, ImageUtils.rotateImage(paramBitmap)));
				} else if (this.type == 3) {
					localImageView.setImageBitmap(ImageUtils
							.rotateImage(paramBitmap));
				} else if (this.type == 4) {
					BitmapDrawable localBitmapDrawable = new BitmapDrawable(
							paramBitmap);
					localImageView.setImageDrawable(ImageUtils
							.getMaskDrawable(this.context));
					localImageView.setBackgroundDrawable(localBitmapDrawable);
				} else {
					localImageView.setImageBitmap(paramBitmap);
				}

				localImageView.startAnimation(AnimationUtils.loadAnimation(
						this.context, 2130968577));
			}
		}
	}

  static class DownloadedDrawable1 extends BitmapDrawable
  {
    private final WeakReference<ImageUtils.BitmapDownloaderTask> bitmapDownloaderTaskReference1;

    public DownloadedDrawable1(Drawable paramDrawable, ImageUtils.BitmapDownloaderTask paramBitmapDownloaderTask)
    {
      super();
      this.bitmapDownloaderTaskReference1 = new WeakReference(paramBitmapDownloaderTask);
    }

    public ImageUtils.BitmapDownloaderTask getBitmapDownloaderTask()
    {
      return (ImageUtils.BitmapDownloaderTask)this.bitmapDownloaderTaskReference1.get();
    }
  }

  static class DownloadedDrawable2 extends AnimationDrawable
  {
    private final WeakReference<ImageUtils.BitmapDownloaderTask> bitmapDownloaderTaskReference2;

    public DownloadedDrawable2(Drawable paramDrawable, ImageUtils.BitmapDownloaderTask paramBitmapDownloaderTask)
    {
      AnimationDrawable localAnimationDrawable = (AnimationDrawable)paramDrawable;
      int i = localAnimationDrawable.getNumberOfFrames();
      for (int j = 0; j < i; j++)
        super.addFrame(localAnimationDrawable.getFrame(j), localAnimationDrawable.getDuration(j));
      super.setOneShot(false);
      this.bitmapDownloaderTaskReference2 = new WeakReference(paramBitmapDownloaderTask);
    }

    public ImageUtils.BitmapDownloaderTask getBitmapDownloaderTask()
    {
      return (ImageUtils.BitmapDownloaderTask)this.bitmapDownloaderTaskReference2.get();
    }
  }

  static class DownloadedDrawable3 extends BitmapDrawable
  {
    private final WeakReference<ImageUtils.DrawableDownloaderTask> bitmapDownloaderTaskReference3;

    public DownloadedDrawable3(Drawable paramDrawable, ImageUtils.DrawableDownloaderTask paramDrawableDownloaderTask)
    {
      super();
      this.bitmapDownloaderTaskReference3 = new WeakReference(paramDrawableDownloaderTask);
    }

    public ImageUtils.DrawableDownloaderTask getBitmapDownloaderTask()
    {
      return (ImageUtils.DrawableDownloaderTask)this.bitmapDownloaderTaskReference3.get();
    }
  }

  static class DrawableDownloaderTask extends AsyncTaskEx<Object, Void, Drawable>
  {
    CacheManager cache = CacheManager.getInstance();
    private Context context;
    private final WeakReference<ImageView> imageViewReference;

    public DrawableDownloaderTask(ImageView paramImageView)
    {
      this.imageViewReference = new WeakReference(paramImageView);
    }

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