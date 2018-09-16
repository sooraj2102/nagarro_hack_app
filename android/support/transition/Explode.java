package android.support.transition;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import java.util.Map;

public class Explode extends Visibility
{
  private static final String PROPNAME_SCREEN_BOUNDS = "android:explode:screenBounds";
  private static final TimeInterpolator sAccelerate;
  private static final TimeInterpolator sDecelerate = new DecelerateInterpolator();
  private int[] mTempLoc = new int[2];

  static
  {
    sAccelerate = new AccelerateInterpolator();
  }

  public Explode()
  {
    setPropagation(new CircularPropagation());
  }

  public Explode(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setPropagation(new CircularPropagation());
  }

  private static float calculateDistance(float paramFloat1, float paramFloat2)
  {
    return (float)Math.sqrt(paramFloat1 * paramFloat1 + paramFloat2 * paramFloat2);
  }

  private static float calculateMaxDistance(View paramView, int paramInt1, int paramInt2)
  {
    int i = Math.max(paramInt1, paramView.getWidth() - paramInt1);
    int j = Math.max(paramInt2, paramView.getHeight() - paramInt2);
    return calculateDistance(i, j);
  }

  private void calculateOut(View paramView, Rect paramRect, int[] paramArrayOfInt)
  {
    paramView.getLocationOnScreen(this.mTempLoc);
    int i = this.mTempLoc[0];
    int j = this.mTempLoc[1];
    Rect localRect = getEpicenter();
    int k;
    if (localRect == null)
      k = i + paramView.getWidth() / 2 + Math.round(paramView.getTranslationX());
    for (int m = j + paramView.getHeight() / 2 + Math.round(paramView.getTranslationY()); ; m = localRect.centerY())
    {
      int n = paramRect.centerX();
      int i1 = paramRect.centerY();
      float f1 = n - k;
      float f2 = i1 - m;
      if ((f1 == 0.0F) && (f2 == 0.0F))
      {
        f1 = (float)(2.0D * Math.random()) - 1.0F;
        f2 = (float)(2.0D * Math.random()) - 1.0F;
      }
      float f3 = calculateDistance(f1, f2);
      float f4 = f1 / f3;
      float f5 = f2 / f3;
      float f6 = calculateMaxDistance(paramView, k - i, m - j);
      paramArrayOfInt[0] = Math.round(f6 * f4);
      paramArrayOfInt[1] = Math.round(f6 * f5);
      return;
      k = localRect.centerX();
    }
  }

  private void captureValues(TransitionValues paramTransitionValues)
  {
    View localView = paramTransitionValues.view;
    localView.getLocationOnScreen(this.mTempLoc);
    int i = this.mTempLoc[0];
    int j = this.mTempLoc[1];
    int k = i + localView.getWidth();
    int m = j + localView.getHeight();
    paramTransitionValues.values.put("android:explode:screenBounds", new Rect(i, j, k, m));
  }

  public void captureEndValues(@NonNull TransitionValues paramTransitionValues)
  {
    super.captureEndValues(paramTransitionValues);
    captureValues(paramTransitionValues);
  }

  public void captureStartValues(@NonNull TransitionValues paramTransitionValues)
  {
    super.captureStartValues(paramTransitionValues);
    captureValues(paramTransitionValues);
  }

  public Animator onAppear(ViewGroup paramViewGroup, View paramView, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    if (paramTransitionValues2 == null)
      return null;
    Rect localRect = (Rect)paramTransitionValues2.values.get("android:explode:screenBounds");
    float f1 = paramView.getTranslationX();
    float f2 = paramView.getTranslationY();
    calculateOut(paramViewGroup, localRect, this.mTempLoc);
    float f3 = f1 + this.mTempLoc[0];
    float f4 = f2 + this.mTempLoc[1];
    return TranslationAnimationCreator.createAnimation(paramView, paramTransitionValues2, localRect.left, localRect.top, f3, f4, f1, f2, sDecelerate);
  }

  public Animator onDisappear(ViewGroup paramViewGroup, View paramView, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    if (paramTransitionValues1 == null)
      return null;
    Rect localRect = (Rect)paramTransitionValues1.values.get("android:explode:screenBounds");
    int i = localRect.left;
    int j = localRect.top;
    float f1 = paramView.getTranslationX();
    float f2 = paramView.getTranslationY();
    float f3 = f1;
    float f4 = f2;
    int[] arrayOfInt = (int[])(int[])paramTransitionValues1.view.getTag(R.id.transition_position);
    if (arrayOfInt != null)
    {
      f3 += arrayOfInt[0] - localRect.left;
      f4 += arrayOfInt[1] - localRect.top;
      localRect.offsetTo(arrayOfInt[0], arrayOfInt[1]);
    }
    calculateOut(paramViewGroup, localRect, this.mTempLoc);
    return TranslationAnimationCreator.createAnimation(paramView, paramTransitionValues1, i, j, f1, f2, f3 + this.mTempLoc[0], f4 + this.mTempLoc[1], sAccelerate);
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.Explode
 * JD-Core Version:    0.6.0
 */