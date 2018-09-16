package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;

public class Fade extends Visibility
{
  public static final int IN = 1;
  private static final String LOG_TAG = "Fade";
  public static final int OUT = 2;
  private static final String PROPNAME_TRANSITION_ALPHA = "android:fade:transitionAlpha";

  public Fade()
  {
  }

  public Fade(int paramInt)
  {
    setMode(paramInt);
  }

  public Fade(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, Styleable.FADE);
    setMode(TypedArrayUtils.getNamedInt(localTypedArray, (XmlResourceParser)paramAttributeSet, "fadingMode", 0, getMode()));
    localTypedArray.recycle();
  }

  private Animator createAnimation(View paramView, float paramFloat1, float paramFloat2)
  {
    if (paramFloat1 == paramFloat2)
      return null;
    ViewUtils.setTransitionAlpha(paramView, paramFloat1);
    ObjectAnimator localObjectAnimator = ObjectAnimator.ofFloat(paramView, ViewUtils.TRANSITION_ALPHA, new float[] { paramFloat2 });
    localObjectAnimator.addListener(new FadeAnimatorListener(paramView));
    addListener(new TransitionListenerAdapter(paramView)
    {
      public void onTransitionEnd(@NonNull Transition paramTransition)
      {
        ViewUtils.setTransitionAlpha(this.val$view, 1.0F);
        ViewUtils.clearNonTransitionAlpha(this.val$view);
        paramTransition.removeListener(this);
      }
    });
    return localObjectAnimator;
  }

  private static float getStartAlpha(TransitionValues paramTransitionValues, float paramFloat)
  {
    float f = paramFloat;
    if (paramTransitionValues != null)
    {
      Float localFloat = (Float)paramTransitionValues.values.get("android:fade:transitionAlpha");
      if (localFloat != null)
        f = localFloat.floatValue();
    }
    return f;
  }

  public void captureStartValues(@NonNull TransitionValues paramTransitionValues)
  {
    super.captureStartValues(paramTransitionValues);
    paramTransitionValues.values.put("android:fade:transitionAlpha", Float.valueOf(ViewUtils.getTransitionAlpha(paramTransitionValues.view)));
  }

  public Animator onAppear(ViewGroup paramViewGroup, View paramView, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    float f = getStartAlpha(paramTransitionValues1, 0.0F);
    if (f == 1.0F)
      f = 0.0F;
    return createAnimation(paramView, f, 1.0F);
  }

  public Animator onDisappear(ViewGroup paramViewGroup, View paramView, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    ViewUtils.saveNonTransitionAlpha(paramView);
    return createAnimation(paramView, getStartAlpha(paramTransitionValues1, 1.0F), 0.0F);
  }

  private static class FadeAnimatorListener extends AnimatorListenerAdapter
  {
    private boolean mLayerTypeChanged = false;
    private final View mView;

    FadeAnimatorListener(View paramView)
    {
      this.mView = paramView;
    }

    public void onAnimationEnd(Animator paramAnimator)
    {
      ViewUtils.setTransitionAlpha(this.mView, 1.0F);
      if (this.mLayerTypeChanged)
        this.mView.setLayerType(0, null);
    }

    public void onAnimationStart(Animator paramAnimator)
    {
      if ((ViewCompat.hasOverlappingRendering(this.mView)) && (this.mView.getLayerType() == 0))
      {
        this.mLayerTypeChanged = true;
        this.mView.setLayerType(2, null);
      }
    }
  }
}

/* Location:           /home/satyam/AndroidStudioProjects/app/dex2jar-0.0.9.15/classes-dex2jar.jar
 * Qualified Name:     android.support.transition.Fade
 * JD-Core Version:    0.6.0
 */