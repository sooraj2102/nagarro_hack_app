package me.zhanghai.android.materialprogressbar;

import android.content.Context;
import android.graphics.Canvas;

class HorizontalProgressBackgroundDrawable extends BaseSingleHorizontalProgressDrawable
  implements ShowBackgroundDrawable
{
  private boolean mShow = true;

  public HorizontalProgressBackgroundDrawable(Context paramContext)
  {
    super(paramContext);
  }

  public void draw(Canvas paramCanvas)
  {
    if (this.mShow)
      super.draw(paramCanvas);
  }

  public boolean getShowBackground()
  {
    return this.mShow;
  }

  public void setShowBackground(boolean paramBoolean)
  {
    if (this.mShow != paramBoolean)
    {
      this.mShow = paramBoolean;
      invalidateSelf();
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     me.zhanghai.android.materialprogressbar.HorizontalProgressBackgroundDrawable
 * JD-Core Version:    0.6.0
 */