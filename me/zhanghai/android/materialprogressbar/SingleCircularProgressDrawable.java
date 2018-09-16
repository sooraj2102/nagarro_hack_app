package me.zhanghai.android.materialprogressbar;

import android.graphics.Canvas;
import android.graphics.Paint;

class SingleCircularProgressDrawable extends BaseSingleCircularProgressDrawable
  implements ShowBackgroundDrawable
{
  private static final int LEVEL_MAX = 10000;
  private static final float START_ANGLE_MAX_DYNAMIC = 360.0F;
  private static final float START_ANGLE_MAX_NORMAL = 0.0F;
  private static final float SWEEP_ANGLE_MAX = 360.0F;
  private boolean mShowBackground;
  private final float mStartAngleMax;

  SingleCircularProgressDrawable(int paramInt)
  {
    switch (paramInt)
    {
    default:
      throw new IllegalArgumentException("Invalid value for style");
    case 0:
      this.mStartAngleMax = 0.0F;
      return;
    case 1:
    }
    this.mStartAngleMax = 360.0F;
  }

  public boolean getShowBackground()
  {
    return this.mShowBackground;
  }

  protected void onDrawRing(Canvas paramCanvas, Paint paramPaint)
  {
    int i = getLevel();
    if (i == 0);
    float f2;
    float f3;
    do
    {
      return;
      float f1 = i / 10000.0F;
      f2 = f1 * this.mStartAngleMax;
      f3 = f1 * 360.0F;
      drawRing(paramCanvas, paramPaint, f2, f3);
    }
    while (!this.mShowBackground);
    drawRing(paramCanvas, paramPaint, f2, f3);
  }

  protected boolean onLevelChange(int paramInt)
  {
    invalidateSelf();
    return true;
  }

  public void setShowBackground(boolean paramBoolean)
  {
    if (this.mShowBackground != paramBoolean)
    {
      this.mShowBackground = paramBoolean;
      invalidateSelf();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     me.zhanghai.android.materialprogressbar.SingleCircularProgressDrawable
 * JD-Core Version:    0.6.0
 */