package com.mappn.gfan.common.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.mappn.gfan.Session;
import com.mappn.gfan.common.util.ImageUtils;
import com.mappn.gfan.common.util.Utils;
import java.io.File;

public class SplashView extends RelativeLayout
  implements View.OnClickListener
{
  private static final int DURATION = 1000;
  private static final int HEIGHT = 250;
  private static final int HIDE_HEIGHT = 90;
  private final int TITLE_HEIGHT = 32;
  private boolean isInit;
  private boolean isShow;
  private Animation.AnimationListener mAnimationListener = new AnimationListener()
  {
	  public void onAnimationEnd(Animation paramAnimation)
	  {
		  isShow = true;
//	    SplashView.access$002(this.this$0, true);
	  }

	  public void onAnimationRepeat(Animation paramAnimation)
	  {
	  }

	  public void onAnimationStart(Animation paramAnimation)
	  {
		  isShow = false;
//	    SplashView.access$002(this.this$0, false);
	  }
  };
  private ImageView mBackground;
  private Animation mBackgroundHideAnimation;
  private Animation mBackgroundShowAnimation;
  private float mDensity = getContext().getResources().getDisplayMetrics().density;
  private Animation mFooterHideAnimation;
  private Animation mFooterShowAnimation;
  private View mFooterView;
  private int mHeight;
  private boolean mIsAnimationEnd = true;
  private int mSmallViewHeigh = (int)(250.0F * this.mDensity);
  private TextView mTitle;
  private int mTitleHeight = (int)(32.0F * this.mDensity);
  private Animation mTitleHideAnimation;
  private Animation mTitleShowAnimation;

  public SplashView(Context paramContext, View paramView)
  {
    super(paramContext);
    RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-1, -2);
    localLayoutParams.topMargin = this.mSmallViewHeigh;
    this.mFooterView = paramView;
    this.mFooterView.setLayoutParams(localLayoutParams);
  }

  private ImageView createBackgroundView()
  {
	Bitmap localBitmap = null;
    ImageView localImageView = new ImageView(getContext());
    RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-1, this.mHeight);
    localLayoutParams.topMargin = -90;
    localImageView.setLayoutParams(localLayoutParams);
    localImageView.setScaleType(ImageView.ScaleType.MATRIX);
    localImageView.setClickable(true);
    localImageView.setOnClickListener(this);
    File localFile = new File(getContext().getFilesDir(), "splash.png");
    if (localFile.exists())
    {
      try
      {
    	  localBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
      }
      catch (OutOfMemoryError localOutOfMemoryError)
      {
        Utils.E("initSplashBg OutOfMemoryError", localOutOfMemoryError);
      }
    }
    else {
    	localBitmap = BitmapFactory.decodeResource(getResources(), 2130837762);
    }
    
    localImageView.setImageBitmap(ImageUtils.scaleBitmap(getContext(), localBitmap));
    return localImageView;
    }


  private TextView createTitleView()
  {
    TextView localTextView = new TextView(getContext());
    RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-1, this.mTitleHeight);
    localLayoutParams.topMargin = (this.mSmallViewHeigh - this.mTitleHeight);
    localTextView.setLayoutParams(localLayoutParams);
    localTextView.setText(Session.get(getContext()).getVersionName());
    localTextView.setGravity(19);
    localTextView.setBackgroundResource(2130837758);
    return localTextView;
  }

  private void ensureLayoutParams()
  {
    this.mBackgroundShowAnimation = new TranslateAnimation(0.0F, 0.0F, 0.0F, 90.0F);
    this.mBackgroundShowAnimation.setDuration(1000L);
    this.mBackgroundShowAnimation.setFillAfter(true);
    this.mBackgroundHideAnimation = new TranslateAnimation(0.0F, 0.0F, 90.0F, 0.0F);
    this.mBackgroundHideAnimation.setDuration(1000L);
    this.mBackgroundHideAnimation.setFillAfter(true);
    int i = this.mHeight - this.mSmallViewHeigh;
    this.mTitleShowAnimation = new TranslateAnimation(0.0F, 0.0F, 0.0F, i);
    this.mTitleShowAnimation.setDuration(1000L);
    this.mTitleShowAnimation.setFillAfter(true);
    this.mTitleHideAnimation = new TranslateAnimation(0.0F, 0.0F, i, 0.0F);
    this.mTitleHideAnimation.setDuration(1000L);
    this.mTitleHideAnimation.setFillAfter(true);
    this.mFooterShowAnimation = new TranslateAnimation(0.0F, 0.0F, 0.0F, i);
    this.mFooterShowAnimation.setDuration(1000L);
    this.mFooterShowAnimation.setFillAfter(true);
    this.mFooterShowAnimation.setAnimationListener(this.mAnimationListener);
    this.mFooterHideAnimation = new TranslateAnimation(0.0F, 0.0F, i, 0.0F);
    this.mFooterHideAnimation.setDuration(1000L);
    this.mFooterHideAnimation.setFillAfter(true);
    this.mFooterHideAnimation.setAnimationListener(this.mAnimationListener);
  }

  public void init()
  {
    this.mTitle = createTitleView();
    this.mBackground = createBackgroundView();
    ImageView localImageView = new ImageView(getContext());
    RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-1, (int)(4.0F * this.mDensity));
    localLayoutParams.addRule(10);
    localImageView.setLayoutParams(localLayoutParams);
    localImageView.setBackgroundResource(2130837799);
    addView(this.mBackground);
    addView(this.mTitle);
    addView(localImageView);
  }

  public void onClick(View view)
  {
      if(mIsAnimationEnd)
      {
          boolean flag;
          if(isShow)
          {
              mBackground.startAnimation(mBackgroundHideAnimation);
              mTitle.startAnimation(mTitleHideAnimation);
              mFooterView.startAnimation(mFooterHideAnimation);
          } else
          {
              mBackground.startAnimation(mBackgroundShowAnimation);
              mTitle.startAnimation(mTitleShowAnimation);
              mFooterView.startAnimation(mFooterShowAnimation);
          }
          if(!isShow)
              flag = true;
          else
              flag = false;
          isShow = flag;
      }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    this.mHeight = View.MeasureSpec.getSize(paramInt2);
    ensureLayoutParams();
    if ((!this.isInit) && (this.mHeight > 0))
    {
      init();
      addView(this.mFooterView);
      this.isInit = true;
    }
  }

  public void setFooterView(View paramView)
  {
    RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-1, -1);
    localLayoutParams.topMargin = this.mSmallViewHeigh;
    paramView.setLayoutParams(localLayoutParams);
    paramView.setBackgroundColor(-16776961);
    this.mFooterView = paramView;
  }
}