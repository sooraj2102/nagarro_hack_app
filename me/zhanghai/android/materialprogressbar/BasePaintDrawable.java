package me.zhanghai.android.materialprogressbar;

import android.graphics.Canvas;
import android.graphics.Paint;

abstract class BasePaintDrawable extends BaseDrawable
{
  private Paint mPaint;

  protected final void onDraw(Canvas paramCanvas, int paramInt1, int paramInt2)
  {
    if (this.mPaint == null)
    {
      this.mPaint = new Paint();
      this.mPaint.setAntiAlias(true);
      this.mPaint.setColor(-16777216);
      onPreparePaint(this.mPaint);
    }
    this.mPaint.setAlpha(this.mAlpha);
    this.mPaint.setColorFilter(getColorFilterForDrawing());
    onDraw(paramCanvas, paramInt1, paramInt2, this.mPaint);
  }

  protected abstract void onDraw(Canvas paramCanvas, int paramInt1, int paramInt2, Paint paramPaint);

  protected abstract void onPreparePaint(Paint paramPaint);
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     me.zhanghai.android.materialprogressbar.BasePaintDrawable
 * JD-Core Version:    0.6.0
 */