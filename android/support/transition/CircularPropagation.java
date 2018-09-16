package android.support.transition;

import android.graphics.Rect;
import android.view.ViewGroup;

public class CircularPropagation extends VisibilityPropagation
{
  private float mPropagationSpeed = 3.0F;

  private static float distance(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    float f1 = paramFloat3 - paramFloat1;
    float f2 = paramFloat4 - paramFloat2;
    return (float)Math.sqrt(f1 * f1 + f2 * f2);
  }

  public long getStartDelay(ViewGroup paramViewGroup, Transition paramTransition, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    if ((paramTransitionValues1 == null) && (paramTransitionValues2 == null))
      return 0L;
    int i = 1;
    TransitionValues localTransitionValues;
    int j;
    int k;
    Rect localRect;
    int m;
    if ((paramTransitionValues2 == null) || (getViewVisibility(paramTransitionValues1) == 0))
    {
      localTransitionValues = paramTransitionValues1;
      i = -1;
      j = getViewX(localTransitionValues);
      k = getViewY(localTransitionValues);
      localRect = paramTransition.getEpicenter();
      if (localRect == null)
        break label152;
      m = localRect.centerX();
    }
    label152: int[] arrayOfInt;
    int i2;
    for (int i1 = localRect.centerY(); ; i2 = Math.round(arrayOfInt[1] + paramViewGroup.getHeight() / 2 + paramViewGroup.getTranslationY()))
    {
      float f = distance(j, k, m, i1) / distance(0.0F, 0.0F, paramViewGroup.getWidth(), paramViewGroup.getHeight());
      long l = paramTransition.getDuration();
      if (l < 0L)
        l = 300L;
      return Math.round(f * ((float)(l * i) / this.mPropagationSpeed));
      localTransitionValues = paramTransitionValues2;
      break;
      arrayOfInt = new int[2];
      paramViewGroup.getLocationOnScreen(arrayOfInt);
      int n = Math.round(arrayOfInt[0] + paramViewGroup.getWidth() / 2 + paramViewGroup.getTranslationX());
    }
  }

  public void setPropagationSpeed(float paramFloat)
  {
    if (paramFloat == 0.0F)
      throw new IllegalArgumentException("propagationSpeed may not be 0");
    this.mPropagationSpeed = paramFloat;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.CircularPropagation
 * JD-Core Version:    0.6.0
 */