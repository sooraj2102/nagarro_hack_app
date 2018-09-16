package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;

public class ChangeClipBounds extends Transition
{
  private static final String PROPNAME_BOUNDS = "android:clipBounds:bounds";
  private static final String PROPNAME_CLIP = "android:clipBounds:clip";
  private static final String[] sTransitionProperties = { "android:clipBounds:clip" };

  public ChangeClipBounds()
  {
  }

  public ChangeClipBounds(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  private void captureValues(TransitionValues paramTransitionValues)
  {
    View localView = paramTransitionValues.view;
    if (localView.getVisibility() == 8);
    Rect localRect1;
    do
    {
      return;
      localRect1 = ViewCompat.getClipBounds(localView);
      paramTransitionValues.values.put("android:clipBounds:clip", localRect1);
    }
    while (localRect1 != null);
    Rect localRect2 = new Rect(0, 0, localView.getWidth(), localView.getHeight());
    paramTransitionValues.values.put("android:clipBounds:bounds", localRect2);
  }

  public void captureEndValues(@NonNull TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }

  public void captureStartValues(@NonNull TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }

  public Animator createAnimator(@NonNull ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    ObjectAnimator localObjectAnimator = null;
    if (paramTransitionValues1 != null)
    {
      localObjectAnimator = null;
      if (paramTransitionValues2 != null)
      {
        boolean bool1 = paramTransitionValues1.values.containsKey("android:clipBounds:clip");
        localObjectAnimator = null;
        if (bool1)
        {
          boolean bool2 = paramTransitionValues2.values.containsKey("android:clipBounds:clip");
          localObjectAnimator = null;
          if (bool2)
            break label59;
        }
      }
    }
    label59: Rect localRect1;
    Rect localRect2;
    int i;
    while (true)
    {
      return localObjectAnimator;
      localRect1 = (Rect)paramTransitionValues1.values.get("android:clipBounds:clip");
      localRect2 = (Rect)paramTransitionValues2.values.get("android:clipBounds:clip");
      if (localRect2 != null)
        break;
      i = 1;
      label99: if (localRect1 == null)
      {
        localObjectAnimator = null;
        if (localRect2 == null)
          continue;
      }
      if (localRect1 != null)
        break label234;
      localRect1 = (Rect)paramTransitionValues1.values.get("android:clipBounds:bounds");
    }
    while (true)
    {
      boolean bool3 = localRect1.equals(localRect2);
      localObjectAnimator = null;
      if (bool3)
        break;
      ViewCompat.setClipBounds(paramTransitionValues2.view, localRect1);
      RectEvaluator localRectEvaluator = new RectEvaluator(new Rect());
      localObjectAnimator = ObjectAnimator.ofObject(paramTransitionValues2.view, ViewUtils.CLIP_BOUNDS, localRectEvaluator, new Rect[] { localRect1, localRect2 });
      if (i == 0)
        break;
      localObjectAnimator.addListener(new AnimatorListenerAdapter(paramTransitionValues2.view)
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          ViewCompat.setClipBounds(this.val$endView, null);
        }
      });
      return localObjectAnimator;
      i = 0;
      break label99;
      label234: if (localRect2 != null)
        continue;
      localRect2 = (Rect)paramTransitionValues2.values.get("android:clipBounds:bounds");
    }
  }

  public String[] getTransitionProperties()
  {
    return sTransitionProperties;
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.ChangeClipBounds
 * JD-Core Version:    0.6.0
 */