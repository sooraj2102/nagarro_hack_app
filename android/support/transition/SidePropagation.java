package android.support.transition;

import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;

public class SidePropagation extends VisibilityPropagation
{
  private float mPropagationSpeed = 3.0F;
  private int mSide = 80;

  private int distance(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
  {
    int k;
    label20: int i;
    if (this.mSide == 8388611)
      if (ViewCompat.getLayoutDirection(paramView) == 1)
      {
        k = 1;
        if (k == 0)
          break label80;
        i = 5;
      }
    while (true)
      switch (i)
      {
      default:
        return 0;
        k = 0;
        break label20;
        label80: i = 3;
        continue;
        if (this.mSide == 8388613)
        {
          int j;
          if (ViewCompat.getLayoutDirection(paramView) == 1)
          {
            j = 1;
            label106: if (j == 0)
              break label123;
          }
          label123: for (i = 3; ; i = 5)
          {
            break;
            j = 0;
            break label106;
          }
        }
        i = this.mSide;
      case 3:
      case 48:
      case 5:
      case 80:
      }
    return paramInt7 - paramInt1 + Math.abs(paramInt4 - paramInt2);
    return paramInt8 - paramInt2 + Math.abs(paramInt3 - paramInt1);
    return paramInt1 - paramInt5 + Math.abs(paramInt4 - paramInt2);
    return paramInt2 - paramInt6 + Math.abs(paramInt3 - paramInt1);
  }

  private int getMaxDistance(ViewGroup paramViewGroup)
  {
    switch (this.mSide)
    {
    default:
      return paramViewGroup.getHeight();
    case 3:
    case 5:
    case 8388611:
    case 8388613:
    }
    return paramViewGroup.getWidth();
  }

  public long getStartDelay(ViewGroup paramViewGroup, Transition paramTransition, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    if ((paramTransitionValues1 == null) && (paramTransitionValues2 == null))
      return 0L;
    int i = 1;
    Rect localRect = paramTransition.getEpicenter();
    TransitionValues localTransitionValues;
    int j;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    int i3;
    if ((paramTransitionValues2 == null) || (getViewVisibility(paramTransitionValues1) == 0))
    {
      localTransitionValues = paramTransitionValues1;
      i = -1;
      j = getViewX(localTransitionValues);
      k = getViewY(localTransitionValues);
      int[] arrayOfInt = new int[2];
      paramViewGroup.getLocationOnScreen(arrayOfInt);
      m = arrayOfInt[0] + Math.round(paramViewGroup.getTranslationX());
      n = arrayOfInt[1] + Math.round(paramViewGroup.getTranslationY());
      i1 = m + paramViewGroup.getWidth();
      i2 = n + paramViewGroup.getHeight();
      if (localRect == null)
        break label207;
      i3 = localRect.centerX();
    }
    for (int i4 = localRect.centerY(); ; i4 = (n + i2) / 2)
    {
      float f = distance(paramViewGroup, j, k, i3, i4, m, n, i1, i2) / getMaxDistance(paramViewGroup);
      long l = paramTransition.getDuration();
      if (l < 0L)
        l = 300L;
      return Math.round(f * ((float)(l * i) / this.mPropagationSpeed));
      localTransitionValues = paramTransitionValues2;
      break;
      label207: i3 = (m + i1) / 2;
    }
  }

  public void setPropagationSpeed(float paramFloat)
  {
    if (paramFloat == 0.0F)
      throw new IllegalArgumentException("propagationSpeed may not be 0");
    this.mPropagationSpeed = paramFloat;
  }

  public void setSide(int paramInt)
  {
    this.mSide = paramInt;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.SidePropagation
 * JD-Core Version:    0.6.0
 */