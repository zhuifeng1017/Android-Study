package com.mappn.gfan.common.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import com.mappn.gfan.Session;

public class IndicatorDrawable extends View
{
  private static final int HEIGHT = 32;
  private static final int RADIUS_S = 4;
  private static final int REFRESH = 1;
  private int mCurrentLevel = 6;
  private int mCurrentPage = 0;
  private float mDensity;
  private Handler mHandler = new MyHandler(this);// = new IndicatorDrawable.1(this);
  private int mHighlightStartRadius;
  private int mNormalRadius;
  private int mPageNumber = 0;
  private Paint mPaint = new Paint(1);
  private int mStartPosX = 15;
  private int mStartPosY;
  private int mStepWidth;

  public IndicatorDrawable(Context paramContext)
  {
    super(paramContext);
  }

  public IndicatorDrawable(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public IndicatorDrawable(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }

  private void startAnimation()
  {
  //  new IndicatorDrawable.2(this).start();
  }

  public void nextPage()
  {
      mCurrentPage = 1 + mCurrentPage;
      if(mCurrentPage == mPageNumber)
      {
          mCurrentPage = mPageNumber - 1;
      } else
      {
          mCurrentLevel = 0;
          startAnimation();
      }
  }

  protected void onDraw(Canvas canvas)
  {
      super.onDraw(canvas);
      int i = mStartPosX;
      int j = 0;
      while(j < mPageNumber) 
      {
          if(j == mCurrentPage)
          {
              mPaint.setColor(-1);
              canvas.drawCircle(i, mStartPosY, mHighlightStartRadius + mCurrentLevel, mPaint);
          } else
          {
              mPaint.setColor(0xff888888);
              canvas.drawCircle(i, mStartPosY, mNormalRadius, mPaint);
          }
          i += mStepWidth;
          j++;
      }
      canvas.save();
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    int i = (int)(32.0F * this.mDensity);
    setMeasuredDimension(this.mPageNumber * (int)(14.0F * this.mDensity), i);
    this.mStartPosY = (i / 2);
    this.mNormalRadius = (int)(4.0F * this.mDensity);
    if (this.mDensity > 1.0F);
    for (this.mHighlightStartRadius = 2; ; this.mHighlightStartRadius = 0)
    {
      this.mStepWidth = (int)(13.0F * this.mDensity);
      return;
    }
  }

  public void previousPage()
  {
      mCurrentPage = mCurrentPage - 1;
      if(mCurrentPage < 0)
      {
          mCurrentPage = 0;
      } else
      {
          mCurrentLevel = 0;
          startAnimation();
      }
  }

  public void setPage(int i)
  {
      mCurrentPage = i;
      if(mCurrentPage < 0)
          mCurrentPage = 0;
      else
      if(mCurrentPage == mPageNumber)
      {
          mCurrentPage = mPageNumber - 1;
      } else
      {
          mCurrentLevel = 0;
          startAnimation();
      }
  }

  public void setPageNumber(int paramInt)
  {
    this.mDensity = Session.get(getContext()).density;
    this.mPageNumber = paramInt;
    requestLayout();
    invalidate();
  }
  
  class MyHandler extends Handler
  {
	  MyHandler(IndicatorDrawable paramIndicatorDrawable)
    {
    }

    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default:
    	  break;
      case 1:
//    	  IndicatorDrawable.access$002(this.this$0, paramMessage.arg1);
//          this.this$0.invalidate();
    	  break;
      }
    }
  }
}