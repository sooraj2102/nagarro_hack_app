package me.zhanghai.android.materialprogressbar;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import me.zhanghai.android.materialprogressbar.internal.ThemeUtils;

abstract class BaseIndeterminateProgressDrawable extends BaseProgressDrawable
  implements Animatable
{
  protected Animator[] mAnimators;

  @SuppressLint({"NewApi"})
  public BaseIndeterminateProgressDrawable(Context paramContext)
  {
    setTint(ThemeUtils.getColorFromAttrRes(R.attr.colorControlActivated, -16777216, paramContext));
  }

  private boolean isStarted()
  {
    Animator[] arrayOfAnimator = this.mAnimators;
    int i = arrayOfAnimator.length;
    for (int j = 0; ; j++)
    {
      int k = 0;
      if (j < i)
      {
        if (!arrayOfAnimator[j].isStarted())
          continue;
        k = 1;
      }
      return k;
    }
  }

  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if (isStarted())
      invalidateSelf();
  }

  public boolean isRunning()
  {
    Animator[] arrayOfAnimator = this.mAnimators;
    int i = arrayOfAnimator.length;
    for (int j = 0; ; j++)
    {
      int k = 0;
      if (j < i)
      {
        if (!arrayOfAnimator[j].isRunning())
          continue;
        k = 1;
      }
      return k;
    }
  }

  public void start()
  {
    if (isStarted())
      return;
    Animator[] arrayOfAnimator = this.mAnimators;
    int i = arrayOfAnimator.length;
    for (int j = 0; j < i; j++)
      arrayOfAnimator[j].start();
    invalidateSelf();
  }

  public void stop()
  {
    Animator[] arrayOfAnimator = this.mAnimators;
    int i = arrayOfAnimator.length;
    for (int j = 0; j < i; j++)
      arrayOfAnimator[j].end();
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     me.zhanghai.android.materialprogressbar.BaseIndeterminateProgressDrawable
 * JD-Core Version:    0.6.0
 */