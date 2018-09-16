package android.support.design.internal;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.transition.Transition;
import android.support.transition.TransitionValues;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Map;

@RequiresApi(14)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class TextScale extends Transition
{
  private static final String PROPNAME_SCALE = "android:textscale:scale";

  private void captureValues(TransitionValues paramTransitionValues)
  {
    if ((paramTransitionValues.view instanceof TextView))
    {
      TextView localTextView = (TextView)paramTransitionValues.view;
      paramTransitionValues.values.put("android:textscale:scale", Float.valueOf(localTextView.getScaleX()));
    }
  }

  public void captureEndValues(TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }

  public void captureStartValues(TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }

  public Animator createAnimator(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    if ((paramTransitionValues1 == null) || (paramTransitionValues2 == null) || (!(paramTransitionValues1.view instanceof TextView)) || (!(paramTransitionValues2.view instanceof TextView)));
    label159: label163: 
    while (true)
    {
      return null;
      TextView localTextView = (TextView)paramTransitionValues2.view;
      Map localMap1 = paramTransitionValues1.values;
      Map localMap2 = paramTransitionValues2.values;
      float f1;
      float f2;
      if (localMap1.get("android:textscale:scale") != null)
      {
        f1 = ((Float)localMap1.get("android:textscale:scale")).floatValue();
        if (localMap2.get("android:textscale:scale") == null)
          break label159;
        f2 = ((Float)localMap2.get("android:textscale:scale")).floatValue();
      }
      while (true)
      {
        if (f1 == f2)
          break label163;
        ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { f1, f2 });
        localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(localTextView)
        {
          public void onAnimationUpdate(ValueAnimator paramValueAnimator)
          {
            float f = ((Float)paramValueAnimator.getAnimatedValue()).floatValue();
            this.val$view.setScaleX(f);
            this.val$view.setScaleY(f);
          }
        });
        return localValueAnimator;
        f1 = 1.0F;
        break;
        f2 = 1.0F;
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.design.internal.TextScale
 * JD-Core Version:    0.6.0
 */